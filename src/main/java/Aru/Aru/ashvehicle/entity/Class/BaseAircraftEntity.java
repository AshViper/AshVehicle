package Aru.Aru.ashvehicle.entity.Class;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.projectile.*;
import com.atsuishio.superbwarfare.entity.vehicle.base.*;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.*;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import Aru.Aru.ashvehicle.init.LockTargetPacket;
import Aru.Aru.ashvehicle.init.ModNetwork;
import Aru.Aru.ashvehicle.init.MultiLockTargetPacket;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;

public abstract class BaseAircraftEntity extends ContainerMobileVehicleEntity implements GeoEntity, WeaponVehicleEntity, AircraftEntity {
    public static Consumer<MobileVehicleEntity> fireSound = (vehicle) -> {
    };
    public static final EntityDataAccessor<Integer> LOADED_ROCKET;
    public static final EntityDataAccessor<Integer> LOADED_BOMB;
    public static final EntityDataAccessor<Integer> LOADED_MISSILE;
    public static final EntityDataAccessor<Integer> FIRE_TIME;
    public static final EntityDataAccessor<String> TARGET_UUID;
    private final AnimatableInstanceCache cache;
    public int fireIndex;
    public int reloadCoolDownBomb;
    public int reloadCoolDownMissile;
    public String lockingTargetO;
    public String lockingTarget;
    public float destroyRot;
    public int lockTime;
    public boolean locked;
    private boolean wasFiring;
    public float delta_xo;
    public float delta_yo;
    public float delta_x;
    public float delta_y;
    private final Map<UUID, Integer> lockTargets = new HashMap<>();
    protected final Set<UUID> lockedTargets = new HashSet<>();
    Vec3 velocity = this.getDeltaMovement();
    Vec3 view = this.getViewVector(1.0F);
    double angleBetween = calculateAngle(velocity, view); // 進行方向と向きの角度（ラジアン）
    boolean isHighAoA = angleBetween > Math.toRadians(45); // 45度以上を高迎角とみなす

    static {
        LOADED_ROCKET = SynchedEntityData.defineId(BaseAircraftEntity.class, EntityDataSerializers.INT);
        LOADED_BOMB = SynchedEntityData.defineId(BaseAircraftEntity.class, EntityDataSerializers.INT);
        LOADED_MISSILE = SynchedEntityData.defineId(BaseAircraftEntity.class, EntityDataSerializers.INT);
        FIRE_TIME = SynchedEntityData.defineId(BaseAircraftEntity.class, EntityDataSerializers.INT);
        TARGET_UUID = SynchedEntityData.defineId(BaseAircraftEntity.class, EntityDataSerializers.STRING);
    }

    public BaseAircraftEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.lockingTargetO = "none";
        this.lockingTarget = "none";
        this.wasFiring = false;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOADED_ROCKET, 0);
        this.entityData.define(LOADED_BOMB, 0);
        this.entityData.define(LOADED_MISSILE, 0);
        this.entityData.define(FIRE_TIME, 0);
        this.entityData.define(TARGET_UUID, "none");
    }

    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{(new SmallCannonShellWeapon()).damage((float)(Integer)VehicleConfig.A_10_CANNON_DAMAGE.get()).explosionDamage((float)(Integer)VehicleConfig.A_10_CANNON_EXPLOSION_DAMAGE.get()).explosionRadius(((Double)VehicleConfig.A_10_CANNON_EXPLOSION_RADIUS.get()).floatValue()).sound((SoundEvent)ModSounds.INTO_CANNON.get()).icon(Mod.loc("textures/screens/vehicle_weapon/cannon_30mm.png")), (new HeliRocketWeapon()).damage((float)(Integer)VehicleConfig.A_10_ROCKET_DAMAGE.get()).explosionDamage((float)(Integer)VehicleConfig.A_10_ROCKET_EXPLOSION_DAMAGE.get()).explosionRadius(((Double)VehicleConfig.A_10_ROCKET_EXPLOSION_RADIUS.get()).floatValue()).sound((SoundEvent)ModSounds.INTO_MISSILE.get()), (new Mk82Weapon()).sound((SoundEvent)ModSounds.INTO_MISSILE.get()), (new Agm65Weapon()).sound((SoundEvent)ModSounds.INTO_MISSILE.get())}};
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedRocket", (Integer)this.entityData.get(LOADED_ROCKET));
        compound.putInt("LoadedBomb", (Integer)this.entityData.get(LOADED_BOMB));
        compound.putInt("LoadedMissile", (Integer)this.entityData.get(LOADED_MISSILE));
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_ROCKET, compound.getInt("LoadedRocket"));
        this.entityData.set(LOADED_BOMB, compound.getInt("LoadedBomb"));
        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
    }

    @ParametersAreNonnullByDefault
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound((SoundEvent)ModSounds.WHEEL_STEP.get(), (float)(this.getDeltaMovement().length() * 0.2), this.random.nextFloat() * 0.1F + 1.0F);
    }

    public boolean sendFireStarParticleOnHurt() {
        return false;
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> this.getSourceAngle(source, 0.25F) * damage * (this.getHealth() > 0.1F ? 0.4F : 0.05F));
    }

    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == ModItems.ROCKET_70.get() && (Integer)this.entityData.get(LOADED_ROCKET) < 28) {
            this.entityData.set(LOADED_ROCKET, (Integer)this.entityData.get(LOADED_ROCKET) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        } else if (stack.getItem() == ModItems.MEDIUM_AERIAL_BOMB.get() && (Integer)this.entityData.get(LOADED_BOMB) < 3) {
            this.entityData.set(LOADED_BOMB, (Integer)this.entityData.get(LOADED_BOMB) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        } else if (stack.getItem() == ModItems.AGM.get() && (Integer)this.entityData.get(LOADED_MISSILE) < 4) {
            this.entityData.set(LOADED_MISSILE, (Integer)this.entityData.get(LOADED_MISSILE) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        } else {
            return super.interact(player, hand);
        }
    }

    public void baseTick() {
        // 🔫 射撃サウンド処理
        if (!this.wasFiring && this.isFiring() && this.level().isClientSide()) {
            fireSound.accept(this);
        }
        this.wasFiring = this.isFiring();

        // 状態・移動記録
        this.lockingTargetO = this.getTargetUuid();
        this.delta_xo = this.delta_x;
        this.delta_yo = this.delta_y;

        super.baseTick();

        // 🔁 空気抵抗・加速
        float f = (float) Mth.clamp(
                Math.max((this.onGround() ? 0.819F : 0.82F) - 0.0035 * this.getDeltaMovement().length(), 0.5)
                        + 0.001F * Mth.abs(90.0F - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F,
                0.01, 0.99
        );

        boolean forward = this.getDeltaMovement().dot(this.getViewVector(1.0F)) > 0.0;
        this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F)
                .scale((forward ? 0.227 : 0.1) * this.getDeltaMovement().dot(this.getViewVector(1.0F)))));
        this.setDeltaMovement(this.getDeltaMovement().multiply(f, f, f));

        // ✈️ ソニックブーム判定（Mach1 = 約17 blocks/tick）
        double speed = this.getDeltaMovement().length();
        //System.out.println("Speed: " + speed);
        double mach1 = 3.5f;
        boolean hasBoomed = this.getPersistentData().getBoolean("HasBoomed");

        if (!hasBoomed && speed >= mach1) {
            this.getPersistentData().putBoolean("HasBoomed", true);
            if (this.level() instanceof ServerLevel serverLevel) {
                int count = 60;            // パーティクル数
                double maxLength = 5.0;    // 円錐の長さ（z方向）
                double maxRadius = 2.5;    // 円錐の底面半径（x,y平面）

                double x = this.getX();
                double y = this.getY();
                double z = this.getZ();

                RandomSource random = this.level().random;

                for (int i = 0; i < count; i++) {
                    // z方向の位置（高さ）
                    double length = random.nextDouble() * maxLength;  // 0 ～ maxLength の範囲

                    // z方向に沿って、円錐の断面半径を線形に変化（先端ほど小さく）
                    double radiusAtLength = (1 - (length / maxLength)) * maxRadius;

                    // x,y の角度（円周上）
                    double angle = random.nextDouble() * 2 * Math.PI;

                    // 半径は少しランダムにブレを加える
                    double radius = radiusAtLength * (0.7 + 0.6 * random.nextDouble());

                    // パーティクルの座標
                    double posX = x + Math.cos(angle) * radius;
                    double posY = y + 1 + Math.sin(angle) * radius;
                    double posZ = z + length;

                    // 速度はz軸方向に少し押し出す + 断面方向に拡散
                    double speedZ = 0.05 + 0.05 * random.nextDouble();
                    double speedRadial = 0.02 + 0.03 * random.nextDouble();

                    double motionX = Math.cos(angle) * speedRadial;
                    double motionY = Math.sin(angle) * speedRadial;
                    double motionZ = speedZ;

                    ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD,
                            posX, posY, posZ,
                            1, motionX, motionY, motionZ,
                            0.1, true);
                }
            }
        } else if (hasBoomed && speed < mach1 * 0.9) {
            this.getPersistentData().putBoolean("HasBoomed", false); // 低速で再ロック可能に
        }

        // 🌊 水中衝突ダメージ
        if (this.isInWater() && this.tickCount % 4 == 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 0.6, 0.6));

            if (this.lastTickSpeed > 0.4) {
                float damage = 20.0F * (float) java.lang.Math.pow(this.lastTickSpeed - 0.4, 2);
                this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this,
                        this.getFirstPassenger() == null ? this : this.getFirstPassenger()), damage);
            }
        }

        // 🎮 クールダウンと弾処理
        if (this.level() instanceof ServerLevel) {
            if (this.reloadCoolDown > 0) --this.reloadCoolDown;
            if (this.reloadCoolDownBomb > 0) --this.reloadCoolDownBomb;
            if (this.reloadCoolDownMissile > 0) --this.reloadCoolDownMissile;
            this.handleAmmo();
        }

        // 🔫 射撃処理
        Entity rider = this.getFirstPassenger();
        if (rider instanceof Player player) {
            if (this.fireInputDown) {
                int weaponIndex = this.getWeaponIndex(0);
                int ammo = this.entityData.get(AMMO);

                if (weaponIndex == 0 && (ammo > 0 || InventoryTool.hasCreativeAmmoBox(player))) {
                    if (!this.cannotFire) this.vehicleShoot(player, 0);
                } else if (weaponIndex == 1 && ammo > 0) {
                    this.vehicleShoot(player, 0);
                }
            }
        }

        // 🛬 着陸時の地形変形
        if (this.onGround()) {
            this.terrainCompactA10();
        }

        // 🔥 発射タイマー
        if (this.entityData.get(FIRE_TIME) > 0) {
            this.entityData.set(FIRE_TIME, this.entityData.get(FIRE_TIME) - 1);
        }

        if (isHighAoA && this.tickCount % 2 == 0 && this.level() instanceof ServerLevel serverLevel) {
            double wingOffset = 5.0; // 主翼の横方向のオフセット
            double heightOffset = 1.0;

            for (double side : new double[]{-1.0, 1.0}) {
                Vec3 wingTip = this.position()
                        .add(this.getLookAngle().cross(new Vec3(0, 1, 0)).normalize().scale(wingOffset * side))
                        .add(0, heightOffset, 0);

                ParticleTool.sendParticle(
                        serverLevel,
                        ParticleTypes.CLOUD,
                        wingTip.x, wingTip.y, wingTip.z,
                        1, 0, 0, 0,
                        0.01, true
                );
            }
        }

        // 🎯 ミサイルロック処理
        if (this.getWeaponIndex(0) == 3) {
            this.seekTarget();
        }

        // ⚠️ 警告/防衛処理
        this.lowHealthWarning();
        this.releaseDecoy();
        this.refreshDimensions();
    }


    public void lowHealthWarning() {
        Matrix4f transform = this.getVehicleTransform(1.0F);
        if ((double)this.getHealth() <= 0.4 * (double)this.getMaxHealth()) {
            for(Entity e : getPlayer(this.level())) {
                if (e instanceof ServerPlayer) {
                    ServerPlayer player = (ServerPlayer)e;
                    Level pos = player.level();
                    if (pos instanceof ServerLevel) {
                        ServerLevel serverLevel = (ServerLevel)pos;
                        Vector4f position = this.transformPosition(transform, -1.603125F, 0.875F, -5.0625F);
                        ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, (double)position.x, (double)position.y, (double)position.z, 5, (double)0.25F, (double)0.25F, (double)0.25F, (double)0.0F, true);
                    }
                }
            }
        }

        Level var11 = this.level();
        if (var11 instanceof ServerLevel serverLevel) {
            if ((double)this.getHealth() <= (double)0.25F * (double)this.getMaxHealth()) {
                this.playLowHealthParticle(serverLevel);
            }

            if ((double)this.getHealth() <= 0.15 * (double)this.getMaxHealth()) {
                this.playLowHealthParticle(serverLevel);
            }
        }

        if ((double)this.getHealth() <= 0.1 * (double)this.getMaxHealth()) {
            for(Entity e : getPlayer(this.level())) {
                if (e instanceof ServerPlayer) {
                    ServerPlayer player = (ServerPlayer)e;
                    Level var19 = player.level();
                    if (var19 instanceof ServerLevel) {
                        ServerLevel serverLevel = (ServerLevel)var19;
                        Vector4f position = this.transformPosition(transform, -1.603125F, 0.875F, -5.0625F);
                        ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, (double)position.x, (double)position.y, (double)position.z, 5, (double)0.25F, (double)0.25F, (double)0.25F, (double)0.0F, true);
                        ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, (double)position.x, (double)position.y, (double)position.z, 5, (double)0.25F, (double)0.25F, (double)0.25F, (double)0.0F, true);
                        ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, (double)position.x, (double)position.y, (double)position.z, 5, (double)0.25F, (double)0.25F, (double)0.25F, (double)0.0F, true);
                        ParticleTool.sendParticle(serverLevel, (SimpleParticleType)ModParticleTypes.FIRE_STAR.get(), (double)position.x, (double)position.y, (double)position.z, 5, (double)0.25F, (double)0.25F, (double)0.25F, (double)0.0F, true);
                        Vector4f position2 = this.transformPosition(transform, 1.603125F, 0.875F, -5.0625F);
                        ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, (double)position2.x, (double)position2.y, (double)position2.z, 5, (double)0.25F, (double)0.25F, (double)0.25F, (double)0.0F, true);
                        ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, (double)position2.x, (double)position2.y, (double)position2.z, 5, (double)0.25F, (double)0.25F, (double)0.25F, (double)0.0F, true);
                        ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, (double)position2.x, (double)position2.y, (double)position2.z, 5, (double)0.25F, (double)0.25F, (double)0.25F, (double)0.0F, true);
                        ParticleTool.sendParticle(serverLevel, (SimpleParticleType)ModParticleTypes.FIRE_STAR.get(), (double)position2.x, (double)position2.y, (double)position2.z, 5, (double)0.25F, (double)0.25F, (double)0.25F, (double)0.0F, true);
                    }
                }
            }

            Level var15 = this.level();
            if (var15 instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)var15;
                ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + (double)(0.7F * this.getBbHeight()), this.getZ(), 2, 0.35 * (double)this.getBbWidth(), 0.15 * (double)this.getBbHeight(), 0.35 * (double)this.getBbWidth(), 0.01, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + (double)(0.7F * this.getBbHeight()), this.getZ(), 2, 0.35 * (double)this.getBbWidth(), 0.15 * (double)this.getBbHeight(), 0.35 * (double)this.getBbWidth(), 0.01, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, this.getX(), this.getY() + (double)(0.85F * this.getBbHeight()), this.getZ(), 4, 0.35 * (double)this.getBbWidth(), 0.12 * (double)this.getBbHeight(), 0.35 * (double)this.getBbWidth(), 0.05, true);
                ParticleTool.sendParticle(serverLevel, (SimpleParticleType)ModParticleTypes.FIRE_STAR.get(), this.getX(), this.getY() + (double)(0.85F * this.getBbHeight()), this.getZ(), 4, 0.1 * (double)this.getBbWidth(), 0.05 * (double)this.getBbHeight(), 0.1 * (double)this.getBbWidth(), 0.4, true);
            }

            if (this.tickCount % 15 == 0) {
                this.level().playSound((Player)null, this.getOnPos(), SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }

        if (this.getHealth() < 0.1F * this.getMaxHealth() && this.tickCount % 13 == 0) {
            this.level().playSound((Player)null, this.getOnPos(), (SoundEvent)ModSounds.NO_HEALTH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        } else if (this.getHealth() >= 0.1F && this.getHealth() < 0.4F * this.getMaxHealth() && this.tickCount % 10 == 0) {
            this.level().playSound((Player)null, this.getOnPos(), (SoundEvent)ModSounds.LOW_HEALTH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

    }

    public void terrainCompactA10() {
        if (this.onGround()) {
            Matrix4f transform = this.getWheelsTransform(1.0F);
            Vector4f positionF = this.transformPosition(transform, 0.141675F, 0.0F, 4.6315126F);
            Vector4f positionLB = this.transformPosition(transform, 2.5752F, 0.0F, -0.7516125F);
            Vector4f positionRB = this.transformPosition(transform, -2.5752F, 0.0F, -0.7516125F);
            Vec3 p1 = new Vec3((double)positionF.x, (double)positionF.y, (double)positionF.z);
            Vec3 p2 = new Vec3((double)positionLB.x, (double)positionLB.y, (double)positionLB.z);
            Vec3 p3 = new Vec3((double)positionRB.x, (double)positionRB.y, (double)positionRB.z);
            float p1y = (float)this.traceBlockY(p1, (double)3.0F);
            float p2y = (float)this.traceBlockY(p2, (double)3.0F);
            float p3y = (float)this.traceBlockY(p3, (double)3.0F);
            p1 = new Vec3((double)positionF.x, (double)p1y, (double)positionF.z);
            p2 = new Vec3((double)positionLB.x, (double)p2y, (double)positionLB.z);
            p3 = new Vec3((double)positionRB.x, (double)p3y, (double)positionRB.z);
            Vec3 p4 = p2.add(p3).scale((double)0.5F);
            Vec3 v1 = p2.vectorTo(p3);
            Vec3 v2 = p4.vectorTo(p1);
            double x = getXRotFromVector(v2);
            double z = getXRotFromVector(v1);
            float diffX = Math.clamp(-5.0F, 5.0F, Mth.wrapDegrees((float)((double)-2.0F * x) - this.getXRot()));
            this.setXRot(Mth.clamp(this.getXRot() + 0.05F * diffX, -45.0F, 45.0F));
            float diffZ = Math.clamp(-5.0F, 5.0F, Mth.wrapDegrees((float)((double)-2.0F * z) - this.getRoll()));
            this.setZRot(Mth.clamp(this.getRoll() + 0.05F * diffZ, -45.0F, 45.0F));
        } else if (this.isInWater()) {
            this.setXRot(this.getXRot() * 0.9F);
            this.setZRot(this.getRoll() * 0.9F);
        }

    }

    public void handleAmmo() {
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

        boolean hasCreativeAmmoBox = var10000;
        int ammoCount = this.countItem((Item)ModItems.SMALL_SHELL.get());
        if ((this.hasItem((Item)ModItems.ROCKET_70.get()) || hasCreativeAmmoBox) && this.reloadCoolDown == 0 && (Integer)this.getEntityData().get(LOADED_ROCKET) < 28) {
            this.entityData.set(LOADED_ROCKET, (Integer)this.getEntityData().get(LOADED_ROCKET) + 1);
            this.reloadCoolDown = 15;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.ROCKET_70.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
        }

        if ((this.hasItem((Item)ModItems.MEDIUM_AERIAL_BOMB.get()) || hasCreativeAmmoBox) && this.reloadCoolDownBomb == 0 && (Integer)this.getEntityData().get(LOADED_BOMB) < 3) {
            this.entityData.set(LOADED_BOMB, (Integer)this.getEntityData().get(LOADED_BOMB) + 1);
            this.reloadCoolDownBomb = 300;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.MEDIUM_AERIAL_BOMB.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
        }

        if ((this.hasItem((Item)ModItems.AGM.get()) || hasCreativeAmmoBox) && this.reloadCoolDownMissile == 0 && (Integer)this.getEntityData().get(LOADED_MISSILE) < 4) {
            this.entityData.set(LOADED_MISSILE, (Integer)this.getEntityData().get(LOADED_MISSILE) + 1);
            this.reloadCoolDownMissile = 400;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.AGM.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
        }

        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, ammoCount);
        } else if (this.getWeaponIndex(0) == 1) {
            this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_ROCKET));
        } else if (this.getWeaponIndex(0) == 2) {
            this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_BOMB));
        } else if (this.getWeaponIndex(0) == 3) {
            this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_MISSILE));
        }

    }

    private boolean isValidLockTarget(Entity target) {
        if (!target.isAlive()) return false;

        // プレイヤー自身や自分の乗り物など無効
        if (target == this || target == this.getFirstPassenger()) return false;

        // エンティティタイプのMODフィルタ
        ResourceLocation typeId = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
        if (typeId == null) return false;

        Set<String> allowedMods = Set.of("superbwarfare", "ashvehicle", "vvp" );
        if (!allowedMods.contains(typeId.getNamespace())) return false;

        // 除外対象のエンティティタイプ
        Set<EntityType<?>> excluded = Set.of(
                ModEntities.SMALL_CANNON_SHELL.get(),
                ModEntities.HELI_ROCKET.get(),
                ModEntities.CANNON_SHELL.get(),
                ModEntities.GUN_GRENADE.get(),
                ModEntities.PROJECTILE.get(),
                ModEntities.AGM_65.get(),
                ModEntities.RPG_ROCKET.get(),
                ModEntities.WG_MISSILE.get(),
                ModEntities.JAVELIN_MISSILE.get(),
                ModEntities.HAND_GRENADE.get(),
                ModEntities.RGO_GRENADE.get(),
                ModEntities.MELON_BOMB.get(),
                ModEntities.MORTAR_SHELL.get(),
                ModEntities.MORTAR.get(),
                ModEntities.LASER.get(),
                ModEntities.SMOKE_DECOY.get(),
                ModEntities.CLAYMORE.get(),
                ModEntities.BLU_43.get(),
                ModEntities.TM_62.get(),
                ModEntities.C_4.get(),
                ModEntities.WATER_MASK.get(),
                ModEntities.TASER_BULLET.get(),
                ModEntities.MK_82.get(),
                ModEntities.MK_42.get(),
                Aru.Aru.ashvehicle.init.ModEntities.AAM_4.get(),
                Aru.Aru.ashvehicle.init.ModEntities.GBU_57.get()
        );
        return !excluded.contains(target.getType());
    }

    private static final Set<EntityType<?>> STEALTH_TYPES = Set.of(
            Aru.Aru.ashvehicle.init.ModEntities.F_35.get(),
            Aru.Aru.ashvehicle.init.ModEntities.B_2.get(),
            Aru.Aru.ashvehicle.init.ModEntities.F_22.get(),
            Aru.Aru.ashvehicle.init.ModEntities.SU_57.get(),
            Aru.Aru.ashvehicle.init.ModEntities.F_117.get()
    );

    public void seekTarget() {
        Entity entity = this.getFirstPassenger();
        if (!(entity instanceof Player player)) return;

        String uuidStr = this.getTargetUuid();

        // UUIDとして扱う前に有効チェックをする
        boolean validUuid = false;
        UUID targetUUID = null;
        if (uuidStr != null && !uuidStr.equals("none") && !uuidStr.isBlank()) {
            try {
                targetUUID = UUID.fromString(uuidStr);
                validUuid = true;
            } catch (IllegalArgumentException e) {
                // 不正なUUID文字列なのでリセットする
                this.setTargetUuid("none");
                this.lockTime = 0;
            }
        }

        if (validUuid && this.getTargetUuid().equals(this.lockingTargetO) && this.level() instanceof ServerLevel serverLevel) {
            Entity lockedEntity = serverLevel.getEntity(targetUUID);
            if (lockedEntity != null && STEALTH_TYPES.contains(lockedEntity.getType())) {
                if (this.tickCount % 2 == 0) {
                    ++this.lockTime; // ステルス対象はロック時間が遅く進む
                }
            } else {
                ++this.lockTime;
            }
        } else {
            this.resetSeek(player);
        }

        // 🔽 全候補から検索
        Entity candidate = SeekTool.seekCustomSizeEntity(this, this.level(), 384.0, 18.0, 0.9, false);

        // 🔍 フィルター条件（MODやタイプ）
        if (candidate != null && isValidLockTarget(candidate)) {
            String candidateUuid = candidate.getUUID().toString();

            if (this.lockTime == 0) {
                this.setTargetUuid(candidateUuid);
            }

            if (!candidateUuid.equals(this.getTargetUuid())) {
                this.resetSeek(player);
                this.setTargetUuid(candidateUuid);
            }
        } else {
            this.setTargetUuid("none");
        }

        if (this.lockTime == 1 && player instanceof ServerPlayer serverPlayer) {
            SoundTool.playLocalSound(serverPlayer, ModSounds.JET_LOCK.get(), 2.0F, 1.0F);
        }

        if (this.lockTime > 20 && !this.locked) {
            this.locked = true;

            if (player instanceof ServerPlayer serverPlayer) {
                SoundTool.playLocalSound(serverPlayer, ModSounds.JET_LOCKON.get(), 2.0F, 1.0F);
                // ここも安全にUUIDを扱う
                if (validUuid) {
                    ModNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new LockTargetPacket(targetUUID));
                }
            }
        }
    }

    public void seekTargets() {
        Entity entity = this.getFirstPassenger();
        if (!(entity instanceof Player player)) return;

        List<Entity> candidates = SeekTool.seekCustomSizeEntities(this, this.level(), 512.0, 32.0, 0.9, false);
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
                // 👇 ロック数が上限（4体）未満なら追加
                if (lockedTargets.size() < 4) {
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

        // 範囲外になったロックを削除
        lockTargets.keySet().removeIf(uuid -> !newCandidates.contains(uuid));
        lockedTargets.removeIf(uuid -> !newCandidates.contains(uuid));

        // 変更があればクライアントに送信（追加または解除）
        if (player instanceof ServerPlayer serverPlayer) {
            if (!newlyLocked.isEmpty() || !previousLocked.equals(lockedTargets)) {
                List<UUID> sendList = new ArrayList<>(lockedTargets);
                ModNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new MultiLockTargetPacket(sendList));
            }
        }
    }

    public void resetSeek(Player player) {
        this.lockTime = 0;
        this.locked = false;
        if (player instanceof ServerPlayer serverPlayer) {
            ClientboundStopSoundPacket clientboundstopsoundpacket = new ClientboundStopSoundPacket(new ResourceLocation("superbwarfare", "jet_lock"), SoundSource.PLAYERS);
            serverPlayer.connection.send(clientboundstopsoundpacket);
            ModNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new LockTargetPacket(new UUID(0L, 0L)));
        }

    }

    public void setTargetUuid(String uuid) {
        this.lockingTarget = uuid;
    }

    public String getTargetUuid() {
        return this.lockingTarget;
    }

    public void travel() {
        Entity passenger = this.getFirstPassenger();
        if (this.getHealth() > 0.1F * this.getMaxHealth()) {
            if (passenger != null && !this.isInWater()) {
                if (passenger instanceof Player) {
                    if (this.getEnergy() > 0) {
                        if (this.forwardInputDown) {
                            this.entityData.set(POWER, Math.min((Float)this.entityData.get(POWER) + 0.004F, this.sprintInputDown ? 1.0F : 0.0575F));
                        }

                        if (this.backInputDown) {
                            this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - 0.002F, -0.2F));
                        }
                    }

                    if (!this.onGround()) {
                        if (this.rightInputDown) {
                            this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) - 1.2F);
                        } else if (this.leftInputDown) {
                            this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) + 1.2F);
                        }
                    }

                    if (this.downInputDown) {
                        if (this.onGround()) {
                            this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.8F);
                            this.setDeltaMovement(this.getDeltaMovement().multiply(0.97, (double)1.0F, 0.97));
                        } else {
                            this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.97F);
                            this.setDeltaMovement(this.getDeltaMovement().multiply(0.994, (double)1.0F, 0.994));
                        }

                        this.entityData.set(PLANE_BREAK, Math.min((Float)this.entityData.get(PLANE_BREAK) + 10.0F, 60.0F));
                    }
                }
            } else {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.95F);
                if (this.onGround()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.94, (double)1.0F, 0.94));
                } else {
                    this.setXRot(Mth.clamp(this.getXRot() + 0.1F, -89.0F, 89.0F));
                }
            }

            if (this.getEnergy() > 0 && !this.level().isClientSide) {
                this.consumeEnergy((int)(Mth.abs((Float)this.entityData.get(POWER)) * (float)(Integer)VehicleConfig.A_10_MAX_ENERGY_COST.get()));
            }

            float rotSpeed = 1.5F + 2.0F * Mth.abs(VectorTool.calculateY(this.getRoll()));
            float addY = Mth.clamp(Math.max((this.onGround() ? 0.1F : 0.2F) * (float)this.getDeltaMovement().length(), 0.0F) * (Float)this.entityData.get(MOUSE_SPEED_X), -rotSpeed, rotSpeed);
            float addX = Mth.clamp(Math.min((float)Math.max(this.getDeltaMovement().dot(this.getViewVector(1.0F)) - 0.24, 0.03), 0.4F) * (Float)this.entityData.get(MOUSE_SPEED_Y), -3.5F, 3.5F);
            float addZ = (Float)this.entityData.get(DELTA_ROT) - (this.onGround() ? 0.0F : 0.004F) * (Float)this.entityData.get(MOUSE_SPEED_X) * (float)this.getDeltaMovement().dot(this.getViewVector(1.0F));
            float i = this.getXRot() / 80.0F;
            this.delta_x = addX;
            this.delta_y = addY - VectorTool.calculateY(this.getXRot()) * addZ;
            this.setYRot(this.getYRot() + this.delta_y);
            if (!this.onGround()) {
                this.setXRot(this.getXRot() + this.delta_x);
                this.setZRot(this.getRoll() - addZ * (1.0F - Mth.abs(i)));
            }

            if (!this.onGround()) {
                float speed = Mth.clamp(Mth.abs(this.roll) / 90.0F, 0.0F, 1.0F);
                if (this.roll > 0.0F) {
                    this.setZRot(this.roll - Math.min(speed, this.roll));
                } else if (this.roll < 0.0F) {
                    this.setZRot(this.roll + Math.min(speed, -this.roll));
                }
            }

            this.setPropellerRot(this.getPropellerRot() + 30.0F * (Float)this.entityData.get(POWER));
            if (this.upInputDown) {
                this.upInputDown = false;
                if ((Integer)this.entityData.get(GEAR_ROT) == 0 && !this.onGround()) {
                    this.entityData.set(GEAR_UP, true);
                } else if ((Integer)this.entityData.get(GEAR_ROT) == 85) {
                    this.entityData.set(GEAR_UP, false);
                }
            }

            if (this.onGround()) {
                this.entityData.set(GEAR_UP, false);
            }

            if ((Boolean)this.entityData.get(GEAR_UP)) {
                this.entityData.set(GEAR_ROT, Math.min((Integer)this.entityData.get(GEAR_ROT) + 5, 85));
            } else {
                this.entityData.set(GEAR_ROT, Math.max((Integer)this.entityData.get(GEAR_ROT) - 5, 0));
            }

            float flapX = (1.0F - Mth.abs(this.getRoll()) / 90.0F) * Mth.clamp((Float)this.entityData.get(MOUSE_SPEED_Y), -22.5F, 22.5F) - VectorTool.calculateY(this.getRoll()) * Mth.clamp((Float)this.entityData.get(MOUSE_SPEED_X), -22.5F, 22.5F);
            this.setFlap1LRot(Mth.clamp(-flapX - 4.0F * addZ - (Float)this.entityData.get(PLANE_BREAK), -22.5F, 22.5F));
            this.setFlap1RRot(Mth.clamp(-flapX + 4.0F * addZ - (Float)this.entityData.get(PLANE_BREAK), -22.5F, 22.5F));
            this.setFlap1L2Rot(Mth.clamp(-flapX - 4.0F * addZ + (Float)this.entityData.get(PLANE_BREAK), -22.5F, 22.5F));
            this.setFlap1R2Rot(Mth.clamp(-flapX + 4.0F * addZ + (Float)this.entityData.get(PLANE_BREAK), -22.5F, 22.5F));
            this.setFlap2LRot(Mth.clamp(flapX - 4.0F * addZ, -22.5F, 22.5F));
            this.setFlap2RRot(Mth.clamp(flapX + 4.0F * addZ, -22.5F, 22.5F));
            float flapY = (1.0F - Mth.abs(this.getRoll()) / 90.0F) * Mth.clamp((Float)this.entityData.get(MOUSE_SPEED_X), -22.5F, 22.5F) + VectorTool.calculateY(this.getRoll()) * Mth.clamp((Float)this.entityData.get(MOUSE_SPEED_Y), -22.5F, 22.5F);
            this.setFlap3Rot(flapY * 5.0F);
        } else if (!this.onGround()) {
            this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - 3.0E-4F, 0.02F));
            this.destroyRot += 0.1F;
            float diffX = 90.0F - this.getXRot();
            this.setXRot(this.getXRot() + diffX * 0.001F * this.destroyRot);
            this.setZRot(this.getRoll() - this.destroyRot);
            this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, -0.03, (double)0.0F));
            this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, (double)(-this.destroyRot) * 0.005, (double)0.0F));
        }

        this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.99F);
        this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) * 0.85F);
        this.entityData.set(PLANE_BREAK, (Float)this.entityData.get(PLANE_BREAK) * 0.8F);
        Matrix4f transform = this.getVehicleTransform(1.0F);
        double flapAngle = (double)((this.getFlap1LRot() + this.getFlap1RRot() + this.getFlap1L2Rot() + this.getFlap1R2Rot()) / 4.0F);
        Vector4f force0 = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f force1 = this.transformPosition(transform, 0.0F, 1.0F, 0.0F);
        Vec3 force = (new Vec3((double)force0.x, (double)force0.y, (double)force0.z)).vectorTo(new Vec3((double)force1.x, (double)force1.y, (double)force1.z));
        this.setDeltaMovement(this.getDeltaMovement().add(force.scale(this.getDeltaMovement().dot(this.getViewVector(1.0F)) * 0.022 * ((double)1.0F + Math.sin((this.onGround() ? (double)25.0F : flapAngle + (double)25.0F) * (double)((float)java.lang.Math.PI / 180F))))));
        this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).scale(0.2 * (double)(Float)this.entityData.get(POWER))));
    }

    public void move(@NotNull MoverType movementType, @NotNull Vec3 movement) {
        if (!this.level().isClientSide()) {
            MobileVehicleEntity.IGNORE_ENTITY_GROUND_CHECK_STEPPING = true;
        }

        if (this.level() instanceof ServerLevel && this.canCollideBlockBeastly()) {
            this.collideBlockBeastly();
        }

        super.move(movementType, movement);
        if (this.level() instanceof ServerLevel) {
            if (this.horizontalCollision) {
                this.collideBlock();
                if (this.canCollideHardBlock()) {
                    this.collideHardBlock();
                }
            }

            if (this.lastTickSpeed < 0.3 || this.collisionCoolDown > 0) {
                return;
            }

            Entity driver = EntityFindUtil.findEntity(this.level(), (String)this.entityData.get(LAST_DRIVER_UUID));
            if (this.verticalCollision) {
                if ((Integer)this.entityData.get(GEAR_ROT) <= 10 && !(Mth.abs(this.getRoll()) > 20.0F)) {
                    if ((double)Mth.abs((float)this.lastTickVerticalSpeed) > 0.4) {
                        this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, (Entity)(driver == null ? this : driver)), (float)((double)96.0F * ((double)Mth.abs((float)this.lastTickVerticalSpeed) - 0.4) * (this.lastTickSpeed - 0.3) * (this.lastTickSpeed - 0.3)));
                        if (!this.level().isClientSide) {
                            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.VEHICLE_STRIKE.get(), this.getSoundSource(), 1.0F, 1.0F);
                        }

                        this.bounceVertical(Direction.getNearest(this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z()).getOpposite());
                    }
                } else {
                    this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, (Entity)(driver == null ? this : driver)), (float)((double)(8.0F + Mth.abs(this.getRoll() * 0.2F)) * (this.lastTickSpeed - 0.3) * (this.lastTickSpeed - 0.3)));
                    if (!this.level().isClientSide) {
                        this.level().playSound((Player)null, this, (SoundEvent)ModSounds.VEHICLE_STRIKE.get(), this.getSoundSource(), 1.0F, 1.0F);
                    }

                    this.bounceVertical(Direction.getNearest(this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z()).getOpposite());
                }
            }

            if (this.horizontalCollision) {
                this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, (Entity)(driver == null ? this : driver)), (float)((double)126.0F * (this.lastTickSpeed - 0.4) * (this.lastTickSpeed - 0.4)));
                this.bounceHorizontal(Direction.getNearest(this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z()).getOpposite());
                if (!this.level().isClientSide) {
                    this.level().playSound((Player)null, this, (SoundEvent)ModSounds.VEHICLE_STRIKE.get(), this.getSoundSource(), 1.0F, 1.0F);
                }

                this.collisionCoolDown = 4;
                this.crash = true;
            }
        }

    }

    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.A_10_ENGINE.get();
    }

    public float getEngineSoundVolume() {
        return (Float)this.entityData.get(POWER) * (this.sprintInputDown ? 5.5F : 3.0F);
    }

    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            float x = 0F;
            float y = 0F;
            float z = 0F;
            y += (float)passenger.getMyRidingOffset();
            Vector4f worldPosition = this.transformPosition(transform, x, y, z );
            passenger.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            callback.accept(passenger, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            this.copyEntityData(passenger);
        }
    }

    public Vec3 driverPos(float ticks) {
        Matrix4f transform = this.getVehicleTransform(ticks);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 1.35F, 4.0F);
        return new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
    }

    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = this.getVehicleTransform(ticks);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 1.35F, 4.15F);
        return new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
    }

    public void copyEntityData(Entity entity) {
        entity.setYHeadRot(entity.getYHeadRot() + this.delta_y);
        entity.setYRot(entity.getYRot() + this.delta_y);
        entity.setYBodyRot(this.getYRot());
    }

    public Matrix4f getVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float)Mth.lerp((double)ticks, this.xo, this.getX()), (float)Mth.lerp((double)ticks, this.yo, this.getY()), (float)Mth.lerp((double)ticks, this.zo, this.getZ()));
        transform.rotate(Axis.YP.rotationDegrees(-Mth.lerp(ticks, this.yRotO, this.getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, this.xRotO, this.getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, this.prevRoll, this.getRoll())));
        return transform;
    }

    public void destroy() {
        if (this.crash) {
            this.crashPassengers();
        } else {
            this.explodePassengers();
        }

        if (this.level() instanceof ServerLevel) {
            CustomExplosion explosion = (new CustomExplosion(this.level(), this, ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, this.getAttacker()), 300.0F, this.getX(), this.getY(), this.getZ(), 8.0F, (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
            explosion.explode();
            ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnHugeExplosionParticles(this.level(), this.position());
        }

        super.destroy();
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/a10_icon.png");
    }

    public Vec3 shootPos(float tickDelta) {
        Matrix4f transform = this.getVehicleTransform(tickDelta);
        Vector4f worldPosition;
        if (this.getWeaponIndex(0) == 0) {
            worldPosition = this.transformPosition(transform, 0.1321625F, -0.56446874F, 7.852106F);
        } else if (this.getWeaponIndex(0) == 1) {
            worldPosition = this.transformPosition(transform, 0.0F, -1.443F, 0.13F);
        } else {
            worldPosition = this.transformPosition(transform, 0.0F, -1.203125F, 0.0625F);
        }

        return new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
    }

    public Vec3 shootVec(float tickDelta) {
        Matrix4f transform = this.getVehicleTransform(tickDelta);
        Vector4f worldPosition;
        Vector4f worldPosition2;
        if (this.getWeaponIndex(0) == 3) {
            worldPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
            worldPosition2 = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        } else {
            worldPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
            worldPosition2 = this.transformPosition(transform, 0.0F, -0.03F, 1.0F);
        }

        return (new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z)).vectorTo(new Vec3((double)worldPosition2.x, (double)worldPosition2.y, (double)worldPosition2.z)).normalize();
    }

    public Float gearRot(float tickDelta) {
        return Mth.lerp(tickDelta, this.gearRotO, (float)(Integer)this.entityData.get(GEAR_ROT));
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
                this.entityData.set(FIRE_TIME, Math.min((Integer)this.entityData.get(FIRE_TIME) + 6, 6));
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
        } else if (this.getWeaponIndex(0) == 1 && (Integer)this.getEntityData().get(LOADED_ROCKET) > 0) {
            HeliRocketEntity heliRocketEntity = ((HeliRocketWeapon)this.getWeapon(0)).create(player);
            Vector4f worldPosition;
            Vector4f worldPosition2;
            if (this.fireIndex == 0) {
                worldPosition = this.transformPosition(transform, -3.9321876F, -1.3868062F, 0.12965F);
                worldPosition2 = this.transformPosition(transform, -3.9171875F, -1.4168062F, 1.12965F);
                this.fireIndex = 1;
            } else if (this.fireIndex == 1) {
                worldPosition = this.transformPosition(transform, -1.56875F, -1.443F, 0.1272F);
                worldPosition2 = this.transformPosition(transform, -1.55375F, -1.4729999F, 1.1272F);
                this.fireIndex = 2;
            } else if (this.fireIndex == 2) {
                worldPosition = this.transformPosition(transform, 1.56875F, -1.443F, 0.1272F);
                worldPosition2 = this.transformPosition(transform, 1.57675F, -1.4729999F, 1.1272F);
                this.fireIndex = 3;
            } else {
                worldPosition = this.transformPosition(transform, 3.9321876F, -1.3868062F, 0.12965F);
                worldPosition2 = this.transformPosition(transform, 3.9401875F, -1.4168062F, 1.12965F);
                this.fireIndex = 0;
            }

            Vec3 shootVec = (new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z)).vectorTo(new Vec3((double)worldPosition2.x, (double)worldPosition2.y, (double)worldPosition2.z)).normalize();
            heliRocketEntity.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            heliRocketEntity.shoot(shootVec.x, shootVec.y, shootVec.z, 8.0F, 0.5F);
            player.level().addFreshEntity(heliRocketEntity);
            BlockPos pos = BlockPos.containing(new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z));
            this.level().playSound((Player)null, pos, (SoundEvent)ModSounds.HELICOPTER_ROCKET_FIRE_3P.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
            this.entityData.set(LOADED_ROCKET, (Integer)this.getEntityData().get(LOADED_ROCKET) - 1);
            Level level = player.level();
            Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

            for(Entity target : level.getEntitiesOfClass(Entity.class, (new AABB(center, center)).inflate((double)5.0F), (e) -> true).stream().sorted(Comparator.comparingDouble((e) -> e.distanceToSqr(center))).toList()) {
                if (target instanceof ServerPlayer) {
                    ServerPlayer serverPlayer = (ServerPlayer)target;
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ShakeClientMessage((double)6.0F, (double)5.0F, (double)12.0F, this.getX(), this.getEyeY(), this.getZ()));
                }
            }

            this.reloadCoolDown = 15;
        } else if (this.getWeaponIndex(0) == 2 && (Integer)this.getEntityData().get(LOADED_BOMB) > 0) {
            Mk82Entity Mk82Entity = ((Mk82Weapon)this.getWeapon(0)).create(player);
            Vector4f worldPosition;
            if ((Integer)this.getEntityData().get(LOADED_BOMB) == 3) {
                worldPosition = this.transformPosition(transform, 0.55625F, -1.203125F, 0.0625F);
            } else if ((Integer)this.getEntityData().get(LOADED_BOMB) == 2) {
                worldPosition = this.transformPosition(transform, 0.0F, -1.203125F, 0.0625F);
            } else {
                worldPosition = this.transformPosition(transform, -0.55625F, -1.203125F, 0.0625F);
            }

            Mk82Entity.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            Mk82Entity.shoot(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z, (float)this.getDeltaMovement().length(), 10.0F);
            player.level().addFreshEntity(Mk82Entity);
            BlockPos pos = BlockPos.containing(new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z));
            this.level().playSound((Player)null, pos, (SoundEvent)ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
            if ((Integer)this.getEntityData().get(LOADED_BOMB) == 3) {
                this.reloadCoolDownBomb = 300;
            }

            this.entityData.set(LOADED_BOMB, (Integer)this.getEntityData().get(LOADED_BOMB) - 1);
        } else if (this.getWeaponIndex(0) == 3 && (Integer)this.getEntityData().get(LOADED_MISSILE) > 0) {
            Agm65Entity Agm65Entity = ((Agm65Weapon)this.getWeapon(0)).create(player);
            Vector4f worldPosition;
            if ((Integer)this.getEntityData().get(LOADED_MISSILE) == 4) {
                worldPosition = this.transformPosition(transform, 5.28F, -1.76F, 1.87F);
            } else if ((Integer)this.getEntityData().get(LOADED_MISSILE) == 3) {
                worldPosition = this.transformPosition(transform, -5.28F, -1.76F, 1.87F);
            } else if ((Integer)this.getEntityData().get(LOADED_MISSILE) == 2) {
                worldPosition = this.transformPosition(transform, 6.63F, -1.55F, 1.83F);
            } else {
                worldPosition = this.transformPosition(transform, -6.63F, -1.55F, 1.83F);
            }

            if (this.locked) {
                Agm65Entity.setTargetUuid(this.getTargetUuid());
            }

            Agm65Entity.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            Agm65Entity.shoot(this.shootVec(1.0F).x, this.shootVec(1.0F).y, this.shootVec(1.0F).z, (float)this.getDeltaMovement().length() + 1.0F, 1.0F);
            player.level().addFreshEntity(Agm65Entity);
            BlockPos pos = BlockPos.containing(new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z));
            this.level().playSound((Player)null, pos, (SoundEvent)ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
            if ((Integer)this.getEntityData().get(LOADED_MISSILE) == 3) {
                this.reloadCoolDownMissile = 400;
            }

            this.entityData.set(LOADED_MISSILE, (Integer)this.getEntityData().get(LOADED_MISSILE) - 1);
        }

    }

    public float shootingVolume() {
        return (float)(Integer)this.entityData.get(FIRE_TIME) * 0.3F;
    }

    public float shootingPitch() {
        return 0.7F + (float)(Integer)this.entityData.get(FIRE_TIME) * 0.05F;
    }

    public int mainGunRpm(Player player) {
        if (this.getWeaponIndex(0) == 2) {
            return 600;
        } else {
            return this.getWeaponIndex(0) == 3 ? 120 : 0;
        }
    }

    public boolean isFiring() {
        return (Integer)this.entityData.get(FIRE_TIME) > 0;
    }

    public boolean canShoot(Player player) {
        if (this.getWeaponIndex(0) != 2 && this.getWeaponIndex(0) != 3) {
            return false;
        } else {
            return (Integer)this.entityData.get(AMMO) > 0;
        }
    }

    public int getAmmoCount(Player player) {
        return (Integer)this.entityData.get(AMMO);
    }

    public boolean banHand(Player player) {
        return true;
    }

    public boolean hidePassenger(Entity entity) {
        return false;
    }

    public int zoomFov() {
        return 3;
    }

    public int getWeaponHeat(Player player) {
        return (Integer)this.entityData.get(HEAT);
    }

    public float getRotX(float tickDelta) {
        return this.getPitch(tickDelta);
    }

    public float getRotY(float tickDelta) {
        return this.getYaw(tickDelta);
    }

    public float getRotZ(float tickDelta) {
        return this.getRoll(tickDelta);
    }

    public float getPower() {
        return (Float)this.entityData.get(POWER);
    }

    public int getDecoy() {
        return (Integer)this.entityData.get(DECOY_COUNT);
    }

    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return (double)0.0F;
    }

    public double getMouseSensitivity() {
        return ClientEventHandler.zoomVehicle ? 0.03 : 0.07;
    }

    public double getMouseSpeedX() {
        return 0.3;
    }

    public double getMouseSpeedY() {
        return 0.3;
    }

    public boolean isEnclosed(int index) {
        return true;
    }

    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/aircraft.png");
    }

    @OnlyIn(Dist.CLIENT)
    public @Nullable Pair<Quaternionf, Quaternionf> getPassengerRotation(Entity entity, float tickDelta) {
        return Pair.of(Axis.XP.rotationDegrees(-this.getViewXRot(tickDelta)), Axis.ZP.rotationDegrees(-this.getRoll(tickDelta)));
    }

    public Matrix4f getClientVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float)Mth.lerp((double)ticks, this.xo, this.getX()), (float)Mth.lerp((double)ticks, this.yo + (double)2.375F, this.getY() + (double)2.375F), (float)Mth.lerp((double)ticks, this.zo, this.getZ()));
        transform.rotate(Axis.YP.rotationDegrees((float)((double)(-Mth.lerp(ticks, this.yRotO, this.getYRot())) + ClientMouseHandler.freeCameraYaw)));
        transform.rotate(Axis.XP.rotationDegrees((float)((double)Mth.lerp(ticks, this.xRotO, this.getXRot()) + ClientMouseHandler.freeCameraPitch)));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, this.prevRoll, this.getRoll())));
        return transform;
    }

    @OnlyIn(Dist.CLIENT)
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        return this.getSeatIndex(player) == 0 ? new Vec2((float)((double)(this.getRotY(partialTicks) - 0.5F * Mth.lerp(partialTicks, this.delta_yo, this.delta_y)) - ClientMouseHandler.freeCameraYaw), (float)((double)(this.getRotX(partialTicks) - 0.5F * Mth.lerp(partialTicks, this.delta_xo, this.delta_x)) + ClientMouseHandler.freeCameraPitch)) : super.getCameraRotation(partialTicks, player, false, false);
    }

    @OnlyIn(Dist.CLIENT)
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (this.getSeatIndex(player) == 0) {
            Matrix4f transform = this.getClientVehicleTransform(partialTicks);
            Vector4f maxCameraPosition = this.transformPosition(transform, 0.0F, 4.0F, -14.0F - (float)ClientMouseHandler.custom3pDistanceLerp);
            Vec3 finalPos = CameraTool.getMaxZoom(transform, maxCameraPosition);
            return isFirstPerson ? new Vec3(Mth.lerp((double)partialTicks, player.xo, player.getX()), Mth.lerp((double)partialTicks, player.yo + (double)player.getEyeHeight(), player.getEyeY()), Mth.lerp((double)partialTicks, player.zo, player.getZ())) : finalPos;
        } else {
            return super.getCameraPosition(partialTicks, player, false, false);
        }
    }
}
