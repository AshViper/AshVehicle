package tech.lq0.ashvehicle.entity;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.projectile.*;
import com.atsuishio.superbwarfare.entity.vehicle.A10Entity;
import com.atsuishio.superbwarfare.entity.vehicle.base.*;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.*;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.lq0.ashvehicle.entity.weapon.Aam4Entity;
import tech.lq0.ashvehicle.entity.weapon.Aam4Weapon;
import tech.lq0.ashvehicle.init.ModEntities;
import tech.lq0.ashvehicle.init.ModNetwork;
import tech.lq0.ashvehicle.init.MultiLockTargetPacket;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Stream;

public class ZumwaltEntity extends ContainerMobileVehicleEntity implements GeoEntity, ArmedVehicleEntity, WeaponVehicleEntity, LandArmorEntity, OBBEntity {
    private final AnimatableInstanceCache cache;
    public OBB obb2;
    private final Map<UUID, Integer> lockTargets = new HashMap<>();
    protected final Set<UUID> lockedTargets = new HashSet<>();
    public int reloadCoolDownMissile;
    public boolean locked;
    public static final EntityDataAccessor<Integer> LOADED_MISSILE;

    public ZumwaltEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.ZYNWALT.get(), world);
    }

    public ZumwaltEntity(EntityType<ZumwaltEntity> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(10F, 5.0F, 80.0F), new Quaternionf(), OBB.Part.BODY);
    }

    static {
        LOADED_MISSILE = SynchedEntityData.defineId(A10Entity.class, EntityDataSerializers.INT);
    }

    @Override
    public void updateOBB() {
        Matrix4f transform = this.getVehicleTransform(1.0F);
        Vector4f worldPosition2 = this.transformPosition(transform, 0.0F, 1.5625F, 4.28125F);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1.0F, this));
    }

    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{
            (new ProjectileWeapon())
                    .damage((float)(Integer) VehicleConfig.HEAVY_MACHINE_GUN_DAMAGE.get())
                    .headShot(2.0F).zoom(false).icon(Mod.loc("textures/screens/vehicle_weapon/gun_12_7mm.png"))
                    .sound1p((SoundEvent) ModSounds.M_2_HB_FIRE_1P.get())
                    .sound3p((SoundEvent)ModSounds.M_2_HB_FIRE_3P.get())
                    .sound3pFar((SoundEvent)ModSounds.M_2_HB_FAR.get())
                    .sound3pVeryFar((SoundEvent)ModSounds.M_2_HB_VERYFAR.get()),
                (new Aam4Weapon()).sound((SoundEvent)ModSounds.INTO_MISSILE.get())
        }};
    }

    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition((double)50.0F, (double)20.0F, (double)0.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOADED_MISSILE, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedMissile", (Integer)this.entityData.get(LOADED_MISSILE));
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
    }

    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.8;
    }

    public void baseTick() {
        super.baseTick();
        this.updateOBB();
        for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(0.1))) {
            if (entity instanceof Player && entity.onGround()) {
                Vec3 delta = this.getDeltaMovement();
                entity.setDeltaMovement(entity.getDeltaMovement().add(delta.x, 0, delta.z)); // 一緒に動かす
            }
        }
        double fluidFloat = 0.12 * this.getSubmergedHeight(this);
        this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, fluidFloat, (double)0.0F));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.2, 0.99, 0.2));
        } else if (this.isInWater()) {
            float f = (float)((double)0.75F - (double)0.04F * Math.min(this.getSubmergedHeight(this), (double)this.getBbHeight()) + (double)(0.09F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F));
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).normalize().scale(0.04 * this.getDeltaMovement().dot(this.getViewVector(1.0F)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply((double)f, 0.85, (double)f));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.99, 0.99));
        }

        Level result = this.level();
        if (result instanceof ServerLevel serverLevel) {
            if (this.isInWater() && this.getDeltaMovement().length() > 0.1) {
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, this.getX() + (double)0.5F * this.getDeltaMovement().x, this.getY() + this.getSubmergedHeight(this) - 0.2, this.getZ() + (double)0.5F * this.getDeltaMovement().z, (int)((double)2.0F + (double)4.0F * this.getDeltaMovement().length()), 0.65, (double)0.0F, 0.65, (double)0.0F, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + (double)0.5F * this.getDeltaMovement().x, this.getY() + this.getSubmergedHeight(this) - 0.2, this.getZ() + (double)0.5F * this.getDeltaMovement().z, (int)((double)2.0F + (double)10.0F * this.getDeltaMovement().length()), 0.65, (double)0.0F, 0.65, (double)0.0F, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.BUBBLE_COLUMN_UP, this.getX() - (double)4.5F * this.getLookAngle().x, this.getY() - (double)0.25F, this.getZ() - (double)4.5F * this.getLookAngle().z, (int)(40.0F * Mth.abs((Float)this.entityData.get(POWER))), 0.15, 0.15, 0.15, 0.02, true);
            }
        }

        if (this.level() instanceof ServerLevel) {
            if (this.reloadCoolDownMissile > 0) --this.reloadCoolDownMissile;
            this.handleAmmo();
        }

        Entity var12 = this.getFirstPassenger();
        if (var12 instanceof Player player) {
            BlockHitResult result1 = player.level().clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getViewVector(1.0F).scale((double)512.0F)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            Entity lookingEntity = TraceTool.findLookingEntity(player, (double)520.0F);
            Matrix4f transform = this.getBarrelTransform(1.0F);
            Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.20106874F, 1.9117F);
            Vec3 shootPos = new Vec3((double)worldPosition.x + (double)0.5F * this.getDeltaMovement().x, (double)worldPosition.y, (double)worldPosition.z + (double)0.5F * this.getDeltaMovement().z);
            Vec3 hitPos;
            if (lookingEntity != null) {
                hitPos = TraceTool.playerFindLookingPos(player, lookingEntity, (double)512.0F);
            } else {
                hitPos = result1.getLocation();
            }

            if (hitPos != null) {
                this.turretAutoAimFormVector(40.0F, 40.0F, -25.0F, 50.0F, shootPos.vectorTo(hitPos).normalize());
            }
        }

        if (this.getWeaponIndex(0) == 1) {
            this.seekTargets();
        }

        //this.customOBBCollisionAndFloorTick();
        this.lowHealthWarning();
        this.inertiaRotate(2.0F);
        this.terrainCompact(2.0F, 3.0F);
        this.refreshDimensions();
    }

    public void customOBBCollisionAndFloorTick() {
        for (Entity entity : this.level().getEntities(null, this.getBoundingBox().inflate(3))) {
            if (entity == this || !entity.isAlive() || entity.noPhysics) continue;

            Vec3 entityCenter = entity.getBoundingBox().getCenter();
            AABB entityBox = entity.getBoundingBox();

            boolean stoodOnOBB = false;

            for (OBB obb : getOBBs()) {
                // 1) 通過防止判定（壁など）
                if (OBB.isColliding(obb, entityBox)) {
                    Vector3f normal = obb.getClosestFaceNormal(entityCenter);
                    Vec3 pushVec = new Vec3(normal.x(), normal.y(), normal.z()).scale(0.1);

                    entity.setPos(entity.getX() + pushVec.x, entity.getY() + pushVec.y, entity.getZ() + pushVec.z);

                    Vec3 vel = entity.getDeltaMovement();
                    Vec3 canceled = vel.subtract(
                            pushVec.x != 0 ? vel.x : 0,
                            pushVec.y != 0 ? vel.y : 0,
                            pushVec.z != 0 ? vel.z : 0);
                    entity.setDeltaMovement(canceled);
                }

                // 2) 上に乗れる判定（床）
                OBB.ClosestFaceResult faceResult = OBB.findClosestFace(List.of(obb), entityCenter);
                if (faceResult == null) continue;

                if (faceResult.faceNormal().y() >= 0.3f) {
                    double halfHeight = entity.getBbHeight() / 2.0;
                    double snapY = faceResult.faceCenter().y() + halfHeight + 0.001;

                    // Y座標をOBBの床面にスナップ
                    entity.setPos(entity.getX(), snapY, entity.getZ());

                    // 水平に引き寄せ（XZ平面での安定化）
                    Vector3f center = faceResult.faceCenter();
                    double dx = center.x() - entity.getX();
                    double dz = center.z() - entity.getZ();

                    if (entity instanceof LivingEntity living && dx * dx + dz * dz < 0.5) {
                        Vec3 vel = living.getDeltaMovement();
                        living.setDeltaMovement(vel.add(dx * 0.1, 0, dz * 0.1));
                    }

                    // 落下停止＆地面状態
                    entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0, 1));
                    entity.fallDistance = 0.0f;
                    entity.setOnGround(true);

                    stoodOnOBB = true;
                }
            }

            // 床に乗っていなければ自由落下
            if (!stoodOnOBB && entity instanceof LivingEntity living) {
                living.setOnGround(false);
            }
        }
    }

    public boolean canCollideHardBlock() {
        return this.getDeltaMovement().horizontalDistance() > 0.15;
    }

    private void handleAmmo() {
        boolean var10000;
        label73: {
            Entity var3 = this.getFirstPassenger();
            if (var3 instanceof Player player) {
                if (InventoryTool.hasCreativeAmmoBox(player)) {
                    var10000 = true;
                    break label73;
                }
            }

            var10000 = false;
        }
        int ammoCount = this.countItem((Item)ModItems.HEAVY_AMMO.get());
        boolean hasCreativeAmmoBox = var10000;
        Ammo var10001 = Ammo.HEAVY;
        Objects.requireNonNull(var10001);
        if ((this.hasItem((Item)ModItems.AGM.get()) || hasCreativeAmmoBox) && this.reloadCoolDownMissile == 0 && (Integer)this.getEntityData().get(LOADED_MISSILE) < 200) {
            this.entityData.set(LOADED_MISSILE, (Integer)this.getEntityData().get(LOADED_MISSILE) + 1);
            this.reloadCoolDownMissile = 20;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.AGM.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
        }
        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, ammoCount);
        } else if (this.getWeaponIndex(0) == 1) {
            this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_MISSILE));
        }
    }

    public void vehicleShoot(Player player, int type) {
        Matrix4f transform = this.getVehicleTransform(1.0F);
        if (this.getWeaponIndex(0) == 0) {
            if (this.cannotFire) {
                return;
            }

            boolean var10000;
            label112: {
                Entity pos = this.getFirstPassenger();
                if (pos instanceof Player) {
                    Player pPlayer = (Player)pos;
                    if (InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                        var10000 = true;
                        break label112;
                    }
                }

                var10000 = false;
            }

            boolean hasCreativeAmmo = var10000;
            Vector4f worldPosition = this.transformPosition(transform, 0.1321625F, -0.56446874F, 7.852106F);
            Vector4f worldPosition2 = this.transformPosition(transform, 0.1421625F, -0.5944687F, 8.852106F);
            Vec3 shootVec = (new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z)).vectorTo(new Vec3((double)worldPosition2.x, (double)worldPosition2.y, (double)worldPosition2.z)).normalize();
            if ((Integer)this.entityData.get(AMMO) > 0 || hasCreativeAmmo) {
                SmallCannonShellEntity entityToSpawn = ((SmallCannonShellWeapon)this.getWeapon(0)).create(player);
                entityToSpawn.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
                entityToSpawn.shoot(shootVec.x, shootVec.y, shootVec.z, 30.0F, 0.5F);
                this.level().addFreshEntity(entityToSpawn);
                ParticleTool.sendParticle((ServerLevel)this.level(), ParticleTypes.LARGE_SMOKE, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z, 1, 0.2, 0.2, 0.2, 0.001, true);
                ParticleTool.sendParticle((ServerLevel)this.level(), ParticleTypes.CLOUD, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z, 2, (double)0.5F, (double)0.5F, (double)0.5F, 0.005, true);
                if (!hasCreativeAmmo) {
                    this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.SMALL_SHELL.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
                }
            }

            Level level = player.level();
            Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

            for(Entity target : level.getEntitiesOfClass(Entity.class, (new AABB(center, center)).inflate((double)5.0F), (e) -> true).stream().sorted(Comparator.comparingDouble((e) -> e.distanceToSqr(center))).toList()) {
                if (target instanceof ServerPlayer) {
                    ServerPlayer serverPlayer = (ServerPlayer)target;
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ShakeClientMessage((double)6.0F, (double)5.0F, (double)12.0F, this.getX(), this.getEyeY(), this.getZ()));
                }
            }

            this.entityData.set(HEAT, (Integer)this.entityData.get(HEAT) + 2);
        } else if (this.getWeaponIndex(0) == 1 && (Integer)this.getEntityData().get(LOADED_MISSILE) > 0) {
            int loaded = this.getEntityData().get(LOADED_MISSILE);
            int countToFire = Math.min(loaded, this.lockedTargets.size());

            List<UUID> targets = new ArrayList<>(this.lockedTargets);
            for (int i = 0; i < countToFire; i++) {
                UUID targetUuid = targets.get(i);
                Aam4Entity missile = ((Aam4Weapon) this.getWeapon(0)).create(player);

                // ミサイル発射位置（交互に並べる）
                Random random = new Random();
                int posIndex = random.nextInt(4);
                Vector4f firePosVec;
                switch (posIndex) {
                    case 4 -> firePosVec = this.transformPosition(transform, 5.0F, 7F, 30F);
                    case 3 -> firePosVec = this.transformPosition(transform, -5.0F, 7F, 30F);
                    case 2 -> firePosVec = this.transformPosition(transform, 5.0F, 7F, -30F);
                    default -> firePosVec = this.transformPosition(transform, -5.0F, 7F, -30F);
                }

                Vec3 firePos = new Vec3(firePosVec.x, firePosVec.y, firePosVec.z);

                missile.setTargetUuid(targetUuid.toString());
                missile.setPos(firePos);

                // 🔼 真上に向かって打ち出す
                Vec3 shootDir = new Vec3(0, 3, 0); // ← Vec3.UP の代替
                missile.setDeltaMovement(shootDir.scale(1.5));

                missile.setNoGravity(true); // ← 必要なら、初期上昇時に重力を無効化

                level().addFreshEntity(missile);

                BlockPos pos = BlockPos.containing(firePos);
                this.level().playSound(null, pos, ModSounds.MISSILE_START.get(), SoundSource.HOSTILE, 3.0F, 1.0F);

                loaded--;
                this.entityData.set(LOADED_MISSILE, loaded);

                if (loaded == 3) {
                    this.reloadCoolDownMissile = 25;
                }

                if (loaded <= 0) break;
            }
        }
    }

    public Vec3 shootVec(float tickDelta) {
        Matrix4f transform = this.getVehicleTransform(tickDelta);
        Vector4f worldPosition;
        Vector4f worldPosition2;
        if (this.getWeaponIndex(0) == 1) {
            worldPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
            worldPosition2 = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        } else {
            worldPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
            worldPosition2 = this.transformPosition(transform, 0.0F, -0.03F, 1.0F);
        }

        return (new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z)).vectorTo(new Vec3((double)worldPosition2.x, (double)worldPosition2.y, (double)worldPosition2.z)).normalize();
    }

    public void travel() {
        Entity passenger0 = this.getFirstPassenger();

        if (this.getEnergy() > 0) {
            if (passenger0 == null) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
            }

            // 船の加速と減速（緩やかに）
            if (this.forwardInputDown) {
                this.entityData.set(POWER, (Float)this.entityData.get(POWER) + 0.003F); // 加速遅く
            }

            if (this.backInputDown) {
                this.entityData.set(POWER, (Float)this.entityData.get(POWER) - 0.001F); // 減速遅く
            }

            // 旋回制御（緩やかに）
            if (this.rightInputDown) {
                this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) - 0.02F); // 小さめの回転力
            } else if (this.leftInputDown) {
                this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) + 0.02F);
            }

            // 慣性を維持（減衰を弱く）
            this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.985F);
            this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) * 0.9F);

            // プロペラ回転演出（適度に）
            this.setRotorRot(this.getRotorRot() + 15.0F * (Float)this.entityData.get(POWER));

            // ラダー（操舵角）も緩やかに
            this.setRudderRot(Mth.clamp(this.getRudderRot() - (Float)this.entityData.get(DELTA_ROT), -0.75F, 0.75F) * 0.85F);

            if (this.isInWater() || this.isUnderWater()) {
                // 水中にいるときのみ制御
                float direct = (90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F;

                // ピッチ・ロールは最小限
                this.setXRot(this.getXRot() * 0.98F);
                this.setYRot((float)((double)this.getYRot() - (double)(Float)this.entityData.get(DELTA_ROT)));
                this.setZRot(this.getRoll() * 0.9F); // 徐々に安定

                // 前進移動（穏やかに）
                this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).scale((double)((Float)this.entityData.get(POWER) * 0.25F))));
            }
        }

        // ロール角の慣性戻し
        this.setZRot(this.roll * 0.9F);
    }

    private boolean isValidLockTarget(Entity target) {
        if (!target.isAlive()) return false;

        // プレイヤー自身や自分の乗り物など無効
        if (target == this || target == this.getFirstPassenger()) return false;

        // エンティティタイプのMODフィルタ
        ResourceLocation typeId = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
        if (typeId == null) return false;

        Set<String> allowedMods = Set.of("superbwarfare", "ashvehicle");
        if (!allowedMods.contains(typeId.getNamespace())) return false;

        // 除外対象のエンティティタイプ
        Set<EntityType<?>> excluded = Set.of(
                com.atsuishio.superbwarfare.init.ModEntities.SMALL_CANNON_SHELL.get(),
                com.atsuishio.superbwarfare.init.ModEntities.HELI_ROCKET.get(),
                com.atsuishio.superbwarfare.init.ModEntities.CANNON_SHELL.get(),
                com.atsuishio.superbwarfare.init.ModEntities.GUN_GRENADE.get(),
                com.atsuishio.superbwarfare.init.ModEntities.PROJECTILE.get(),
                com.atsuishio.superbwarfare.init.ModEntities.AGM_65.get(),
                com.atsuishio.superbwarfare.init.ModEntities.RPG_ROCKET.get(),
                com.atsuishio.superbwarfare.init.ModEntities.WG_MISSILE.get(),
                com.atsuishio.superbwarfare.init.ModEntities.JAVELIN_MISSILE.get(),
                com.atsuishio.superbwarfare.init.ModEntities.HAND_GRENADE.get(),
                com.atsuishio.superbwarfare.init.ModEntities.RGO_GRENADE.get(),
                com.atsuishio.superbwarfare.init.ModEntities.MELON_BOMB.get(),
                com.atsuishio.superbwarfare.init.ModEntities.MORTAR_SHELL.get(),
                com.atsuishio.superbwarfare.init.ModEntities.MORTAR.get(),
                com.atsuishio.superbwarfare.init.ModEntities.LASER.get(),
                com.atsuishio.superbwarfare.init.ModEntities.SMOKE_DECOY.get(),
                com.atsuishio.superbwarfare.init.ModEntities.CLAYMORE.get(),
                com.atsuishio.superbwarfare.init.ModEntities.BLU_43.get(),
                com.atsuishio.superbwarfare.init.ModEntities.TM_62.get(),
                com.atsuishio.superbwarfare.init.ModEntities.C_4.get(),
                com.atsuishio.superbwarfare.init.ModEntities.WATER_MASK.get(),
                com.atsuishio.superbwarfare.init.ModEntities.TASER_BULLET.get(),
                com.atsuishio.superbwarfare.init.ModEntities.MK_82.get(),
                com.atsuishio.superbwarfare.init.ModEntities.MK_42.get(),
                tech.lq0.ashvehicle.init.ModEntities.AAM_4.get(),
                tech.lq0.ashvehicle.init.ModEntities.GBU_57.get()
        );
        return !excluded.contains(target.getType());
    }

    private static final Set<EntityType<?>> STEALTH_TYPES = Set.of(
            tech.lq0.ashvehicle.init.ModEntities.F_35.get(),
            tech.lq0.ashvehicle.init.ModEntities.B_2.get(),
            tech.lq0.ashvehicle.init.ModEntities.F_22.get(),
            tech.lq0.ashvehicle.init.ModEntities.SU_57.get(),
            tech.lq0.ashvehicle.init.ModEntities.F_117.get()
    );

    public void seekTargets() {
        Entity entity = this.getFirstPassenger();
        if (!(entity instanceof Player player)) return;

        // ロック候補を取得（範囲内）
        List<Entity> candidates = SeekTool.seekCustomSizeEntities(player, this.level(), 512.0, 32.0, 0.9, false);

        Set<UUID> newCandidates = new HashSet<>();
        Set<UUID> newlyLocked = new HashSet<>();

        for (Entity target : candidates) {
            if (!isValidLockTarget(target)) continue;
            UUID uuid = target.getUUID();
            newCandidates.add(uuid);

            boolean stealth = STEALTH_TYPES.contains(target.getType());
            int progress = lockTargets.getOrDefault(uuid, 0);

            if (this.tickCount % (stealth ? 2 : 1) == 0) {
                progress++;
            }

            if (progress == 1 && player instanceof ServerPlayer serverPlayer) {
                SoundTool.playLocalSound(serverPlayer, ModSounds.JET_LOCK.get(), 2.0F, 1.0F);
            }

            if (progress > 20 && !lockedTargets.contains(uuid)) {
                // 最大4体までロック
                if (lockedTargets.size() < 10) {
                    lockedTargets.add(uuid);
                    newlyLocked.add(uuid);

                    if (player instanceof ServerPlayer serverPlayer) {
                        SoundTool.playLocalSound(serverPlayer, ModSounds.JET_LOCKON.get(), 2.0F, 1.0F);
                    }
                }
            }

            lockTargets.put(uuid, progress);
        }

        Set<UUID> previousLocked = new HashSet<>(lockedTargets);

        // 範囲外 or 画角外のターゲットを解除
        lockTargets.keySet().removeIf(uuid -> !newCandidates.contains(uuid));
        lockedTargets.removeIf(uuid -> !newCandidates.contains(uuid));

        // 変更があればクライアントに通知
        if (player instanceof ServerPlayer serverPlayer) {
            if (!newlyLocked.isEmpty() || !previousLocked.equals(lockedTargets)) {
                List<UUID> sendList = new ArrayList<>(lockedTargets);
                ModNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new MultiLockTargetPacket(sendList));
            }
        }
    }

    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.BOAT_ENGINE.get();
    }

    public float getEngineSoundVolume() {
        return (Mth.abs((Float)this.entityData.get(POWER)) - 0.01F) * 2.0F;
    }

    @ParametersAreNonnullByDefault
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            int i = this.getOrderedPassengers().indexOf(passenger);
            float y = -0.65F;
            Vector4f var10000;
            switch (i) {
                case 0 -> var10000 = this.transformPosition(transform, 0.0F, y + 0.25F, -0.2F);
                case 1 -> var10000 = this.transformPosition(transform, -0.8F, y, -1.2F);
                case 2 -> var10000 = this.transformPosition(transform, 0.8F, y, -1.2F);
                case 3 -> var10000 = this.transformPosition(transform, -0.8F, y, -2.2F);
                case 4 -> var10000 = this.transformPosition(transform, 0.8F, y, -2.2F);
                default -> var10000 = null;
            }

            Vector4f worldPosition = var10000;
            if (worldPosition != null) {
                passenger.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
                callback.accept(passenger, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            }

            if (passenger != this.getFirstPassenger()) {
                passenger.setXRot(passenger.getXRot() + (this.getXRot() - this.xRotO));
            }

            this.copyEntityData(passenger);
        }
    }

    public void copyEntityData(Entity entity) {
        float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        float g = Mth.clamp(f, -105.0F, 105.0F);
        entity.yRotO += g - f;
        entity.setYRot(entity.getYRot() + g - f);
        entity.setYHeadRot(entity.getYRot());
        entity.setYBodyRot(this.getYRot());
    }

    public void destroy() {
        if (this.level() instanceof ServerLevel) {
            CustomExplosion explosion = (new CustomExplosion(this.level(), this, ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this.getAttacker(), this.getAttacker()), 80.0F, this.getX(), this.getY(), this.getZ(), 5.0F, (Boolean) ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
            explosion.explode();
            ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnMediumExplosionParticles(this.level(), this.position());
        }

        this.explodePassengers();
        super.destroy();
    }

    protected void clampRotation(Entity entity) {
        float a = this.getTurretYaw(1.0F);
        float r = (Mth.abs(a) - 90.0F) / 90.0F;
        float r2;
        if (Mth.abs(a) <= 90.0F) {
            r2 = a / 90.0F;
        } else if (a < 0.0F) {
            r2 = -(180.0F + a) / 90.0F;
        } else {
            r2 = (180.0F - a) / 90.0F;
        }

        float min = -50.0F - r * this.getXRot() - r2 * this.getRoll();
        float max = 25.0F - r * this.getXRot() - r2 * this.getRoll();
        float f = Mth.wrapDegrees(entity.getXRot());
        float f1 = Mth.clamp(f, min, max);
        entity.xRotO += f1 - f;
        entity.setXRot(entity.getXRot() + f1 - f);
        float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        float f3 = Mth.clamp(f2, -105.0F, 105.0F);
        entity.yRotO += f3 - f2;
        entity.setYRot(entity.getYRot() + f3 - f2);
        entity.setYBodyRot(this.getYRot());
    }

    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = this.getBarrelTransform(ticks);
        float x = 0.0F;
        float y = 0.5F;
        float z = -0.25F;
        Vector4f worldPosition = this.transformPosition(transform, x, y, z);
        return new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
    }

    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = this.getBarrelTransform(pPartialTicks);
        Vector4f rootPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f targetPosition = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        return (new Vec3((double)rootPosition.x, (double)rootPosition.y, (double)rootPosition.z)).vectorTo(new Vec3((double)targetPosition.x, (double)targetPosition.y, (double)targetPosition.z));
    }

    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = this.getTurretTransform(ticks);
        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.5088375F, 0.04173125F);
        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        float a = this.getTurretYaw(ticks);
        float r = (Mth.abs(a) - 90.0F) / 90.0F;
        float r2;
        if (Mth.abs(a) <= 90.0F) {
            r2 = a / 90.0F;
        } else if (a < 0.0F) {
            r2 = -(180.0F + a) / 90.0F;
        } else {
            r2 = (180.0F - a) / 90.0F;
        }

        float x = Mth.lerp(ticks, this.turretXRotO, this.getTurretXRot());
        float xV = Mth.lerp(ticks, this.xRotO, this.getXRot());
        float z = Mth.lerp(ticks, this.prevRoll, this.getRoll());
        transformT.rotate(Axis.XP.rotationDegrees(x + r * xV + r2 * z));
        return transformT;
    }

    public Matrix4f getTurretTransform(float ticks) {
        Matrix4f transformV = this.getVehicleTransform(ticks);
        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 1.5616626F, -0.565625F);
        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, this.turretYRotO, this.getTurretYRot())));
        return transformV;
    }

    public Matrix4f getVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float)Mth.lerp((double)ticks, this.xo, this.getX()), (float)Mth.lerp((double)ticks, this.yo + (double)0.9F, this.getY() + (double)0.9F), (float)Mth.lerp((double)ticks, this.zo, this.getZ()));
        transform.rotate(Axis.YP.rotationDegrees(-Mth.lerp(ticks, this.yRotO, this.getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, this.xRotO, this.getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, this.prevRoll, this.getRoll())));
        return transform;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public int getMaxPassengers() {
        return 5;
    }

    public int mainGunRpm(Player player) {
        return 500;
    }

    public boolean canShoot(Player player) {
        return ((Integer)this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !this.cannotFire;
    }

    public int getAmmoCount(Player player) {
        return (Integer)this.entityData.get(AMMO);
    }

    public boolean hidePassenger(Entity entity) {
        return false;
    }

    public int zoomFov() {
        return 1;
    }

    public int getWeaponHeat(Player player) {
        return (Integer)this.entityData.get(HEAT);
    }

    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/speedboat_icon.png");
    }

    public Vec3 getGunVec(float ticks) {
        return this.getBarrelVector(ticks);
    }

    @OnlyIn(Dist.CLIENT)
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        return this.getSeatIndex(player) == 0 && zoom ? new Vec2((float)(-getYRotFromVector(this.getBarrelVec(partialTicks))), (float)(-getXRotFromVector(this.getBarrelVec(partialTicks)))) : super.getCameraRotation(partialTicks, player, zoom, isFirstPerson);
    }

    @OnlyIn(Dist.CLIENT)
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        return this.getSeatIndex(player) == 0 && zoom ? new Vec3(this.driverZoomPos(partialTicks).x, this.driverZoomPos(partialTicks).y, this.driverZoomPos(partialTicks).z) : super.getCameraPosition(partialTicks, player, zoom, isFirstPerson);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean useFixedCameraPos(Entity entity) {
        return this.getSeatIndex(entity) == 0;
    }

    @OnlyIn(Dist.CLIENT)
    public @Nullable Pair<Quaternionf, Quaternionf> getPassengerRotation(Entity entity, float tickDelta) {
        return Pair.of(Axis.XP.rotationDegrees(-this.getViewXRot(tickDelta)), Axis.ZP.rotationDegrees(-this.getRoll(tickDelta)));
    }

    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/water.png");
    }

    @Override
    public List<OBB> getOBBs() {
        return List.of(this.obb2);
    }
}
