package Aru.Aru.ashvehicle.entity;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity;
import com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity;
import com.atsuishio.superbwarfare.entity.projectile.WgMissileEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.ProjectileWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.WgMissileWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.Ammo;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import Aru.Aru.ashvehicle.init.ModEntities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

public class M3A3BradleyEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity {
    public static final EntityDataAccessor<Integer> CANNON_FIRE_TIME;
    public static final EntityDataAccessor<Integer> LOADED_MISSILE;
    public static final EntityDataAccessor<Integer> MISSILE_COUNT;
    private final AnimatableInstanceCache cache;
    public int reloadCoolDown;

    public M3A3BradleyEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
    }
    public M3A3BradleyEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.M3A3_BRADLEY.get(), level);
    }
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{(new SmallCannonShellWeapon()).damage((float)(Integer) VehicleConfig.BMP_2_CANNON_DAMAGE.get()).explosionDamage((float)(Integer)VehicleConfig.BMP_2_CANNON_EXPLOSION_DAMAGE.get()).explosionRadius(((Double)VehicleConfig.BMP_2_CANNON_EXPLOSION_RADIUS.get()).floatValue()).sound((SoundEvent) ModSounds.INTO_MISSILE.get()).icon(Mod.loc("textures/screens/vehicle_weapon/cannon_30mm.png")).sound1p((SoundEvent)ModSounds.BMP_CANNON_FIRE_1P.get()).sound3p((SoundEvent)ModSounds.BMP_CANNON_FIRE_3P.get()).sound3pFar((SoundEvent)ModSounds.LAV_CANNON_FAR.get()).sound3pVeryFar((SoundEvent)ModSounds.LAV_CANNON_VERYFAR.get()), (new ProjectileWeapon()).damage(9.5F).headShot(2.0F).zoom(false).sound((SoundEvent)ModSounds.INTO_CANNON.get()).icon(Mod.loc("textures/screens/vehicle_weapon/gun_7_62mm.png")).sound1p((SoundEvent)ModSounds.COAX_FIRE_1P.get()).sound3p((SoundEvent)ModSounds.M_60_FIRE_3P.get()).sound3pFar((SoundEvent)ModSounds.M_60_FAR.get()).sound3pVeryFar((SoundEvent)ModSounds.M_60_VERYFAR.get()), (new WgMissileWeapon()).damage((float)(Integer) ExplosionConfig.WIRE_GUIDE_MISSILE_DAMAGE.get()).explosionDamage((float)(Integer)ExplosionConfig.WIRE_GUIDE_MISSILE_EXPLOSION_DAMAGE.get()).explosionRadius((float)(Integer)ExplosionConfig.WIRE_GUIDE_MISSILE_EXPLOSION_RADIUS.get()).sound((SoundEvent)ModSounds.INTO_MISSILE.get()).sound1p((SoundEvent)ModSounds.BMP_MISSILE_FIRE_1P.get()).sound3p((SoundEvent)ModSounds.BMP_MISSILE_FIRE_3P.get())}};
    }

    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition((double)3.0F + ClientMouseHandler.custom3pDistanceLerp, (double)1.0F, (double)0.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CANNON_FIRE_TIME, 0);
        this.entityData.define(LOADED_MISSILE, 0);
        this.entityData.define(MISSILE_COUNT, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedMissile", (Integer)this.entityData.get(LOADED_MISSILE));
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> this.getSourceAngle(source, 0.4F) * damage);
    }

    @ParametersAreNonnullByDefault
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound((SoundEvent)ModSounds.WHEEL_STEP.get(), (float)(this.getDeltaMovement().length() * 0.15), this.random.nextFloat() * 0.15F + 1.05F);
    }

    public double getSubmergedHeight(Entity entity) {
        return super.getSubmergedHeight(entity);
    }

    public void baseTick() {
        super.baseTick();
        if (this.getLeftTrack() < 0.0F) {
            this.setLeftTrack(100.0F);
        }

        if (this.getLeftTrack() > 100.0F) {
            this.setLeftTrack(0.0F);
        }

        if (this.getRightTrack() < 0.0F) {
            this.setRightTrack(100.0F);
        }

        if (this.getRightTrack() > 100.0F) {
            this.setRightTrack(0.0F);
        }

        if (this.level() instanceof ServerLevel) {
            if (this.reloadCoolDown > 0) {
                --this.reloadCoolDown;
            }

            this.handleAmmo();
        }

        double fluidFloat = 0.052 * this.getSubmergedHeight(this);
        this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, fluidFloat, (double)0.0F));
        if (this.onGround()) {
            float f0 = 0.54F + 0.25F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).normalize().scale(0.05 * this.getDeltaMovement().dot(this.getViewVector(1.0F)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply((double)f0, 0.99, (double)f0));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.99, 0.99));
        }

        if (this.isInWater()) {
            float f1 = (float)((double)0.7F - (double)0.04F * org.joml.Math.min(this.getSubmergedHeight(this), (double)this.getBbHeight()) + (double)(0.08F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F));
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).normalize().scale(0.04 * this.getDeltaMovement().dot(this.getViewVector(1.0F)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply((double)f1, 0.85, (double)f1));
        }

        Level var4 = this.level();
        if (var4 instanceof ServerLevel serverLevel) {
            if (this.isInWater() && this.getDeltaMovement().length() > 0.1) {
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, this.getX() + (double)0.5F * this.getDeltaMovement().x, this.getY() + this.getSubmergedHeight(this) - 0.2, this.getZ() + (double)0.5F * this.getDeltaMovement().z, (int)((double)2.0F + (double)4.0F * this.getDeltaMovement().length()), 0.65, (double)0.0F, 0.65, (double)0.0F, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + (double)0.5F * this.getDeltaMovement().x, this.getY() + this.getSubmergedHeight(this) - 0.2, this.getZ() + (double)0.5F * this.getDeltaMovement().z, (int)((double)2.0F + (double)10.0F * this.getDeltaMovement().length()), 0.65, (double)0.0F, 0.65, (double)0.0F, true);
            }
        }

        this.turretAngle(25.0F, 25.0F);
        this.terrainCompact(4.0F, 5.0F);
        this.inertiaRotate(1.0F);
        this.releaseSmokeDecoy(this.getTurretVector(1.0F));
        this.lowHealthWarning();
        this.refreshDimensions();
    }

    public void terrainCompact(float w, float l) {
        if (this.onGround()) {
            float x1 = this.terrainCompactTrackValue(w, l)[0];
            float x2 = this.terrainCompactTrackValue(w, l - 1.0F)[0];
            float x3 = this.terrainCompactTrackValue(w, l - 2.0F)[0];
            float x4 = this.terrainCompactTrackValue(w, l - 3.0F)[0];
            float x5 = this.terrainCompactTrackValue(w, l - 4.0F)[0];
            float x6 = this.terrainCompactTrackValue(w, l - 5.0F)[0];
            List<Float> numbersX = Arrays.asList(x1, x2, x3, x4, x5, x6);
            float maxX = (Float) Collections.max(numbersX);
            float minX = (Float)Collections.min(numbersX);
            float z1 = this.terrainCompactTrackValue(w, l)[1];
            float z2 = this.terrainCompactTrackValue(w, l - 1.0F)[1];
            float z3 = this.terrainCompactTrackValue(w, l - 2.0F)[1];
            float z4 = this.terrainCompactTrackValue(w, l - 3.0F)[1];
            float z5 = this.terrainCompactTrackValue(w, l - 4.0F)[1];
            float z6 = this.terrainCompactTrackValue(w, l - 5.0F)[1];
            List<Float> numbersZ = Arrays.asList(z1, z2, z3, z4, z5, z6);
            float maxZ = (Float)Collections.max(numbersZ);
            float minZ = (Float)Collections.min(numbersZ);
            float diffX = org.joml.Math.clamp(-15.0F, 15.0F, (minX + maxX) / 2.0F);
            this.setXRot(Mth.clamp(this.getXRot() + 0.15F * diffX, -45.0F, 45.0F));
            float diffZ = org.joml.Math.clamp(-15.0F, 15.0F, minZ + maxZ);
            this.setZRot(Mth.clamp(this.getRoll() + 0.15F * diffZ, -45.0F, 45.0F));
        } else if (this.isInWater()) {
            this.setXRot(this.getXRot() * 0.9F);
            this.setZRot(this.getRoll() * 0.9F);
        }

    }

    public boolean canCollideHardBlock() {
        return this.getDeltaMovement().horizontalDistance() > 0.07 || (double)Mth.abs((Float)this.entityData.get(POWER)) > 0.12;
    }

    private void handleAmmo() {
        Entity ammoCount = this.getFirstPassenger();
        if (ammoCount instanceof Player player) {
            Stream var10000 = this.getItemStacks().stream().filter((stack) -> {
                if (stack.is((Item) ModItems.AMMO_BOX.get())) {
                    return Ammo.RIFLE.get(stack) > 0;
                } else {
                    return false;
                }
            });
            Ammo var10001 = Ammo.RIFLE;
            Objects.requireNonNull(var10001);
            int var3 = var10000.mapToInt(stack -> var10001.get((ItemStack) stack)).sum()
                    + this.countItem((Item)ModItems.RIFLE_AMMO.get());
            if ((this.hasItem((Item)ModItems.WIRE_GUIDE_MISSILE.get()) || InventoryTool.hasCreativeAmmoBox(player)) && this.reloadCoolDown <= 0 && (Integer)this.getEntityData().get(LOADED_MISSILE) < 2) {
                this.entityData.set(LOADED_MISSILE, (Integer)this.getEntityData().get(LOADED_MISSILE) + 1);
                this.reloadCoolDown = 160;
                if (!InventoryTool.hasCreativeAmmoBox(player)) {
                    this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.WIRE_GUIDE_MISSILE.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
                }

                this.level().playSound((Player)null, this, (SoundEvent)ModSounds.BMP_MISSILE_RELOAD.get(), this.getSoundSource(), 1.0F, 1.0F);
            }

            if (this.getWeaponIndex(0) == 0) {
                this.entityData.set(AMMO, this.countItem((Item)ModItems.SMALL_SHELL.get()));
            } else if (this.getWeaponIndex(0) == 1) {
                this.entityData.set(AMMO, var3);
            } else {
                this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_MISSILE));
            }

            this.entityData.set(MISSILE_COUNT, this.countItem((Item)ModItems.WIRE_GUIDE_MISSILE.get()));
        }
    }

    public void vehicleShoot(Player player, int type) {
        boolean hasCreativeAmmo = false;

        for(int i = 0; i < this.getMaxPassengers() - 1; ++i) {
            Entity worldPosition = this.getNthEntity(i);
            if (worldPosition instanceof Player pPlayer) {
                if (InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                    hasCreativeAmmo = true;
                }
            }
        }

        Matrix4f transform = this.getBarrelTransform(1.0F);
        if (this.getWeaponIndex(0) == 0) {
            if (this.cannotFire) {
                return;
            }

            float x = -0.45F;
            float y = 0.4F;
            float z = 4.2F;
            Vector4f worldPosition = this.transformPosition(transform, x, y, z);
            SmallCannonShellEntity smallCannonShell = ((SmallCannonShellWeapon)this.getWeapon(0)).create(player);
            smallCannonShell.setPos((double)worldPosition.x - 1.1 * this.getDeltaMovement().x, (double)worldPosition.y, (double)worldPosition.z - 1.1 * this.getDeltaMovement().z);
            smallCannonShell.shoot(this.getBarrelVector(1.0F).x, this.getBarrelVector(1.0F).y + (double)0.005F, this.getBarrelVector(1.0F).z, 35.0F, 0.25F);
            this.level().addFreshEntity(smallCannonShell);
            ParticleTool.sendParticle((ServerLevel)this.level(), ParticleTypes.LARGE_SMOKE, (double)worldPosition.x - 1.1 * this.getDeltaMovement().x, (double)worldPosition.y, (double)worldPosition.z - 1.1 * this.getDeltaMovement().z, 1, 0.02, 0.02, 0.02, (double)0.0F, false);
            if (!player.level().isClientSide) {
                this.playShootSound3p(player, 0, 4, 12, 24);
            }

            Level level = player.level();
            Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

            for(Entity target : level.getEntitiesOfClass(Entity.class, (new AABB(center, center)).inflate((double)4.0F), (e) -> true).stream().sorted(Comparator.comparingDouble((e) -> e.distanceToSqr(center))).toList()) {
                if (target instanceof ServerPlayer) {
                    ServerPlayer serverPlayer = (ServerPlayer)target;
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ShakeClientMessage((double)6.0F, (double)5.0F, (double)9.0F, this.getX(), this.getEyeY(), this.getZ()));
                }
            }

            this.entityData.set(CANNON_RECOIL_TIME, 40);
            this.entityData.set(YAW, this.getTurretYRot());
            this.entityData.set(HEAT, (Integer)this.entityData.get(HEAT) + 5);
            this.entityData.set(FIRE_ANIM, 3);
            if (hasCreativeAmmo) {
                return;
            }

            this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.SMALL_SHELL.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
        } else if (this.getWeaponIndex(0) == 1) {
            if (this.cannotFireCoax) {
                return;
            }

            float x = -0.2F;
            float y = 0.3F;
            float z = 1.2F;
            Vector4f worldPosition = this.transformPosition(transform, x, y, z);
            if ((Integer)this.entityData.get(AMMO) > 0 || hasCreativeAmmo) {
                ProjectileEntity projectileRight = ((ProjectileWeapon)this.getWeapon(0)).create(player).setGunItemId(this.getType().getDescriptionId());
                projectileRight.bypassArmorRate(0.2F);
                projectileRight.setPos((double)worldPosition.x - 1.1 * this.getDeltaMovement().x, (double)worldPosition.y, (double)worldPosition.z - 1.1 * this.getDeltaMovement().z);
                projectileRight.shoot(player, this.getBarrelVector(1.0F).x, this.getBarrelVector(1.0F).y + (double)0.002F, this.getBarrelVector(1.0F).z, 36.0F, 0.25F);
                this.level().addFreshEntity(projectileRight);
                if (!hasCreativeAmmo) {
                    ItemStack ammoBox = (ItemStack)this.getItemStacks().stream().filter((stack) -> {
                        if (stack.is((Item)ModItems.AMMO_BOX.get())) {
                            return Ammo.RIFLE.get(stack) > 0;
                        } else {
                            return false;
                        }
                    }).findFirst().orElse(ItemStack.EMPTY);
                    if (!ammoBox.isEmpty()) {
                        Ammo.RIFLE.add(ammoBox, -1);
                    } else {
                        this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.RIFLE_AMMO.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
                    }
                }
            }

            this.entityData.set(COAX_HEAT, (Integer)this.entityData.get(COAX_HEAT) + 3);
            this.entityData.set(FIRE_ANIM, 2);
            if (!player.level().isClientSide) {
                this.playShootSound3p(player, 0, 3, 6, 12);
            }
        } else if (this.getWeaponIndex(0) == 2 && (Integer)this.getEntityData().get(LOADED_MISSILE) > 0) {
            Matrix4f transformT = this.getBarrelTransform(1.0F);
            Vector4f worldPosition = this.transformPosition(transformT, 2.0F, 1.0F, 0.0F);
            WgMissileEntity wgMissileEntity = ((WgMissileWeapon)this.getWeapon(0)).create(player);
            wgMissileEntity.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            wgMissileEntity.shoot(this.getBarrelVector(1.0F).x, this.getBarrelVector(1.0F).y, this.getBarrelVector(1.0F).z, 2.0F, 0.0F);
            player.level().addFreshEntity(wgMissileEntity);
            if (!player.level().isClientSide) {
                this.playShootSound3p(player, 0, 6, 0, 0);
            }

            this.entityData.set(LOADED_MISSILE, (Integer)this.getEntityData().get(LOADED_MISSILE) - 1);
            this.reloadCoolDown = 160;
        }

    }

    public void travel() {
        Entity passenger0 = this.getFirstPassenger();
        if (this.getEnergy() > 0) {
            if (passenger0 == null) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.entityData.set(POWER, 0.0F);
            }

            if (this.forwardInputDown) {
                this.entityData.set(POWER, org.joml.Math.min((Float)this.entityData.get(POWER) + ((Float)this.entityData.get(POWER) < 0.0F ? 0.004F : 0.0024F), 0.21F));
            }

            if (this.backInputDown) {
                this.entityData.set(POWER, org.joml.Math.max((Float)this.entityData.get(POWER) - ((Float)this.entityData.get(POWER) > 0.0F ? 0.004F : 0.0024F), -0.16F));
                if (this.rightInputDown) {
                    this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) + 0.1F);
                } else if (this.leftInputDown) {
                    this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) - 0.1F);
                }
            } else if (this.rightInputDown) {
                this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) - 0.1F);
            } else if (this.leftInputDown) {
                this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) + 0.1F);
            }

            if (this.forwardInputDown || this.backInputDown) {
                this.consumeEnergy((Integer)VehicleConfig.BMP_2_ENERGY_COST.get());
            }

            this.entityData.set(POWER, (Float)this.entityData.get(POWER) * (this.upInputDown ? 0.5F : (!this.rightInputDown && !this.leftInputDown ? 0.96F : 0.947F)));
            this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) * (float) org.joml.Math.max((double)0.76F - (double)0.1F * this.getDeltaMovement().horizontalDistance(), 0.3));
            double s0 = this.getDeltaMovement().dot(this.getViewVector(1.0F));
            this.setLeftWheelRot((float)((double)this.getLeftWheelRot() - (double)1.25F * s0 + (double)Mth.clamp(0.75F * (Float)this.entityData.get(DELTA_ROT), -5.0F, 5.0F)));
            this.setRightWheelRot((float)((double)this.getRightWheelRot() - (double)1.25F * s0 - (double)Mth.clamp(0.75F * (Float)this.entityData.get(DELTA_ROT), -5.0F, 5.0F)));
            this.setLeftTrack((float)((double)this.getLeftTrack() - 5.969026041820607 * s0 + Mth.clamp(1.2566370801612687 * (double)(Float)this.entityData.get(DELTA_ROT), (double)-5.0F, (double)5.0F)));
            this.setRightTrack((float)((double)this.getRightTrack() - 5.969026041820607 * s0 - Mth.clamp(1.2566370801612687 * (double)(Float)this.entityData.get(DELTA_ROT), (double)-5.0F, (double)5.0F)));
            this.setYRot((float)((double)this.getYRot() - (this.isInWater() && !this.onGround() ? (double)2.5F : (double)6.0F) * (double)(Float)this.entityData.get(DELTA_ROT)));
            if (this.isInWater() || this.onGround()) {
                float power = (Float)this.entityData.get(POWER) * Mth.clamp(1.0F + (float)(s0 > (double)0.0F ? 1 : -1) * this.getXRot() / 35.0F, 0.0F, 2.0F);
                this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).scale((double)((!this.isInWater() && !this.onGround() ? 0.13F : (this.isInWater() && !this.onGround() ? 2.0F : 2.4F)) * power))));
            }

        }
    }

    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.BMP_ENGINE.get();
    }

    public float getEngineSoundVolume() {
        return Math.max(Mth.abs((Float)this.entityData.get(POWER)), Mth.abs(0.1F * (Float)this.entityData.get(DELTA_ROT))) * 2.5F;
    }

    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getTurretTransform(1.0F);
            Matrix4f transformV = this.getVehicleTransform(1.0F);
            int i = this.getSeatIndex(passenger);
            Vector4f worldPosition;
            if (i == 0) {
                worldPosition = this.transformPosition(transform, 0.36F, 1.0F, 0.66F);
            } else {
                worldPosition = this.transformPosition(transformV, 0.0F, 1.0F, 0.0F);
            }

            passenger.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            callback.accept(passenger, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            this.copyEntityData(passenger);
        }
    }

    public void copyEntityData(Entity entity) {
        if (entity == this.getNthEntity(0)) {
            entity.setYBodyRot(this.getBarrelYRot(1.0F));
        }

    }

    public int getMaxPassengers() {
        return 7;
    }

    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = this.getTurretTransform(ticks);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.75F);
        return new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
    }

    public Vec3 getNewEyePos(float pPartialTicks) {
        Matrix4f transform = this.getTurretTransform(pPartialTicks);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.65F, 0.75F);
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
        Vector4f worldPosition = this.transformPosition(transform, 0.3625F, 0.293125F, 1.18095F);
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

    public Vec3 getTurretVector(float pPartialTicks) {
        Matrix4f transform = this.getTurretTransform(pPartialTicks);
        Vector4f rootPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f targetPosition = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        return (new Vec3((double)rootPosition.x, (double)rootPosition.y, (double)rootPosition.z)).vectorTo(new Vec3((double)targetPosition.x, (double)targetPosition.y, (double)targetPosition.z));
    }

    public Matrix4f getTurretTransform(float ticks) {
        Matrix4f transformV = this.getVehicleTransform(ticks);
        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 2.25F, -0.703125F);
        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, this.turretYRotO, this.getTurretYRot())));
        return transformV;
    }

    public void destroy() {
        if (this.level() instanceof ServerLevel) {
            CustomExplosion explosion = (new CustomExplosion(this.level(), this, ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this.getAttacker(), this.getAttacker()), 80.0F, this.getX(), this.getY(), this.getZ(), 5.0F, (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
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

        float min = -74.0F - r * this.getXRot() - r2 * this.getRoll();
        float max = 7.5F - r * this.getXRot() - r2 * this.getRoll();
        float f = Mth.wrapDegrees(entity.getXRot());
        float f1 = Mth.clamp(f, min, max);
        entity.xRotO += f1 - f;
        entity.setXRot(entity.getXRot() + f1 - f);
        entity.setYBodyRot(this.getBarrelYRot(1.0F));
    }

    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public int mainGunRpm(Player player) {
        if (this.getWeaponIndex(0) == 0) {
            return 300;
        } else {
            return this.getWeaponIndex(0) == 1 ? 750 : 250;
        }
    }

    public boolean canShoot(Player player) {
        if (this.getWeaponIndex(0) == 0) {
            return ((Integer)this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !this.cannotFire;
        } else if (this.getWeaponIndex(0) != 1) {
            if (this.getWeaponIndex(0) == 2) {
                return (Integer)this.entityData.get(LOADED_MISSILE) > 0;
            } else {
                return false;
            }
        } else {
            return ((Integer)this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !this.cannotFireCoax;
        }
    }

    public int getAmmoCount(Player player) {
        return (Integer)this.entityData.get(AMMO);
    }

    public boolean banHand(Player player) {
        return true;
    }

    public boolean hidePassenger(Entity entity) {
        return true;
    }

    public int zoomFov() {
        return 3;
    }

    public int getWeaponHeat(Player player) {
        if (this.getWeaponIndex(0) == 0) {
            return (Integer)this.entityData.get(HEAT);
        } else {
            return this.getWeaponIndex(0) == 1 ? (Integer)this.entityData.get(COAX_HEAT) : 0;
        }
    }

    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/bmp2_icon.png");
    }

    @OnlyIn(Dist.CLIENT)
    public void renderFirstPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        super.renderFirstPersonOverlay(guiGraphics, font, player, screenWidth, screenHeight, scale);
        if (this.getWeaponIndex(0) == 0) {
            double heat = (double)(1.0F - (float)(Integer)this.getEntityData().get(HEAT) / 100.0F);
            Object var10002 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal(" 30MM 2A42 " + var10002), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float)heat / 3.7453184F, 1.0F, 1.0F), false);
        } else if (this.getWeaponIndex(0) == 1) {
            double heat = (double)(1.0F - (float)(Integer)this.getEntityData().get(COAX_HEAT) / 100.0F);
            Object var10 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal(" 7.62MM ПКТ " + var10), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float)heat / 3.7453184F, 1.0F, 1.0F), false);
        } else {
            Object var11 = this.getEntityData().get(LOADED_MISSILE);
            guiGraphics.drawString(font, Component.literal("    9M113  " + var11 + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : (Serializable)this.getEntityData().get(MISSILE_COUNT))), screenWidth / 2 - 33, screenHeight - 65, 6749952, false);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        if (this.getWeaponIndex(0) == 0) {
            double heat = (double)((float)(Integer)this.getEntityData().get(HEAT) / 100.0F);
            Object var10002 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal("30MM 2A42 " + var10002), 30, -9, Mth.hsvToRgb(0.0F, (float)heat, 1.0F), false);
        } else if (this.getWeaponIndex(0) == 1) {
            double heat2 = (double)((float)(Integer)this.getEntityData().get(COAX_HEAT) / 100.0F);
            Object var10 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal("7.62MM ПКТ " + var10), 30, -9, Mth.hsvToRgb(0.0F, (float)heat2, 1.0F), false);
        } else {
            Object var11 = this.getEntityData().get(LOADED_MISSILE);
            guiGraphics.drawString(font, Component.literal("9M113 " + var11 + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : (Serializable)this.getEntityData().get(MISSILE_COUNT))), 30, -9, -1, false);
        }

    }

    public boolean hasTracks() {
        return true;
    }

    public boolean hasDecoy() {
        return true;
    }

    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return zoom ? 0.22 : 0.27;
    }

    public boolean isEnclosed(int index) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (!zoom && !isFirstPerson) {
            return super.getCameraRotation(partialTicks, player, false, false);
        } else {
            return this.getSeatIndex(player) == 0 ? new Vec2((float)(-getYRotFromVector(this.getBarrelVec(partialTicks))), (float)(-getXRotFromVector(this.getBarrelVec(partialTicks)))) : new Vec2(Mth.lerp(partialTicks, player.yHeadRotO, player.getYHeadRot()), Mth.lerp(partialTicks, player.xRotO, player.getXRot()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (!zoom && !isFirstPerson) {
            return super.getCameraPosition(partialTicks, player, false, false);
        } else if (this.getSeatIndex(player) == 0) {
            return zoom ? new Vec3(this.driverZoomPos(partialTicks).x, Mth.lerp((double)partialTicks, player.yo + (double)player.getEyeHeight(), player.getEyeY()), this.driverZoomPos(partialTicks).z) : new Vec3(Mth.lerp((double)partialTicks, player.xo, player.getX()), Mth.lerp((double)partialTicks, player.yo + (double)player.getEyeHeight(), player.getEyeY()), Mth.lerp((double)partialTicks, player.zo, player.getZ()));
        } else {
            return new Vec3(Mth.lerp((double)partialTicks, player.xo, player.getX()) - (double)6.0F * player.getViewVector(partialTicks).x, Mth.lerp((double)partialTicks, player.yo + (double)player.getEyeHeight() + (double)1.0F, player.getEyeY() + (double)1.0F) - (double)6.0F * player.getViewVector(partialTicks).y, Mth.lerp((double)partialTicks, player.zo, player.getZ()) - (double)6.0F * player.getViewVector(partialTicks).z);
        }
    }

    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/land.png");
    }

    static {
        CANNON_FIRE_TIME = SynchedEntityData.defineId(M3A3BradleyEntity.class, EntityDataSerializers.INT);
        LOADED_MISSILE = SynchedEntityData.defineId(M3A3BradleyEntity.class, EntityDataSerializers.INT);
        MISSILE_COUNT = SynchedEntityData.defineId(M3A3BradleyEntity.class, EntityDataSerializers.INT);
    }
}
