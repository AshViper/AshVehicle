package Aru.Aru.ashvehicle.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity;
import com.atsuishio.superbwarfare.entity.projectile.SmallRocketEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.HelicopterEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallRocketWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PlayMessages;
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
import Aru.Aru.ashvehicle.init.ModEntities;

import java.util.Objects;
import java.util.stream.Stream;

public class MH60MEntity extends ContainerMobileVehicleEntity implements GeoEntity, HelicopterEntity, WeaponVehicleEntity {
    private final AnimatableInstanceCache cache;
    public static final EntityDataAccessor<Float> PROPELLER_ROT;
    public static final EntityDataAccessor<Integer> LOADED_ROCKET;
    public boolean engineStart;
    public boolean engineStartOver;
    public double velocity;
    public int fireIndex;
    public int holdTick;
    public int holdPowerTick;
    public float destroyRot;
    public float delta_xo;
    public float delta_yo;
    public float delta_x;
    public float delta_y;

    public MH60MEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.cache = GeckoLibUtil.createInstanceCache(this);
    }

    public MH60MEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this((EntityType) ModEntities.MH_60M.get(), level);
    }

    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{
            (new SmallCannonShellWeapon()
            ).blockInteraction((Boolean)VehicleConfig.AH_6_CANNON_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP).damage(((Double)VehicleConfig.AH_6_CANNON_DAMAGE.get()).floatValue()).explosionDamage(((Double)VehicleConfig.AH_6_CANNON_EXPLOSION_DAMAGE.get()).floatValue()).explosionRadius(((Double)VehicleConfig.AH_6_CANNON_EXPLOSION_RADIUS.get()).floatValue()).sound((SoundEvent)ModSounds.INTO_CANNON.get()).icon(Mod.loc("textures/screens/vehicle_weapon/cannon_20mm.png")).sound1p((SoundEvent)ModSounds.HELICOPTER_CANNON_FIRE_1P.get()).sound3p((SoundEvent)ModSounds.HELICOPTER_CANNON_FIRE_3P.get()).sound3pFar((SoundEvent)ModSounds.HELICOPTER_CANNON_FAR.get()).sound3pVeryFar((SoundEvent)ModSounds.HELICOPTER_CANNON_VERYFAR.get()), (new SmallRocketWeapon()).damage(((Double)VehicleConfig.AH_6_ROCKET_DAMAGE.get()).floatValue()).explosionDamage(((Double)VehicleConfig.AH_6_ROCKET_EXPLOSION_DAMAGE.get()).floatValue()).explosionRadius(((Double)VehicleConfig.AH_6_ROCKET_EXPLOSION_RADIUS.get()).floatValue()).sound((SoundEvent)ModSounds.INTO_MISSILE.get()).sound1p((SoundEvent)ModSounds.SMALL_ROCKET_FIRE_3P.get()).sound3p((SoundEvent)ModSounds.SMALL_ROCKET_FIRE_3P.get())}};
    }

    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition((double)7.0F, (double)1.0F, -2.7);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOADED_ROCKET, 0);
        this.entityData.define(PROPELLER_ROT, 0.0F);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedRocket", (Integer)this.entityData.get(LOADED_ROCKET));
        compound.putFloat("PropellerRot", (Float)this.entityData.get(PROPELLER_ROT));
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_ROCKET, compound.getInt("LoadedRocket"));
        this.entityData.set(PROPELLER_ROT, compound.getFloat("PropellerRot"));
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> {
            Entity entity = source.getDirectEntity();
            if (entity != null && entity.getType().is(ModTags.EntityTypes.AERIAL_BOMB)) {
                damage = damage * 2.0F;
            }

            damage = damage * (this.getHealth() > 0.1F ? 0.7F : 0.05F);
            return damage;
        });
    }

    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == ModItems.SMALL_ROCKET.get() && (Integer)this.entityData.get(LOADED_ROCKET) < 14) {
            this.entityData.set(LOADED_ROCKET, (Integer)this.entityData.get(LOADED_ROCKET) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        } else {
            return super.interact(player, hand);
        }
    }

    public void baseTick() {
        this.delta_xo = this.delta_x;
        this.delta_yo = this.delta_y;
        super.baseTick();
        if (this.level() instanceof ServerLevel) {
            if (this.reloadCoolDown > 0) {
                --this.reloadCoolDown;
            }

            this.handleAmmo();
        }

        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, (double)1.0F, 0.8));
        } else {
            this.setZRot(this.getRoll() * (this.backInputDown ? 0.9F : 0.99F));
            float f = (float)Mth.clamp((double)0.95F - 0.015 * this.getDeltaMovement().length() + (double)(0.02F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F), 0.01, 0.99);
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).scale((this.getXRot() < 0.0F ? -0.035 : (this.getXRot() > 0.0F ? 0.035 : (double)0.0F)) * this.getDeltaMovement().length())));
            this.setDeltaMovement(this.getDeltaMovement().multiply((double)f, 0.95, (double)f));
        }

        if (this.isInWater() && this.tickCount % 4 == 0 && this.getSubmergedHeight(this) > (double)0.5F * (double)this.getBbHeight()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 0.6, 0.6));
            this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, (Entity)(this.getFirstPassenger() == null ? this : this.getFirstPassenger())), 6.0F + (float)((double)20.0F * (this.lastTickSpeed - 0.4) * (this.lastTickSpeed - 0.4)));
        }

        this.releaseDecoy();
        this.lowHealthWarning();
        this.terrainCompact(2.7F, 2.7F);
        this.refreshDimensions();
    }

    private void handleAmmo() {
        Entity ammoCount = this.getFirstPassenger();
        if (ammoCount instanceof Player player) {
            Stream var10000 = this.getItemStacks().stream().filter((stack) -> {
                if (stack.is((Item)ModItems.AMMO_BOX.get())) {
                    return Ammo.HEAVY.get(stack) > 0;
                } else {
                    return false;
                }
            });
            Ammo var10001 = Ammo.HEAVY;
            Objects.requireNonNull(var10001);
            int var3 = var10000.mapToInt(stack -> var10001.get((ItemStack) stack)).sum() + this.countItem((Item)ModItems.SMALL_SHELL.get());
            if ((this.hasItem((Item)ModItems.SMALL_ROCKET.get()) || InventoryTool.hasCreativeAmmoBox(player)) && this.reloadCoolDown == 0 && (Integer)this.getEntityData().get(LOADED_ROCKET) < 14) {
                this.entityData.set(LOADED_ROCKET, (Integer)this.getEntityData().get(LOADED_ROCKET) + 1);
                this.reloadCoolDown = 25;
                if (!InventoryTool.hasCreativeAmmoBox(player)) {
                    this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.SMALL_ROCKET.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
                }

                this.level().playSound((Player)null, this, (SoundEvent)ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 1.0F, 1.0F);
            }

            if (this.getWeaponIndex(0) == 0) {
                this.entityData.set(AMMO, var3);
            } else {
                this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_ROCKET));
            }

        }
    }

    public void travel() {
        Entity passenger = this.getFirstPassenger();
        Entity passenger2 = this.getNthEntity(1);
        Entity passenger3 = this.getNthEntity(2);
        Entity passenger4 = this.getNthEntity(3);
        if (this.getHealth() > 0.1F * this.getMaxHealth()) {
            if (passenger == null) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.upInputDown = false;
                this.downInputDown = false;
                this.setZRot(this.roll * 0.98F);
                this.setXRot(this.getXRot() * 0.98F);
                if (passenger2 == null && passenger3 == null && passenger4 == null) {
                    this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.99F);
                }
            } else if (passenger instanceof Player) {
                if (this.rightInputDown) {
                    ++this.holdTick;
                    this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) - 2.0F * (float)Math.min(this.holdTick, 7) * (Float)this.entityData.get(POWER));
                } else if (this.leftInputDown) {
                    ++this.holdTick;
                    this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) + 2.0F * (float)Math.min(this.holdTick, 7) * (Float)this.entityData.get(POWER));
                } else {
                    this.holdTick = 0;
                }

                this.delta_x = (this.onGround() ? 0.0F : 1.5F) * (Float)this.entityData.get(MOUSE_SPEED_Y) * (Float)this.entityData.get(PROPELLER_ROT);
                this.delta_y = Mth.clamp((this.onGround() ? 0.1F : 2.0F) * (Float)this.entityData.get(MOUSE_SPEED_X) * (Float)this.entityData.get(PROPELLER_ROT), -10.0F, 10.0F);
                this.setYRot(this.getYRot() + this.delta_y);
                this.setXRot(this.getXRot() + this.delta_x);
                this.setZRot(this.getRoll() - (Float)this.entityData.get(DELTA_ROT) + (this.onGround() ? 0.0F : 0.25F) * (Float)this.entityData.get(MOUSE_SPEED_X) * (Float)this.entityData.get(PROPELLER_ROT));
            }

            if (this.level() instanceof ServerLevel) {
                if (this.getEnergy() > 0) {
                    boolean up = this.upInputDown || this.forwardInputDown;
                    boolean down = this.downInputDown;
                    if (!this.engineStart && up) {
                        this.engineStart = true;
                        this.level().playSound((Player)null, this, (SoundEvent)ModSounds.HELICOPTER_ENGINE_START.get(), this.getSoundSource(), 3.0F, 1.0F);
                    }

                    if (up && this.engineStartOver) {
                        ++this.holdPowerTick;
                        this.entityData.set(POWER, Math.min((Float)this.entityData.get(POWER) + 7.0E-4F * (float)Math.min(this.holdPowerTick, 10), 0.12F));
                    }

                    if (this.engineStartOver) {
                        if (down) {
                            ++this.holdPowerTick;
                            this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - 0.001F * (float)Math.min(this.holdPowerTick, 5), this.onGround() ? 0.0F : 0.025F));
                        } else if (this.backInputDown) {
                            ++this.holdPowerTick;
                            this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - 0.001F * (float)Math.min(this.holdPowerTick, 5), this.onGround() ? 0.0F : 0.052F));
                            if (passenger != null) {
                                passenger.setXRot(0.8F * passenger.getXRot());
                            }
                        }
                    }

                    if (this.engineStart && !this.engineStartOver) {
                        this.entityData.set(POWER, Math.min((Float)this.entityData.get(POWER) + 0.0012F, 0.045F));
                    }

                    if (!up && !down && !this.backInputDown && this.engineStartOver) {
                        if (this.getDeltaMovement().y() < (double)0.0F) {
                            this.entityData.set(POWER, Math.min((Float)this.entityData.get(POWER) + 2.0E-4F, 0.12F));
                        } else {
                            this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - (this.onGround() ? 5.0E-5F : 2.0E-4F), 0.0F));
                        }

                        this.holdPowerTick = 0;
                    }
                } else {
                    this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - 1.0E-4F, 0.0F));
                    this.forwardInputDown = false;
                    this.backInputDown = false;
                    this.engineStart = false;
                    this.engineStartOver = false;
                }
            }
        } else if (!this.onGround() && this.engineStartOver) {
            this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - 3.0E-4F, 0.01F));
            this.destroyRot += 0.08F;
            float diffX = 45.0F - this.getXRot();
            float diffZ = -20.0F - this.getRoll();
            this.setXRot(this.getXRot() + diffX * 0.05F * (Float)this.entityData.get(PROPELLER_ROT));
            this.setYRot(this.getYRot() + this.destroyRot);
            this.setZRot(this.getRoll() + diffZ * 0.1F * (Float)this.entityData.get(PROPELLER_ROT));
            this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, (double)(-this.destroyRot) * 0.004, (double)0.0F));
        }

        this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) * 0.9F);
        this.entityData.set(PROPELLER_ROT, Mth.lerp(0.18F, (Float)this.entityData.get(PROPELLER_ROT), (Float)this.entityData.get(POWER)));
        this.setPropellerRot(this.getPropellerRot() + 30.0F * (Float)this.entityData.get(PROPELLER_ROT));
        this.entityData.set(PROPELLER_ROT, (Float)this.entityData.get(PROPELLER_ROT) * 0.9995F);
        if (this.engineStart) {
            this.consumeEnergy((int)((double)(Integer)VehicleConfig.AH_6_MIN_ENERGY_COST.get() + (double)(Float)this.entityData.get(POWER) * ((double)((Integer)VehicleConfig.AH_6_MAX_ENERGY_COST.get() - (Integer)VehicleConfig.AH_6_MIN_ENERGY_COST.get()) / 0.12)));
        }

        Matrix4f transform = this.getVehicleTransform(1.0F);
        Vector4f force0 = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f force1 = this.transformPosition(transform, 0.0F, 1.0F, 0.0F);
        Vec3 force = (new Vec3((double)force0.x, (double)force0.y, (double)force0.z)).vectorTo(new Vec3((double)force1.x, (double)force1.y, (double)force1.z));
        this.setDeltaMovement(this.getDeltaMovement().add(force.scale((double)(Float)this.entityData.get(POWER))));
        if ((Float)this.entityData.get(POWER) > 0.04F) {
            this.engineStartOver = true;
        }

        if ((Float)this.entityData.get(POWER) < 4.0E-4F) {
            this.engineStart = false;
            this.engineStartOver = false;
        }

    }

    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.HELICOPTER_ENGINE.get();
    }

    public float getEngineSoundVolume() {
        return (Float)this.entityData.get(PROPELLER_ROT) * 2.0F;
    }

    protected void clampRotation(Entity entity) {
        if (entity == this.getNthEntity(0)) {
            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, -80.0F, 80.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(this.getYRot());
        } else if (entity == this.getNthEntity(1)) {
            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, -80.0F, 80.0F);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);
            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, -80.0F, 80.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(this.getYRot());
        } else if (entity == this.getNthEntity(2)) {
            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, 10.0F, 170.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(this.getYRot() + 90.0F);
        } else if (entity == this.getNthEntity(3)) {
            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, -170.0F, -10.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(this.getYRot() - 90.0F);
        }

    }

    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            float x = 0.7F;
            float y = 1.1F;
            float z = 2.5F;
            y += (float)passenger.getMyRidingOffset();
            int i = this.getOrderedPassengers().indexOf(passenger);
            if (i == 0) {
                Vector4f worldPosition = this.transformPosition(transform, x, y, z);
                passenger.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
                callback.accept(passenger, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            } else if (i == 1) {
                Vector4f worldPosition = this.transformPosition(transform, -x, y, z);
                passenger.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
                callback.accept(passenger, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            } else if (i == 2) {
                Vector4f worldPosition = this.transformPosition(transform, -1.1F, 0.4F, -1.6F);
                passenger.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
                callback.accept(passenger, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            } else if (i == 3) {
                Vector4f worldPosition = this.transformPosition(transform, 1.1F, 0.4F, -1.6F);
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
        if (entity == this.getNthEntity(0)) {
            entity.setYHeadRot(entity.getYHeadRot() + this.delta_y);
            entity.setYRot(entity.getYRot() + this.delta_y);
            entity.setYBodyRot(this.getYRot());
        } else if (entity == this.getNthEntity(1)) {
            float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float g = Mth.clamp(f, -105.0F, 105.0F);
            entity.yRotO += g - f;
            entity.setYRot(entity.getYRot() + g - f + 0.9F * this.destroyRot);
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(this.getYRot());
        } else if (entity == this.getNthEntity(2)) {
            float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float g = Mth.clamp(f, 10.0F, 170.0F);
            entity.yRotO += g - f;
            entity.setYRot(entity.getYRot() + g - f + 0.9F * this.destroyRot);
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(this.getYRot() + 90.0F);
        } else if (entity == this.getNthEntity(3)) {
            float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float g = Mth.clamp(f, -170.0F, -10.0F);
            entity.yRotO += g - f;
            entity.setYRot(entity.getYRot() + g - f + 0.9F * this.destroyRot);
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(this.getYRot() - 90.0F);
        }

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
            CustomExplosion explosion = (new CustomExplosion(this.level(), this, ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, this.getAttacker()), 300.0F, this.getX(), this.getY(), this.getZ(), 8.0F, (Boolean) ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
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

    public Vec3 shootPos(float tickDelta) {
        Matrix4f transform = this.getVehicleTransform(tickDelta);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, -0.83F, 0.8F);
        return new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
    }

    public Vec3 shootVec(float tickDelta) {
        Matrix4f transform = this.getVehicleTransform(tickDelta);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f worldPosition2 = this.transformPosition(transform, 0.0F, 0.01F, 1.0F);
        return (new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z)).vectorTo(new Vec3((double)worldPosition2.x, (double)worldPosition2.y, (double)worldPosition2.z)).normalize();
    }

    public void vehicleShoot(Player player, int type) {
        boolean hasCreativeAmmo = false;

        for(int i = 0; i < this.getMaxPassengers() - 1; ++i) {
            Entity var6 = this.getNthEntity(i);
            if (var6 instanceof Player pPlayer) {
                if (InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                    hasCreativeAmmo = true;
                }
            }
        }

        Matrix4f transform = this.getVehicleTransform(1.0F);
        if (this.getWeaponIndex(0) == 0) {
            if (this.cannotFire) {
                return;
            }

            Vector4f worldPosition;
            Vector4f worldPosition2;
            if (this.fireIndex == 0) {
                worldPosition = this.transformPosition(transform, 1.15F, -0.83F, 0.8F);
                worldPosition2 = this.transformPosition(transform, 1.157F, -0.81799996F, 1.8F);
                this.fireIndex = 1;
            } else {
                worldPosition = this.transformPosition(transform, -1.15F, -0.83F, 0.8F);
                worldPosition2 = this.transformPosition(transform, -1.139F, -0.81799996F, 1.8F);
                this.fireIndex = 0;
            }

            Vec3 shootVec = (new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z)).vectorTo(new Vec3((double)worldPosition2.x, (double)worldPosition2.y, (double)worldPosition2.z)).normalize();
            if ((Integer)this.entityData.get(AMMO) > 0 || hasCreativeAmmo) {
                SmallCannonShellEntity entityToSpawn = ((SmallCannonShellWeapon)this.getWeapon(0)).create(player);
                entityToSpawn.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
                entityToSpawn.shoot(shootVec.x, shootVec.y, shootVec.z, 20.0F, 0.15F);
                this.level().addFreshEntity(entityToSpawn);
                ParticleTool.sendParticle((ServerLevel)this.level(), ParticleTypes.LARGE_SMOKE, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, false);
                if (!hasCreativeAmmo) {
                    ItemStack ammoBox = (ItemStack)this.getItemStacks().stream().filter((stack) -> {
                        if (stack.is((Item)ModItems.AMMO_BOX.get())) {
                            return Ammo.HEAVY.get(stack) > 0;
                        } else {
                            return false;
                        }
                    }).findFirst().orElse(ItemStack.EMPTY);
                    if (!ammoBox.isEmpty()) {
                        Ammo.HEAVY.add(ammoBox, -1);
                    } else {
                        this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.SMALL_SHELL.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
                    }
                }
            }

            this.entityData.set(HEAT, (Integer)this.entityData.get(HEAT) + 4);
            if (!player.level().isClientSide) {
                this.playShootSound3p(player, 0, 4, 12, 24);
            }
        } else if (this.getWeaponIndex(0) == 1 && (Integer)this.getEntityData().get(LOADED_ROCKET) > 0) {
            SmallRocketEntity heliRocketEntity = ((SmallRocketWeapon)this.getWeapon(0)).create(player);
            Vector4f worldPosition;
            Vector4f worldPosition2;
            if (this.fireIndex == 0) {
                worldPosition = this.transformPosition(transform, 1.7F, -0.83F, 0.8F);
                worldPosition2 = this.transformPosition(transform, 1.7064999F, -0.81799996F, 1.8F);
                this.fireIndex = 1;
            } else {
                worldPosition = this.transformPosition(transform, -1.7F, -0.83F, 0.8F);
                worldPosition2 = this.transformPosition(transform, -1.6885F, -0.81799996F, 1.8F);
                this.fireIndex = 0;
            }

            Vec3 shootVec = (new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z)).vectorTo(new Vec3((double)worldPosition2.x, (double)worldPosition2.y, (double)worldPosition2.z)).normalize();
            heliRocketEntity.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            heliRocketEntity.shoot(shootVec.x, shootVec.y, shootVec.z, 7.0F, 0.25F);
            player.level().addFreshEntity(heliRocketEntity);
            if (!player.level().isClientSide) {
                this.playShootSound3p(player, 0, 6, 6, 6);
            }

            this.entityData.set(LOADED_ROCKET, (Integer)this.getEntityData().get(LOADED_ROCKET) - 1);
            this.reloadCoolDown = 30;
        }

    }

    public int mainGunRpm(Player player) {
        return 500;
    }

    public boolean canShoot(Player player) {
        if (this.getWeaponIndex(0) != 0) {
            if (this.getWeaponIndex(0) == 1) {
                return (Integer)this.entityData.get(AMMO) > 0;
            } else {
                return false;
            }
        } else {
            return ((Integer)this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !this.cannotFire;
        }
    }

    public int getAmmoCount(Player player) {
        return (Integer)this.entityData.get(AMMO);
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

    public int getMaxPassengers() {
        return 4;
    }

    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/ah_6_icon.png");
    }

    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return seatIndex == 0 ? (double)0.0F : original;
    }

    public double getMouseSensitivity() {
        return 0.15;
    }

    public double getMouseSpeedX() {
        return 0.35;
    }

    public double getMouseSpeedY() {
        return 0.2;
    }

    @OnlyIn(Dist.CLIENT)
    public @Nullable Pair<Quaternionf, Quaternionf> getPassengerRotation(Entity entity, float tickDelta) {
        if (this.getSeatIndex(entity) == 2) {
            return Pair.of(Axis.XP.rotationDegrees(-this.getRoll(tickDelta)), Axis.ZP.rotationDegrees(this.getViewXRot(tickDelta)));
        } else {
            return this.getSeatIndex(entity) == 3 ? Pair.of(Axis.XP.rotationDegrees(this.getRoll(tickDelta)), Axis.ZP.rotationDegrees(-this.getViewXRot(tickDelta))) : Pair.of(Axis.XP.rotationDegrees(-this.getViewXRot(tickDelta)), Axis.ZP.rotationDegrees(-this.getRoll(tickDelta)));
        }
    }

    public Matrix4f getClientVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float)Mth.lerp((double)ticks, this.xo, this.getX()), (float)Mth.lerp((double)ticks, this.yo + (double)1.45F, this.getY() + (double)1.45F), (float)Mth.lerp((double)ticks, this.zo, this.getZ()));
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
            Vector4f maxCameraPosition = this.transformPosition(transform, -2.1F, 1.0F, -10.0F - (float)ClientMouseHandler.custom3pDistanceLerp);
            Vec3 finalPos = CameraTool.getMaxZoom(transform, maxCameraPosition);
            return isFirstPerson ? new Vec3(Mth.lerp((double)partialTicks, player.xo, player.getX()), Mth.lerp((double)partialTicks, player.yo + (double)player.getEyeHeight(), player.getEyeY()), Mth.lerp((double)partialTicks, player.zo, player.getZ())) : finalPos;
        } else {
            return super.getCameraPosition(partialTicks, player, false, false);
        }
    }

    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/aircraft.png");
    }

    static {
        PROPELLER_ROT = SynchedEntityData.defineId(MH60MEntity.class, EntityDataSerializers.FLOAT);
        LOADED_ROCKET = SynchedEntityData.defineId(MH60MEntity.class, EntityDataSerializers.INT);
    }
}