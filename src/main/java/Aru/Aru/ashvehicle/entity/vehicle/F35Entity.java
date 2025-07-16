package Aru.Aru.ashvehicle.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import Aru.Aru.ashvehicle.entity.Class.BaseAircraftEntity;
import Aru.Aru.ashvehicle.entity.weapon.*;
import Aru.Aru.ashvehicle.init.ModEntities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class F35Entity extends BaseAircraftEntity {
    private boolean wasFiring;

    public F35Entity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public F35Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this((EntityType) ModEntities.F_35.get(), level);
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            float x = 0.0F;
            float y = 1.6F;
            float z = 4.7F;
            y += (float)passenger.getMyRidingOffset();
            Vector4f worldPosition = this.transformPosition(transform, x, y, z );
            passenger.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            callback.accept(passenger, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            this.copyEntityData(passenger);
        }
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{
                (new SmallCannonShellWeapon())
                        .damage((float)(Integer) VehicleConfig.A_10_CANNON_DAMAGE.get())
                        .explosionDamage((float)(Integer)VehicleConfig.A_10_CANNON_EXPLOSION_DAMAGE.get())
                        .explosionRadius(((Double)VehicleConfig.A_10_CANNON_EXPLOSION_RADIUS.get()).floatValue())
                        .sound((SoundEvent) ModSounds.INTO_CANNON.get())
                        .icon(Mod.loc("textures/screens/vehicle_weapon/cannon_30mm.png")),
                (new JassmXRWeapon())
                        .damage((float)(Integer)VehicleConfig.A_10_ROCKET_DAMAGE.get())
                        .explosionDamage((float)(Integer)VehicleConfig.A_10_ROCKET_EXPLOSION_DAMAGE.get())
                        .explosionRadius(((Double)VehicleConfig.A_10_ROCKET_EXPLOSION_RADIUS.get()).floatValue())
                        .sound((SoundEvent)ModSounds.INTO_MISSILE.get()),
                (new Aam4Weapon()).sound((SoundEvent)ModSounds.INTO_MISSILE.get())
        }};
    }

    @Override
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
                    this.getItemStacks().stream().filter((stack) -> stack.is((Item) ModItems.SMALL_SHELL.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
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
            JassmXREntity heliRocketEntity = ((JassmXRWeapon)this.getWeapon(0)).create(player);
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
            this.level().playSound((Player)null, pos, (SoundEvent) ModSounds.SMALL_ROCKET_FIRE_3P.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
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
        } else if(this.getWeaponIndex(0) == 2 && this.getEntityData().get(LOADED_MISSILE) > 0) {
            int loaded = this.getEntityData().get(LOADED_MISSILE);
            int countToFire = Math.min(loaded, this.lockedTargets.size()); // ロック数か残弾数の少ない方

            List<UUID> targets = new ArrayList<>(this.lockedTargets);
            for (int i = 0; i < countToFire; i++) {
                UUID targetUuid = targets.get(i);
                Aam4Entity aam4Entity = ((Aam4Weapon)this.getWeapon(0)).create(player);

                // 発射位置（番号ごとに変える）
                Vector4f worldPosition;
                switch (loaded) {
                    case 4 -> worldPosition = this.transformPosition(transform, 5.28F, -1.76F, 1.87F);
                    case 3 -> worldPosition = this.transformPosition(transform, -5.28F, -1.76F, 1.87F);
                    case 2 -> worldPosition = this.transformPosition(transform, 6.63F, -1.55F, 1.83F);
                    default -> worldPosition = this.transformPosition(transform, -6.63F, -1.55F, 1.83F);
                }

                aam4Entity.setTargetUuid(String.valueOf(targetUuid));
                aam4Entity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
                aam4Entity.shoot(this.shootVec(1.0F).x, this.shootVec(1.0F).y, this.shootVec(1.0F).z, (float)this.getDeltaMovement().length() + 1.0F, 1.0F);

                player.level().addFreshEntity(aam4Entity);

                BlockPos pos = BlockPos.containing(worldPosition.x, worldPosition.y, worldPosition.z);
                this.level().playSound(null, pos, ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3.0F, 1.0F);

                // ミサイル数と発射後の処理
                loaded--;
                this.entityData.set(LOADED_MISSILE, loaded);

                if (loaded == 3) {
                    this.reloadCoolDownMissile = 25;
                }

                if (loaded <= 0) break;
            }
        }
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == ModItems.SMALL_ROCKET.get() && (Integer)this.entityData.get(LOADED_ROCKET) <= 4) {
            this.entityData.set(LOADED_ROCKET, (Integer)this.entityData.get(LOADED_ROCKET) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        } else if (stack.getItem() == ModItems.AGM.get() && (Integer)this.entityData.get(LOADED_MISSILE) <= 6) {
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

    @Override
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

        if (!this.onGround() && this.getDeltaMovement().length() > 1.0) {
            float flapL = this.getFlap2LRot(); // 左エルロン
            float flapR = this.getFlap2RRot(); // 右エルロン

            boolean hardTurn = Math.abs(flapL) > 15.0F || Math.abs(flapR) > 15.0F;

            if (hardTurn && this.level() instanceof ServerLevel serverLevel && this.tickCount % 2 == 0) {
                Matrix4f transform = this.getVehicleTransform(1.0F);

                // 主翼の付け根位置（左右）をモデルに合わせて調整
                Vector4f wingRootL = this.transformPosition(transform, -3.0F, 2.6F, 0.0F); // 左
                Vector4f wingRootR = this.transformPosition(transform, 3.0F, 2.6F, 0.0F);  // 右

                // パーティクル生成（CLOUDまたは自作）
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD,
                        wingRootL.x, wingRootL.y, wingRootL.z,
                        5, 0, 0, 0, 0.01, true);

                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD,
                        wingRootR.x, wingRootR.y, wingRootR.z,
                        5, 0, 0, 0, 0.01, true);
            }
        }

        // 🎯 ミサイルロック処理
        if (this.getWeaponIndex(0) == 2) {
            this.seekTargets();
        }

        // ⚠️ 警告/防衛処理
        this.lowHealthWarning();
        this.releaseDecoy();
        this.refreshDimensions();
    }

    @Override
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
        if ((this.hasItem((Item)ModItems.SMALL_ROCKET.get()) || hasCreativeAmmoBox) && this.reloadCoolDown == 0 && (Integer)this.getEntityData().get(LOADED_ROCKET) < 4) {
            this.entityData.set(LOADED_ROCKET, (Integer)this.getEntityData().get(LOADED_ROCKET) + 1);
            this.reloadCoolDown = 200;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.SMALL_ROCKET.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
        }

        if ((this.hasItem((Item)ModItems.AGM.get()) || hasCreativeAmmoBox) && this.reloadCoolDownMissile == 0 && (Integer)this.getEntityData().get(LOADED_MISSILE) < 6) {
            this.entityData.set(LOADED_MISSILE, (Integer)this.getEntityData().get(LOADED_MISSILE) + 1);
            this.reloadCoolDownMissile = 100;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.AGM.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
        }

        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, ammoCount);
        } else if (this.getWeaponIndex(0) == 1) {
            this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_ROCKET));
        }else if (this.getWeaponIndex(0) == 2) {
            this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_MISSILE));
        }
    }

    @Override
    public List<Vec3> getAfterburnerParticlePositions() {
        List<Vec3> positions = new ArrayList<>();
        // 後方2.2、上1.0、左右-7と7（Z軸を左右方向とした場合）
        positions.add(new Vec3(-8, 2.0, 0));  // ローカル座標
        return positions;
    }
}
