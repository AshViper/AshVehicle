package tech.lq0.ashvehicle.entity;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity;
import com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity;
import com.atsuishio.superbwarfare.entity.vehicle.Lav150Entity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.ProjectileWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.TooltipFlag;
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
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.lq0.ashvehicle.entity.Class.BaseTankEntity;
import tech.lq0.ashvehicle.init.ModEntities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class GepardEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity{
    private final AnimatableInstanceCache cache;
    private boolean fireLeftBarrel = true;
    public GepardEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
    }
    public GepardEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.GEPARD_1A2.get(), level);
    }
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{
            (new SmallCannonShellWeapon()).damage((float)(Integer)VehicleConfig.LAV_150_CANNON_DAMAGE.get())
                    .explosionDamage((float)(Integer)VehicleConfig.LAV_150_CANNON_EXPLOSION_DAMAGE.get())
                    .explosionRadius(((Double)VehicleConfig.LAV_150_CANNON_EXPLOSION_RADIUS.get()).floatValue())
                    .sound((SoundEvent)ModSounds.INTO_MISSILE.get())
                    .icon(Mod.loc("textures/screens/vehicle_weapon/cannon_20mm.png"))
                    .sound1p((SoundEvent)ModSounds.LAV_CANNON_FIRE_1P.get())
                    .sound3p((SoundEvent)ModSounds.LAV_CANNON_FIRE_3P.get())
                    .sound3pFar((SoundEvent)ModSounds.LAV_CANNON_FAR.get())
                    .sound3pVeryFar((SoundEvent)ModSounds.LAV_CANNON_VERYFAR.get())
                , (new ProjectileWeapon()).damage((Double)VehicleConfig.LAV_150_MACHINE_GUN_DAMAGE.get())
                .headShot(2.0F).zoom(false).sound((SoundEvent)ModSounds.INTO_CANNON.get())
                .icon(Mod.loc("textures/screens/vehicle_weapon/gun_7_62mm.png"))
                .sound1p((SoundEvent)ModSounds.COAX_FIRE_1P.get())
                .sound3p((SoundEvent)ModSounds.RPK_FIRE_3P.get())
                .sound3pFar((SoundEvent)ModSounds.RPK_FAR.get())
                .sound3pVeryFar((SoundEvent)ModSounds.RPK_VERYFAR.get())}};
    }

    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition((double)2.75F + ClientMouseHandler.custom3pDistanceLerp, (double)1.0F, (double)0.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @ParametersAreNonnullByDefault
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound((SoundEvent)ModSounds.WHEEL_STEP.get(), (float)(this.getDeltaMovement().length() * 0.3), this.random.nextFloat() * 0.15F + 1.05F);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> this.getSourceAngle(source, 0.4F) * damage);
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
            float f1 = (float)((double)0.7F - (double)0.04F * Math.min(this.getSubmergedHeight(this), (double)this.getBbHeight()) + (double)(0.08F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F));
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
                this.entityData.set(POWER, Math.min((Float)this.entityData.get(POWER) + ((Float)this.entityData.get(POWER) < 0.0F ? 0.004F : 0.0024F), 0.21F));
            }

            if (this.backInputDown) {
                this.entityData.set(POWER, Math.max((Float)this.entityData.get(POWER) - ((Float)this.entityData.get(POWER) > 0.0F ? 0.004F : 0.0024F), -0.16F));
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
            this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) * (float)Math.max((double)0.76F - (double)0.1F * this.getDeltaMovement().horizontalDistance(), 0.3));
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

    public boolean canCollideHardBlock() {
        return this.getDeltaMovement().horizontalDistance() > 0.09 || (double)Mth.abs((Float)this.entityData.get(POWER)) > 0.15;
    }

    private void handleAmmo() {
        if (this.getFirstPassenger() instanceof Player) {
            Stream var10000 = this.getItemStacks().stream().filter((stack) -> {
                if (stack.is((Item)ModItems.AMMO_BOX.get())) {
                    return Ammo.RIFLE.get(stack) > 0;
                } else {
                    return false;
                }
            });
            Ammo var10001 = Ammo.RIFLE;
            Objects.requireNonNull(var10001);
            int ammoCount = var10000.mapToInt(stack -> var10001.get((ItemStack) stack)).sum()
                    + this.countItem((Item)ModItems.RIFLE_AMMO.get());

            if (this.getWeaponIndex(0) == 0) {
                this.entityData.set(AMMO, this.countItem((Item)ModItems.SMALL_SHELL.get()));
            } else if (this.getWeaponIndex(0) == 1) {
                this.entityData.set(AMMO, ammoCount);
            }

        }
    }

    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = this.getBarrelTransform(pPartialTicks);
        Vector4f rootPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f targetPosition = this.transformPosition(transform, 0.0F, 0.0F, 2.0F);
        return (new Vec3((double)rootPosition.x, (double)rootPosition.y, (double)rootPosition.z)).vectorTo(new Vec3((double)targetPosition.x, (double)targetPosition.y, (double)targetPosition.z));
    }

    public void vehicleShoot(Player player, int type) {
        boolean hasCreativeAmmo = false;

        for(int i = 0; i < this.getMaxPassengers() - 1; ++i) {
            Entity y = this.getNthEntity(i);
            if (y instanceof Player pPlayer) {
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
                float x = fireLeftBarrel ? -1.06F :1.06F; // 左右切り替え
                float y = 1.0F;
                float z = 4.5F;
                fireLeftBarrel = !fireLeftBarrel; // 次回は逆側

                Vector4f worldPosition = this.transformPosition(transform, x, y, z);
                SmallCannonShellEntity smallCannonShell = ((SmallCannonShellWeapon)this.getWeapon(0)).create(player);
            smallCannonShell.setPos(
                    (double)worldPosition.x - 1.1 * this.getDeltaMovement().x,
                    (double)worldPosition.y,
                    (double)worldPosition.z - 1.1 * this.getDeltaMovement().z
            );
            Vec3 barrelVec = this.getBarrelVector(1.0F);
            smallCannonShell.shoot(
                    barrelVec.x,
                    barrelVec.y,
                    barrelVec.z,
                    35.0F,
                    0.2F  // 反動やブレを完全になくしたいなら0.0Fに
            );
                this.level().addFreshEntity(smallCannonShell);

                ParticleTool.sendParticle(
                        (ServerLevel)this.level(),
                        ParticleTypes.LARGE_SMOKE,
                        (double)worldPosition.x - 1.1 * this.getDeltaMovement().x,
                        (double)worldPosition.y,
                        (double)worldPosition.z - 1.1 * this.getDeltaMovement().z,
                        1, 0.02, 0.02, 0.02, 0.0F, false
                );

            if (!player.level().isClientSide) {
                this.playShootSound3p(player, 0, 4, 12, 24);
            }

            Level level = player.level();
            Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

            for (Entity target : level.getEntitiesOfClass(Entity.class, (new AABB(center, center)).inflate(4.0F)).stream()
                    .sorted(Comparator.comparingDouble((e) -> e.distanceToSqr(center))).toList()) {
                if (target instanceof ServerPlayer serverPlayer) {
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new ShakeClientMessage(6.0, 5.0, 9.0, this.getX(), this.getEyeY(), this.getZ()));
                }
            }

            this.entityData.set(CANNON_RECOIL_TIME, 40);
            this.entityData.set(YAW, this.getTurretYRot());
            this.entityData.set(HEAT, this.entityData.get(HEAT) + 2);
            this.entityData.set(FIRE_ANIM, 3);

            if (hasCreativeAmmo) return;
            this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.SMALL_SHELL.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
        } else if (this.getWeaponIndex(0) == 1) {
            if (this.cannotFireCoax) {
                return;
            }

            float x = 0.3F;
            float y = 0.08F;
            float z = 0.7F;
            Vector4f worldPosition = this.transformPosition(transform, x, y, z);
            if ((Integer)this.entityData.get(AMMO) > 0 || hasCreativeAmmo) {
                ProjectileEntity projectile = ((ProjectileWeapon)this.getWeapon(0)).create(player).setGunItemId(this.getType().getDescriptionId());
                projectile.bypassArmorRate(0.2F);
                projectile.setPos((double)worldPosition.x - 1.1 * this.getDeltaMovement().x, (double)worldPosition.y, (double)worldPosition.z - 1.1 * this.getDeltaMovement().z);
                projectile.shoot(player, this.getBarrelVector(1.0F).x, this.getBarrelVector(1.0F).y + (double)0.002F, this.getBarrelVector(1.0F).z, 36.0F, 0.25F);
                this.level().addFreshEntity(projectile);
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

            this.entityData.set(COAX_HEAT, (Integer)this.entityData.get(COAX_HEAT) + 1);
            this.entityData.set(FIRE_ANIM, 2);
            if (!player.level().isClientSide) {
                this.playShootSound3p(player, 0, 3, 6, 12);
            }
        }

    }

    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.LAV_ENGINE.get();
    }

    public float getEngineSoundVolume() {
        return Mth.abs((Float)this.entityData.get(POWER)) * 2.0F;
    }

    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getTurretTransform(1.0F);
            Matrix4f transformV = this.getVehicleTransform(1.0F);
            int i = this.getSeatIndex(passenger);
            Vector4f worldPosition;
            if (i == 0) {
                worldPosition = this.transformPosition(transform, 0F, 0.4F, -1.5F);
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
        return 3;
    }

    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = this.getTurretTransform(ticks);
        Vector4f worldPosition = this.transformPosition(transform, 0.3F, 0.75F, 0.56F);
        return new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
    }

    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = this.getTurretTransform(ticks);
        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = this.transformPosition(transform, 0.0234375F, 0.33795F, 0.825F);
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
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 2.4003F, 1.0F);
        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, this.turretYRotO, this.getTurretYRot())));
        return transformV;
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

        float min = -85.5F - r * this.getXRot() - r2 * this.getRoll();
        float max = 25.0F - r * this.getXRot() - r2 * this.getRoll();
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
            return 800;
        } else {
            return this.getWeaponIndex(0) == 1 ? 600 : 300;
        }
    }

    public boolean canShoot(Player player) {
        if (this.getWeaponIndex(0) == 0) {
            return ((Integer)this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !this.cannotFire;
        } else if (this.getWeaponIndex(0) != 1) {
            return false;
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
        return Mod.loc("textures/vehicle_icon/lav150_icon.png");
    }

    @OnlyIn(Dist.CLIENT)
    public void renderFirstPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        super.renderFirstPersonOverlay(guiGraphics, font, player, screenWidth, screenHeight, scale);
        if (this.getWeaponIndex(0) == 0) {
            double heat = (double)(1.0F - (float)(Integer)this.getEntityData().get(HEAT) / 100.0F);
            Object var10002 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal("20MM CANNON " + var10002), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float)heat / 3.7453184F, 1.0F, 1.0F), false);
        } else {
            double heat = (double)(1.0F - (float)(Integer)this.getEntityData().get(COAX_HEAT) / 100.0F);
            Object var10 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal("7.62MM COAX " + var10), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float)heat / 3.7453184F, 1.0F, 1.0F), false);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        super.renderThirdPersonOverlay(guiGraphics, font, player, screenWidth, screenHeight, scale);
        if (this.getWeaponIndex(0) == 0) {
            double heat = (double)((float)(Integer)this.getEntityData().get(HEAT) / 100.0F);
            Object var10002 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal("20MM CANNON " + var10002), 30, -9, Mth.hsvToRgb(0.0F, (float)heat, 1.0F), false);
        } else {
            double heat2 = (double)((float)(Integer)this.getEntityData().get(COAX_HEAT) / 100.0F);
            Object var10 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal("7.62MM COAX " + var10), 30, -9, Mth.hsvToRgb(0.0F, (float)heat2, 1.0F), false);
        }

    }

    public boolean hasDecoy() {
        return true;
    }

    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return zoom ? 0.23 : 0.3;
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
}
