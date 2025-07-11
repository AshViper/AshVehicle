package Aru.Aru.ashvehicle.entity;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import Aru.Aru.ashvehicle.entity.weapon.BallisticMissileEntity;
import Aru.Aru.ashvehicle.entity.weapon.BallisticMissileWeapon;
import Aru.Aru.ashvehicle.init.ModEntities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.stream.Stream;

public class SapsanEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity {
    private final AnimatableInstanceCache cache;
    private boolean wasSprintInputDown = false; // 前回の入力状態
    private boolean sprintToggled;
    private boolean shotToggled;
    private static final EntityDataAccessor<Float> POD_ROT = SynchedEntityData.defineId(SapsanEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> POD_TOGGLED = SynchedEntityData.defineId(SapsanEntity.class, EntityDataSerializers.BOOLEAN);

    public SapsanEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.SAPSAN_GRIM2.get(), world);
    }

    public SapsanEntity(EntityType<SapsanEntity> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(POD_ROT, 0.0F);
        this.entityData.define(POD_TOGGLED, false);
    }

    public void setPodRot(float value) {
        this.entityData.set(POD_ROT, value);
    }

    public float getPodRot() {
        return this.entityData.get(POD_ROT);
    }

    public void setPodToggled(boolean value) {
        this.entityData.set(POD_TOGGLED, value);
    }

    public boolean getPodToggled() {
        return this.entityData.get(POD_TOGGLED);
    }

    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{
            (new BallisticMissileWeapon()).sound((SoundEvent)ModSounds.INTO_MISSILE.get())
        }};
    }

    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition((double)2.75F + ClientMouseHandler.custom3pDistanceLerp, (double)1.0F, (double)0.0F);
    }

    @ParametersAreNonnullByDefault
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound((SoundEvent)ModSounds.WHEEL_STEP.get(), (float)(this.getDeltaMovement().length() * 0.3), this.random.nextFloat() * 0.15F + 1.05F);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> this.getSourceAngle(source, 0.25F) * damage);
    }

    public void baseTick() {
        this.turretYRotO = this.getTurretYRot();
        this.rudderRotO = this.getRudderRot();
        this.leftWheelRotO = this.getLeftWheelRot();
        this.rightWheelRotO = this.getRightWheelRot();
        if (this.level() instanceof ServerLevel) {
            this.handleAmmo();
        }

        float target = this.getPodToggled() ? 90.0F : 0.0F;
        float current = getPodRot();
        float speed = 2.5F;

        if (Math.abs(current - target) <= speed) {
            setPodRot(target);
        }

        // ここで方向ごとに制御する
        if (this.getPodToggled()&& current < 90.0F) {
            setPodRot(current + speed); // 上昇のみ
            //System.out.printf("Toggled: %s | 上昇中： %.1f\n", getPodToggled(), getPodRot());
        } else if (!this.getPodToggled() && current > 0.0F) {
            setPodRot(current - speed); // 下降のみ
            //System.out.printf("Toggled: %s | 降下中： %.1f\n", getPodToggled(), getPodRot());
        }

        double fluidFloat = 0.052 * this.getSubmergedHeight(this);
        this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, fluidFloat, (double)0.0F));
        if (this.onGround()) {
            float f0 = 0.54F + 0.25F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).normalize().scale(0.05 * this.getDeltaMovement().dot(this.getViewVector(1.0F)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply((double)f0, 0.99, (double)f0));
        } else if (this.isInWater()) {
            float f1 = 0.74F + 0.09F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).normalize().scale(0.04 * this.getDeltaMovement().dot(this.getViewVector(1.0F)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply((double)f1, 0.85, (double)f1));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.99, 0.99));
        }

        Level var4 = this.level();
        if (var4 instanceof ServerLevel serverLevel) {
            if (this.isInWater() && this.getDeltaMovement().length() > 0.1) {
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, this.getX() + (double)0.5F * this.getDeltaMovement().x, this.getY() + this.getSubmergedHeight(this) - 0.2, this.getZ() + (double)0.5F * this.getDeltaMovement().z, (int)((double)2.0F + (double)4.0F * this.getDeltaMovement().length()), 0.65, (double)0.0F, 0.65, (double)0.0F, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + (double)0.5F * this.getDeltaMovement().x, this.getY() + this.getSubmergedHeight(this) - 0.2, this.getZ() + (double)0.5F * this.getDeltaMovement().z, (int)((double)2.0F + (double)10.0F * this.getDeltaMovement().length()), 0.65, (double)0.0F, 0.65, (double)0.0F, true);
            }
        }
        this.lowHealthWarning();
        this.terrainCompact(2.7F, 3.61F);
        this.releaseSmokeDecoy(this.getTurretVector(1.0F));
        this.inertiaRotate(1.2F);
        this.refreshDimensions();
        super.baseTick();
    }

    public boolean canCollideHardBlock() {
        return this.getDeltaMovement().horizontalDistance() > 0.09 || (double)Mth.abs((Float)this.entityData.get(POWER)) > 0.15;
    }

    private void handleAmmo() {
        if (this.getFirstPassenger() instanceof Player) {
            Stream var10000 = this.getItemStacks().stream().filter((stack) -> {
                if (stack.is((Item) ModItems.AMMO_BOX.get())) {
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
            }
        }
    }

    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = this.getBarrelTransform(pPartialTicks);
        Vector4f rootPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f targetPosition = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        return (new Vec3((double)rootPosition.x, (double)rootPosition.y, (double)rootPosition.z)).vectorTo(new Vec3((double)targetPosition.x, (double)targetPosition.y, (double)targetPosition.z));
    }

    public void vehicleShoot(Player player, int type) {

    }

    public void shootMissileTo(Player player, Vec3 targetPos) {
        if (!getPodToggled()) return;
        if (this.cannotFire) return;

        boolean hasCreativeAmmo = false;
        for (int i = 0; i < this.getMaxPassengers() - 1; ++i) {
            Entity y = this.getNthEntity(i);
            if (y instanceof Player pPlayer) {
                if (InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                    hasCreativeAmmo = true;
                }
            }
        }

        Matrix4f transform = this.getBarrelTransform(1.0F);

        float x = shotToggled ? -1F : 1F; // 左右切り替え
        float y = 1.0F;
        float z = -10F;
        this.shotToggled = !this.shotToggled;

        Vector4f worldPosition = this.transformPosition(transform, x, y, z);

        // 誘導弾道ミサイル生成
        BallisticMissileEntity missile = ((BallisticMissileWeapon) this.getWeapon(0)).create(player);
        missile.setPos(worldPosition.x, worldPosition.y, worldPosition.z);

        // 目標座標セット（誘導開始）
        missile.setXRot(90);
        missile.setYRot(180);
        missile.setTargetPosition(targetPos);

        this.level().addFreshEntity(missile);

        // 発射エフェクト
        ParticleTool.sendParticle((ServerLevel) this.level(), ParticleTypes.LARGE_SMOKE,
                worldPosition.x, worldPosition.y, worldPosition.z,
                10, 0.1, 0.1, 0.1, 0.0, false);

        // サウンド・アニメーション
        if (!player.level().isClientSide) {
            this.playShootSound3p(player, 1, 10, 30, 60);
        }
        ShakeClientMessage.sendToNearbyPlayers(this, 6.0, 8.0, 6.0, 12.0);

        this.entityData.set(CANNON_RECOIL_TIME, 60);
        this.entityData.set(FIRE_ANIM, 3);

        // 弾薬消費
        if (!hasCreativeAmmo) {
            // 弾薬消費処理をここに入れる（必要に応じて）
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
                this.sprintInputDown = false;
                this.entityData.set(POWER, 0.0F);
            }
            boolean currentInput = this.sprintInputDown;

            if (currentInput && !wasSprintInputDown) {
                // 押した瞬間だけ反応する（トグル）
                sprintToggled = !sprintToggled;
                setPodToggled(sprintToggled);
            }
            wasSprintInputDown = currentInput;

            if (this.forwardInputDown) {
                this.entityData.set(POWER, org.joml.Math.min((Float)this.entityData.get(POWER) + ((Float)this.entityData.get(POWER) < 0.0F ? 0.012F : 0.0024F), 0.18F));
            }

            if (this.backInputDown) {
                this.entityData.set(POWER, org.joml.Math.max((Float)this.entityData.get(POWER) - ((Float)this.entityData.get(POWER) > 0.0F ? 0.012F : 0.0024F), -0.13F));
            }

            if (this.rightInputDown) {
                this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) + 0.1F);
            } else if (this.leftInputDown) {
                this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) - 0.1F);
            }

            if (this.forwardInputDown || this.backInputDown) {
                this.consumeEnergy((Integer)VehicleConfig.LAV_150_ENERGY_COST.get());
            }

            this.entityData.set(POWER, (Float)this.entityData.get(POWER) * (this.upInputDown ? 0.5F : (!this.rightInputDown && !this.leftInputDown ? 0.99F : 0.977F)));
            this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) * (float) org.joml.Math.max((double)0.76F - (double)0.1F * this.getDeltaMovement().horizontalDistance(), 0.3));
            double s0 = this.getDeltaMovement().dot(this.getViewVector(1.0F));
            this.setLeftWheelRot((float)((double)this.getLeftWheelRot() - (double)1.25F * s0 - this.getDeltaMovement().horizontalDistance() * (double)Mth.clamp(1.5F * (Float)this.entityData.get(DELTA_ROT), -5.0F, 5.0F)));
            this.setRightWheelRot((float)((double)this.getRightWheelRot() - (double)1.25F * s0 + this.getDeltaMovement().horizontalDistance() * (double)Mth.clamp(1.5F * (Float)this.entityData.get(DELTA_ROT), -5.0F, 5.0F)));
            this.setRudderRot(Mth.clamp(this.getRudderRot() - (Float)this.entityData.get(DELTA_ROT), -0.8F, 0.8F) * 0.75F);
            int i;
            if ((Boolean)this.entityData.get(L_WHEEL_DAMAGED) && (Boolean)this.entityData.get(R_WHEEL_DAMAGED)) {
                this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.93F);
                i = 0;
            } else if ((Boolean)this.entityData.get(L_WHEEL_DAMAGED)) {
                this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.975F);
                i = 3;
            } else if ((Boolean)this.entityData.get(R_WHEEL_DAMAGED)) {
                this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.975F);
                i = -3;
            } else {
                i = 0;
            }

            if ((Boolean)this.entityData.get(ENGINE1_DAMAGED)) {
                this.entityData.set(POWER, (Float)this.entityData.get(POWER) * 0.875F);
            }

            this.setYRot((float)((double)this.getYRot() - Math.max((double)(this.isInWater() && !this.onGround() ? 5 : 10) * this.getDeltaMovement().horizontalDistance(), (double)0.0F) * (double)this.getRudderRot() * (double)((Float)this.entityData.get(POWER) > 0.0F ? 1 : -1) - (double)i * s0));
            if (this.isInWater() || this.onGround()) {
                float power = (Float)this.entityData.get(POWER) * Mth.clamp(1.0F + (float)(s0 > (double)0.0F ? 1 : -1) * this.getXRot() / 35.0F, 0.0F, 2.0F);
                this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).scale((double)((!this.isInWater() && !this.onGround() ? 0.05F : (this.isInWater() && !this.onGround() ? 0.3F : 1.0F)) * power))));
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
            Matrix4f transform = this.getVehicleTransform(1.0F);
            int i = this.getSeatIndex(passenger);
            Vector4f worldPosition;
            if (i == 0) {
                worldPosition = this.transformPosition(transform, 1.0F, 0.8F, 6.6F);
            } else {
                worldPosition = this.transformPosition(transform, 0.0F, 1.0F, 0.0F);
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

        float min = -32.5F - r * this.getXRot() - r2 * this.getRoll();
        float max = 15.0F - r * this.getXRot() - r2 * this.getRoll();
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
        return 300;
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
        return false;
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
    }

    @OnlyIn(Dist.CLIENT)
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        super.renderThirdPersonOverlay(guiGraphics, font, player, screenWidth, screenHeight, scale);
    }

    public boolean hasDecoy() {
        return true;
    }

    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return zoom ? 0.23 : (Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.3 : 0.4);
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
