package Aru.Aru.ashvehicle.entity;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.*;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.VectorTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import Aru.Aru.ashvehicle.ExtensionTest;
import Aru.Aru.ashvehicle.entity.Class.BaseAircraftEntity;
import Aru.Aru.ashvehicle.entity.weapon.Aam4Entity;
import Aru.Aru.ashvehicle.entity.weapon.Aam4Weapon;
import Aru.Aru.ashvehicle.init.ModEntities;


import java.util.Comparator;

public class F22Entity extends BaseAircraftEntity {

    public static final EntityDataAccessor<Boolean> AFTERBURNER_ACTIVE =
            SynchedEntityData.defineId(F22Entity.class, EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AFTERBURNER_ACTIVE, false);
    }

    public boolean isAfterburnerActive() {
        return this.entityData.get(AFTERBURNER_ACTIVE);
    }

    public void setAfterburnerActive(boolean active) {
        this.entityData.set(AFTERBURNER_ACTIVE, active);
    }

    public F22Entity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public F22Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this((EntityType) ModEntities.F_22.get(), level);
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            float x = 0.0F;
            float y = 1.5F;
            float z = 4.2F;
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
                    .sound((SoundEvent)ModSounds.INTO_CANNON.get())
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
        int ammoCount = this.countItem((Item)ModItems.SMALL_SHELL.get());

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

    @Override
    public ResourceLocation getVehicleIcon() {
        return new ResourceLocation(ExtensionTest.MODID, "textures/icon/vehicle/f-22-icon.png");
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

    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void travel() {
        Entity passenger = this.getFirstPassenger();
        if (this.getHealth() > 0.1F * this.getMaxHealth()) {
            if (passenger != null && !this.isInWater()) {
                if (passenger instanceof Player) {
                    if (this.getEnergy() > 0) {
                        float newPower = this.entityData.get(POWER);
                        if (this.forwardInputDown) {
                            newPower = Math.min(this.entityData.get(POWER) + 0.004F, this.sprintInputDown ? 1.0F : 0.0575F);
                            this.entityData.set(POWER, newPower);
                        }

                        if (this.backInputDown) {
                            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.002F, -0.2F));
                        }
                        this.setAfterburnerActive(newPower > 0.06F);
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
}
