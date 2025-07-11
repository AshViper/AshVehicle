package Aru.Aru.ashvehicle.entity;

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

import java.util.Comparator;

public class F4Entity extends BaseAircraftEntity {
    public F4Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public F4Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this((EntityType) ModEntities.F_4.get(), level);
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            float x = 0.0F;
            float y = 1.4F;
            float z = 3.7F;
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
                (new Aam4Weapon()).sound((SoundEvent)ModSounds.INTO_MISSILE.get())
        }};
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
        int ammoCount = this.countItem((Item) ModItems.SMALL_SHELL.get());

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
            this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_MISSILE));
        }

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
        } else if (this.getWeaponIndex(0) == 1 && (Integer)this.getEntityData().get(LOADED_MISSILE) > 0) {
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
            if ((Integer)this.getEntityData().get(LOADED_MISSILE) == 3) {
                this.reloadCoolDownMissile = 100;
            }

            this.entityData.set(LOADED_MISSILE, (Integer)this.getEntityData().get(LOADED_MISSILE) - 1);
        }
    }
    private boolean wasFiring;
    @Override
    public void baseTick() {
        if (!this.wasFiring && this.isFiring() && this.level().isClientSide()) {
            fireSound.accept(this);
        }

        this.wasFiring = this.isFiring();
        this.lockingTargetO = this.getTargetUuid();
        this.delta_xo = this.delta_x;
        this.delta_yo = this.delta_y;
        super.baseTick();
        float f = (float) Mth.clamp(Math.max((double)(this.onGround() ? 0.819F : 0.82F) - 0.0035 * this.getDeltaMovement().length(), (double)0.5F) + (double)(0.001F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F), 0.01, 0.99);
        boolean forward = this.getDeltaMovement().dot(this.getViewVector(1.0F)) > (double)0.0F;
        this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).scale((forward ? 0.227 : 0.1) * this.getDeltaMovement().dot(this.getViewVector(1.0F)))));
        this.setDeltaMovement(this.getDeltaMovement().multiply((double)f, (double)f, (double)f));
        if (this.isInWater() && this.tickCount % 4 == 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 0.6, 0.6));
            if (this.lastTickSpeed > 0.4) {
                this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, (Entity)(this.getFirstPassenger() == null ? this : this.getFirstPassenger())), (float)((double)20.0F * (this.lastTickSpeed - 0.4) * (this.lastTickSpeed - 0.4)));
            }
        }

        if (this.level() instanceof ServerLevel) {
            if (this.reloadCoolDown > 0) {
                --this.reloadCoolDown;
            }

            if (this.reloadCoolDownBomb > 0) {
                --this.reloadCoolDownBomb;
            }

            if (this.reloadCoolDownMissile > 0) {
                --this.reloadCoolDownMissile;
            }

            this.handleAmmo();
        }

        Entity var4 = this.getFirstPassenger();
        if (var4 instanceof Player player) {
            if (this.fireInputDown) {
                if (this.getWeaponIndex(0) == 0) {
                    if (((Integer)this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !this.cannotFire) {
                        this.vehicleShoot(player, 0);
                    }
                } else if (this.getWeaponIndex(0) == 1 && (Integer)this.entityData.get(AMMO) > 0) {
                    this.vehicleShoot(player, 0);
                }
            }
        }

        if (this.onGround()) {
            this.terrainCompactA10();
        }

        if ((Integer)this.entityData.get(FIRE_TIME) > 0) {
            this.entityData.set(FIRE_TIME, (Integer)this.entityData.get(FIRE_TIME) - 1);
        }

        if (this.getWeaponIndex(0) == 1) {
            this.seekTarget();
        }

        this.lowHealthWarning();
        this.releaseDecoy();
        this.refreshDimensions();
    }
}
