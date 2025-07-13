package Aru.Aru.ashvehicle.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.projectile.HeliRocketEntity;
import com.atsuishio.superbwarfare.entity.projectile.Mk82Entity;
import com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.HeliRocketWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.Mk82Weapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import Aru.Aru.ashvehicle.entity.weapon.Aam4Entity;
import Aru.Aru.ashvehicle.entity.weapon.Aam4Weapon;
import Aru.Aru.ashvehicle.init.ModEntities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SU33Entity extends BaseAircraftEntity {
    private boolean wasFiring;
    public boolean isDoingCobra = false;
    public boolean isDoingKulbit = false;
    private int maneuverTick = 0;

    public SU33Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SU33Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this((EntityType) ModEntities.SU_33.get(), level);
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            float x = 0.02F;
            float y = 1.6F;
            float z = 4.45F;
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
                (new HeliRocketWeapon())
                        .damage((float)(Integer)VehicleConfig.A_10_ROCKET_DAMAGE.get())
                        .explosionDamage((float)(Integer)VehicleConfig.A_10_ROCKET_EXPLOSION_DAMAGE.get())
                        .explosionRadius(((Double)VehicleConfig.A_10_ROCKET_EXPLOSION_RADIUS.get()).floatValue())
                        .sound((SoundEvent)ModSounds.INTO_MISSILE.get()),
                (new Mk82Weapon()).sound((SoundEvent)ModSounds.INTO_MISSILE.get()),
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
            this.level().playSound((Player)null, pos, (SoundEvent) ModSounds.HELICOPTER_ROCKET_FIRE_3P.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
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
            Aam4Entity aam4Entity = ((Aam4Weapon)this.getWeapon(0)).create(player);
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
                aam4Entity.setTargetUuid(this.getTargetUuid());
            }

            aam4Entity.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            aam4Entity.shoot(this.shootVec(1.0F).x, this.shootVec(1.0F).y, this.shootVec(1.0F).z, (float)this.getDeltaMovement().length() + 1.0F, 1.0F);
            player.level().addFreshEntity(aam4Entity);
            BlockPos pos = BlockPos.containing(new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z));
            this.level().playSound((Player)null, pos, (SoundEvent)ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
            this.reloadCoolDownMissile = 20;

            this.entityData.set(LOADED_MISSILE, (Integer)this.getEntityData().get(LOADED_MISSILE) - 1);
        }
    }

    @Override
    public List<Vec3> getAfterburnerParticlePositions() {
        List<Vec3> positions = new ArrayList<>();
        // 後方2.2、上1.0、左右-7と7（Z軸を左右方向とした場合）
        positions.add(new Vec3(-7, 1.5, -1));  // ローカル座標
        positions.add(new Vec3(-7, 1.5, 1));
        return positions;
    }
}
