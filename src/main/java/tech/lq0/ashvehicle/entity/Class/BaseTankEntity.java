package tech.lq0.ashvehicle.entity.Class;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.projectile.CannonShellEntity;
import com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity;
import com.atsuishio.superbwarfare.entity.vehicle.Yx100Entity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.CannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.ProjectileWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.lq0.ashvehicle.init.ModSounds;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class BaseTankEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity {
    public static final EntityDataAccessor<Integer> MG_AMMO;
    public static final EntityDataAccessor<Integer> LOADED_AP;
    public static final EntityDataAccessor<Integer> LOADED_HE;
    public static final EntityDataAccessor<Integer> LOADED_AMMO_TYPE;
    public static final EntityDataAccessor<Integer> GUN_FIRE_TIME;
    private final AnimatableInstanceCache cache;
    public int reloadCoolDown;

    public BaseTankEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
    }

    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{
                (new CannonShellWeapon()).hitDamage((float)(Integer) VehicleConfig.YX_100_AP_CANNON_DAMAGE.get())
                        .explosionRadius(((Double)VehicleConfig.YX_100_AP_CANNON_EXPLOSION_RADIUS.get()).floatValue())
                        .explosionDamage((float)(Integer)VehicleConfig.YX_100_AP_CANNON_EXPLOSION_DAMAGE.get())
                        .fireProbability(0.0F)
                        .fireTime(0)
                        .durability(100)
                        .velocity(40.0F)
                        .gravity(0.1F)
                        .sound((SoundEvent) com.atsuishio.superbwarfare.init.ModSounds.INTO_MISSILE.get()).ammo((Item) ModItems.AP_5_INCHES.get())
                        .icon(Mod.loc("textures/screens/vehicle_weapon/ap_shell.png"))
                        .sound1p(ModSounds.M1A1_ABRAMS_FIRE.get())
                        .sound3p(ModSounds.M1A1_ABRAMS_FIRE.get())
                        .sound3pFar(ModSounds.YX_100_VERY_FAR.get())
                        .sound3pVeryFar(ModSounds.YX_100_VERY_FAR.get()),
                (new CannonShellWeapon()).hitDamage((float)(Integer)VehicleConfig.YX_100_HE_CANNON_DAMAGE.get())
                        .explosionRadius(((Double)VehicleConfig.YX_100_HE_CANNON_EXPLOSION_RADIUS.get()).floatValue())
                        .explosionDamage((float)(Integer)VehicleConfig.YX_100_HE_CANNON_EXPLOSION_DAMAGE.get())
                        .fireProbability(0.18F)
                        .fireTime(2)
                        .durability(0)
                        .velocity(25.0F)
                        .gravity(0.1F)
                        .sound((SoundEvent) com.atsuishio.superbwarfare.init.ModSounds.INTO_CANNON.get())
                        .ammo((Item)ModItems.HE_5_INCHES.get())
                        .icon(Mod.loc("textures/screens/vehicle_weapon/he_shell.png"))
                        .sound1p(ModSounds.M1A1_ABRAMS_FIRE.get())
                        .sound3p(ModSounds.M1A1_ABRAMS_FIRE.get())
                        .sound3pFar(ModSounds.YX_100_VERY_FAR.get())
                        .sound3pVeryFar(ModSounds.YX_100_VERY_FAR.get()),
                (new ProjectileWeapon()).damage((Double)VehicleConfig.LAV_150_MACHINE_GUN_DAMAGE.get())
                        .headShot(2.0F)
                        .zoom(false)
                        .sound((SoundEvent) com.atsuishio.superbwarfare.init.ModSounds.INTO_CANNON.get())
                        .icon(Mod.loc("textures/screens/vehicle_weapon/gun_7_62mm.png"))
                        .sound1p((SoundEvent) com.atsuishio.superbwarfare.init.ModSounds.COAX_FIRE_1P.get())
                        .sound3p((SoundEvent) com.atsuishio.superbwarfare.init.ModSounds.RPK_FIRE_3P.get())
                        .sound3pFar((SoundEvent) com.atsuishio.superbwarfare.init.ModSounds.RPK_FAR.get())
                        .sound3pVeryFar((SoundEvent) com.atsuishio.superbwarfare.init.ModSounds.RPK_VERYFAR.get())
        }};
    }

    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition((double)3.0F, (double)1.0F, (double)0.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MG_AMMO, 0);
        this.entityData.define(LOADED_AP, 0);
        this.entityData.define(LOADED_HE, 0);
        this.entityData.define(LOADED_AMMO_TYPE, 0);
        this.entityData.define(GUN_FIRE_TIME, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedAP", (Integer)this.entityData.get(LOADED_AP));
        compound.putInt("LoadedHE", (Integer)this.entityData.get(LOADED_HE));
        compound.putInt("LoadedAmmoType", (Integer)this.entityData.get(LOADED_AMMO_TYPE));
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_AP, compound.getInt("LoadedAP"));
        this.entityData.set(LOADED_HE, compound.getInt("LoadedHE"));
        this.entityData.set(LOADED_AMMO_TYPE, compound.getInt("LoadedAmmoType"));
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> this.getSourceAngle(source, 0.4F) * damage);
    }

    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        this.playSound((SoundEvent)ModSounds.WHEEL_STEP.get(), (float)(this.getDeltaMovement().length() * 0.15), this.random.nextFloat() * 0.15F + 1.05F);
    }

    public VehicleWeapon getWeapon(int index) {
        VehicleWeapon[][] weapons = this.initWeapons(); // 1行複数武装（1Dとみなす）
        if (weapons.length > 0 && index >= 0 && index < weapons[0].length) {
            return weapons[0][index]; // ✅ 外側配列の 0 番目の行から取り出す
        }
        return null;
    }
    private final float dis = 100.0f;

    public void baseTick() {
        super.baseTick();
        if (this.getLeftTrack() < 0.0F) {
            this.setLeftTrack(dis);
        }

        if (this.getLeftTrack() > dis) {
            this.setLeftTrack(0.0F);
        }

        if (this.getRightTrack() < 0.0F) {
            this.setRightTrack(dis);
        }

        if (this.getRightTrack() > dis) {
            this.setRightTrack(0.0F);
        }

        if ((Integer)this.entityData.get(GUN_FIRE_TIME) > 0) {
            this.entityData.set(GUN_FIRE_TIME, (Integer)this.entityData.get(GUN_FIRE_TIME) - 1);
        }

        if (this.reloadCoolDown == 70) {
            Entity i = this.getFirstPassenger();
            if (i instanceof Player) {
                Player player = (Player)i;
                SoundTool.playLocalSound(player, (SoundEvent) com.atsuishio.superbwarfare.init.ModSounds.YX_100_RELOAD.get());
            }
        }

        if (this.level() instanceof ServerLevel) {
            boolean hasCreativeAmmo = false;

            for(int i = 0; i < this.getMaxPassengers(); ++i) {
                Entity var4 = this.getNthEntity(i);
                if (var4 instanceof Player) {
                    Player pPlayer = (Player)var4;
                    if (InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                        hasCreativeAmmo = true;
                    }
                }
            }

            if (this.reloadCoolDown > 0 && ((Integer)this.entityData.get(LOADED_AMMO_TYPE) == 0 && (hasCreativeAmmo || this.countItem((Item)ModItems.AP_5_INCHES.get()) > 0) || (Integer)this.entityData.get(LOADED_AMMO_TYPE) == 1 && (hasCreativeAmmo || this.countItem((Item)ModItems.HE_5_INCHES.get()) > 0))) {
                --this.reloadCoolDown;
            }

            this.handleAmmo();
        }

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

        Level var10 = this.level();
        if (var10 instanceof ServerLevel serverLevel) {
            if (this.isInWater() && this.getDeltaMovement().length() > 0.1) {
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, this.getX() + (double)0.5F * this.getDeltaMovement().x, this.getY() + this.getSubmergedHeight(this) - 0.2, this.getZ() + (double)0.5F * this.getDeltaMovement().z, (int)((double)2.0F + (double)4.0F * this.getDeltaMovement().length()), 0.65, (double)0.0F, 0.65, (double)0.0F, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + (double)0.5F * this.getDeltaMovement().x, this.getY() + this.getSubmergedHeight(this) - 0.2, this.getZ() + (double)0.5F * this.getDeltaMovement().z, (int)((double)2.0F + (double)10.0F * this.getDeltaMovement().length()), 0.65, (double)0.0F, 0.65, (double)0.0F, true);
            }
        }

        this.turretAngle(5.0F, 5.0F);
        this.gunnerAngle(15.0F, 15.0F);
        this.lowHealthWarning();
        this.terrainCompact(4.375F, 6.3125F);
        this.inertiaRotate(1.2F);
        this.releaseSmokeDecoy(this.getTurretVector(1.0F));
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
            float x7 = this.terrainCompactTrackValue(w, l - 6.0F)[0];
            List<Float> numbersX = Arrays.asList(x1, x2, x3, x4, x5, x6, x7);
            float maxX = (Float) Collections.max(numbersX);
            float minX = (Float)Collections.min(numbersX);
            float z1 = this.terrainCompactTrackValue(w, l)[1];
            float z2 = this.terrainCompactTrackValue(w, l - 1.0F)[1];
            float z3 = this.terrainCompactTrackValue(w, l - 2.0F)[1];
            float z4 = this.terrainCompactTrackValue(w, l - 3.0F)[1];
            float z5 = this.terrainCompactTrackValue(w, l - 4.0F)[1];
            float z6 = this.terrainCompactTrackValue(w, l - 5.0F)[1];
            float z7 = this.terrainCompactTrackValue(w, l - 6.0F)[1];
            List<Float> numbersZ = Arrays.asList(z1, z2, z3, z4, z5, z6, z7);
            float maxZ = (Float)Collections.max(numbersZ);
            float minZ = (Float)Collections.min(numbersZ);
            float diffX = org.joml.Math.clamp(-15.0F, 15.0F, (minX + maxX) / 2.0F);
            this.setXRot(Mth.clamp(this.getXRot() + 0.15F * diffX, -45.0F, 45.0F));
            float diffZ = org.joml.Math.clamp(-15.0F, 15.0F, minZ + maxZ);
            this.setZRot(Mth.clamp(this.getRoll() + 0.15F * diffZ, -45.0F, 45.0F));
            if (maxZ + minZ > 10.0F) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.15, 0));
            }
        } else if (this.isInWater()) {
            this.setXRot(this.getXRot() * 0.9F);
            this.setZRot(this.getRoll() * 0.9F);
        }
    }

    public boolean canCollideHardBlock() {
        return this.getDeltaMovement().horizontalDistance() > 0.05 || (double)Mth.abs((Float)this.entityData.get(POWER)) > 0.1;
    }

    public boolean canCollideBlockBeastly() {
        return this.getDeltaMovement().horizontalDistance() > 0.3;
    }

    public void handleAmmo() {
        if (this.getWeaponIndex(0) == 0 || this.getWeaponIndex(0) == 1) {
            this.entityData.set(LOADED_AMMO_TYPE, this.getWeaponIndex(0));
        }

        boolean hasCreativeAmmo = false;

        for(int i = 0; i < this.getMaxPassengers(); ++i) {
            Entity var4 = this.getNthEntity(i);
            if (var4 instanceof Player pPlayer) {
                if (InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                    hasCreativeAmmo = true;
                }
            }
        }

        if (hasCreativeAmmo) {
            this.entityData.set(AMMO, 9999);
            this.entityData.set(MG_AMMO, 9999);
        } else {
            this.entityData.set(AMMO, this.countItem(this.getWeapon(0).ammo));
            this.entityData.set(MG_AMMO, this.countItem(this.getWeapon(1).ammo));
        }

        if (((Integer)this.getEntityData().get(LOADED_AP) == 0 || (Integer)this.getEntityData().get(LOADED_HE) == 0) && this.reloadCoolDown <= 0 && (hasCreativeAmmo || this.hasItem(this.getWeapon(0).ammo))) {
            if ((Integer)this.entityData.get(LOADED_AMMO_TYPE) == 0 && (Integer)this.entityData.get(LOADED_AP) == 0) {
                this.entityData.set(LOADED_AP, 1);
                if (!hasCreativeAmmo) {
                    this.consumeItem((Item)ModItems.AP_5_INCHES.get(), 1);
                }
            }

            if ((Integer)this.entityData.get(LOADED_AMMO_TYPE) == 1 && (Integer)this.entityData.get(LOADED_HE) == 0) {
                this.entityData.set(LOADED_HE, 1);
                if (!hasCreativeAmmo) {
                    this.consumeItem((Item)ModItems.HE_5_INCHES.get(), 1);
                }
            }
        }

    }

    public void move(@NotNull MoverType movementType, @NotNull Vec3 movement) {
        super.move(movementType, movement);
        if (this.isInWater() && this.horizontalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, 0.07, (double)0.0F));
        }
        Vec3 lookVec = this.getLookAngle(); // 向いてる方向ベクトル
        BlockPos currentPos = this.blockPosition();
        BlockPos frontPos = currentPos.relative(Direction.getNearest((float)lookVec.x, 0, (float)lookVec.z));

        BlockState state = level().getBlockState(frontPos);
        VoxelShape shape = state.getCollisionShape(level(), frontPos);
        if (!shape.isEmpty()) {
            double maxY = 0.0;
            for (AABB box : shape.toAabbs()) {
                if (box.maxY > maxY) maxY = box.maxY;
            }
            double blockTopY = frontPos.getY() + maxY;
            if (blockTopY > this.getY() && blockTopY - this.getY() <= 1.0) {
                // ブロックの上に乗る
                this.setPos(this.getX(), blockTopY, this.getZ());
            }
        }
    }

    public void vehicleShoot(Player player, int type) {
        boolean hasCreativeAmmo = false;

        for(int i = 0; i < this.getMaxPassengers() - 1; ++i) {
            Entity ammo = this.getNthEntity(i);
            if (ammo instanceof Player pPlayer) {
                if (InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                    hasCreativeAmmo = true;
                }
            }
        }

        if (type == 0) {
            if (this.reloadCoolDown == 0 && (this.getWeaponIndex(0) == 0 || this.getWeaponIndex(0) == 1)) {
                if (!this.canConsume((Integer)VehicleConfig.YX_100_SHOOT_COST.get())) {
                    player.displayClientMessage(Component.translatable("tips.superbwarfare.annihilator.energy_not_enough").withStyle(ChatFormatting.RED), true);
                    return;
                }

                Matrix4f transform = this.getBarrelTransform(1.0F);
                Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
                CannonShellWeapon cannonShell = (CannonShellWeapon)this.getWeapon(0);
                CannonShellEntity entityToSpawn = cannonShell.create(player);
                entityToSpawn.setPos((double)worldPosition.x - 1.1 * this.getDeltaMovement().x, (double)worldPosition.y, (double)worldPosition.z - 1.1 * this.getDeltaMovement().z);
                entityToSpawn.shoot(this.getBarrelVector(1.0F).x, this.getBarrelVector(1.0F).y + (double)0.005F, this.getBarrelVector(1.0F).z, cannonShell.velocity, 0.02F);
                this.level().addFreshEntity(entityToSpawn);
                if (!player.level().isClientSide) {
                    this.playShootSound3p(player, 0, 8, 16, 32);
                }

                this.entityData.set(CANNON_RECOIL_TIME, 40);
                if (this.getWeaponIndex(0) == 0) {
                    this.entityData.set(LOADED_AP, 0);
                } else if (this.getWeaponIndex(0) == 1) {
                    this.entityData.set(LOADED_HE, 0);
                }

                this.consumeEnergy(100);
                this.entityData.set(YAW, this.getTurretYRot());
                this.reloadCoolDown = 80;
                Level swarmDroneEntity = this.level();
                if (swarmDroneEntity instanceof ServerLevel) {
                    ServerLevel server = (ServerLevel)swarmDroneEntity;
                    server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() + (double)5.0F * this.getBarrelVector(1.0F).x, this.getY() + 0.1, this.getZ() + (double)5.0F * this.getBarrelVector(1.0F).z, 300, (double)6.0F, 0.02, (double)6.0F, 0.005);
                    double x = (double)worldPosition.x + (double)9.0F * this.getBarrelVector(1.0F).x;
                    double y = (double)worldPosition.y + (double)9.0F * this.getBarrelVector(1.0F).y;
                    double z = (double)worldPosition.z + (double)9.0F * this.getBarrelVector(1.0F).z;
                    server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);
                    server.sendParticles(ParticleTypes.CLOUD, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);
                    int count = 6;

                    for(float i = 9.5F; i < 23.0F; i += 0.5F) {
                        server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, (double)worldPosition.x + (double)i * this.getBarrelVector(1.0F).x, (double)worldPosition.y + (double)i * this.getBarrelVector(1.0F).y, (double)worldPosition.z + (double)i * this.getBarrelVector(1.0F).z, Mth.clamp(count--, 1, 5), 0.15, 0.15, 0.15, 0.0025);
                    }

                    Vector4f worldPositionL = this.transformPosition(transform, -0.35F, 0.0F, 0.0F);
                    Vector4f worldPositionR = this.transformPosition(transform, 0.35F, 0.0F, 0.0F);

                    for(float i = 3.0F; i < 6.0F; i += 0.5F) {
                        server.sendParticles(ParticleTypes.CLOUD, (double)worldPositionL.x + (double)i * this.getBarrelVector(1.0F).x, (double)worldPositionL.y + (double)i * this.getBarrelVector(1.0F).y, (double)worldPositionL.z + (double)i * this.getBarrelVector(1.0F).z, 1, 0.025, 0.025, 0.025, 0.0015);
                        server.sendParticles(ParticleTypes.CLOUD, (double)worldPositionR.x + (double)i * this.getBarrelVector(1.0F).x, (double)worldPositionR.y + (double)i * this.getBarrelVector(1.0F).y, (double)worldPositionR.z + (double)i * this.getBarrelVector(1.0F).z, 1, 0.025, 0.025, 0.025, 0.0015);
                    }
                }

                Level level = player.level();
                Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

                for(Entity target : level.getEntitiesOfClass(Entity.class, (new AABB(center, center)).inflate((double)8.0F), (e) -> true).stream().sorted(Comparator.comparingDouble((e) -> e.distanceToSqr(center))).toList()) {
                    if (target instanceof ServerPlayer) {
                        ServerPlayer serverPlayer = (ServerPlayer)target;
                        Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ShakeClientMessage((double)10.0F, (double)8.0F, (double)60.0F, this.getX(), this.getEyeY(), this.getZ()));
                    }
                }
            } else if (this.getWeaponIndex(0) == 2) {
                if (this.cannotFireCoax) {
                    return;
                }

                Matrix4f transform = this.getBarrelTransform(1.0F);
                Vector4f worldPosition = this.transformPosition(transform, -0.12F, 0.15F, 2.0F);
                if ((Integer)this.entityData.get(MG_AMMO) > 0 || hasCreativeAmmo) {
                    ProjectileEntity projectileRight = ((ProjectileWeapon)this.getWeapon(2)).create(player).setGunItemId(this.getType().getDescriptionId() + ".1");
                    projectileRight.setPos((double)worldPosition.x - 1.1 * this.getDeltaMovement().x, (double)worldPosition.y, (double)worldPosition.z - 1.1 * this.getDeltaMovement().z);
                    projectileRight.shoot(player, this.getBarrelVector(1.0F).x, this.getBarrelVector(1.0F).y + (double)0.005F, this.getBarrelVector(1.0F).z, 36.0F, 0.25F);
                    this.level().addFreshEntity(projectileRight);
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
                            this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.HEAVY_AMMO.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
                        }
                    }
                }

                this.entityData.set(COAX_HEAT, (Integer)this.entityData.get(COAX_HEAT) + 4);
                this.entityData.set(FIRE_ANIM, 2);
                if (!player.level().isClientSide) {
                    this.playShootSound3p(player, 0, 4, 12, 24);
                }
            }
        }

        if (type == 1) {
            if (this.cannotFire) {
                return;
            }

            Matrix4f transform = this.getGunTransform(1.0F);
            Vector4f worldPosition = this.transformPosition(transform, 0.0F, -0.25F, 0.0F);
            ProjectileWeapon projectile = (ProjectileWeapon)this.getWeapon(1);
            ProjectileEntity projectileEntity = projectile.create(player).setGunItemId(this.getType().getDescriptionId() + ".2");
            projectileEntity.setPos((double)worldPosition.x - 1.1 * this.getDeltaMovement().x, (double)worldPosition.y, (double)worldPosition.z - 1.1 * this.getDeltaMovement().z);
            projectileEntity.shoot(this.getGunnerVector(1.0F).x, this.getGunnerVector(1.0F).y + (double)0.01F, this.getGunnerVector(1.0F).z, 20.0F, 0.3F);
            this.level().addFreshEntity(projectileEntity);
            if (!player.level().isClientSide) {
                this.playShootSound3p(player, 1, 4, 12, 24);
            }

            this.entityData.set(GUN_FIRE_TIME, 2);
            this.entityData.set(HEAT, (Integer)this.entityData.get(HEAT) + 4);
            Level level = player.level();
            Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

            for(Entity target : level.getEntitiesOfClass(Entity.class, (new AABB(center, center)).inflate((double)4.0F), (e) -> true).stream().sorted(Comparator.comparingDouble((e) -> e.distanceToSqr(center))).toList()) {
                if (target instanceof ServerPlayer) {
                    ServerPlayer serverPlayer = (ServerPlayer)target;
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ShakeClientMessage((double)6.0F, (double)4.0F, (double)6.0F, this.getX(), this.getEyeY(), this.getZ()));
                }
            }

            if (hasCreativeAmmo) {
                return;
            }

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
                this.consumeItem(this.getWeapon(1).ammo, 1);
            }
        }
    }

    public void travel() {
        Entity passenger0 = this.getFirstPassenger();
        if (this.getEnergy() > 0) {
            if (!(passenger0 instanceof Player)) {
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
                this.consumeEnergy((Integer)VehicleConfig.YX_100_ENERGY_COST.get());
            }

            this.entityData.set(POWER, (Float)this.entityData.get(POWER) * (this.upInputDown ? 0.5F : (!this.rightInputDown && !this.leftInputDown ? 0.96F : 0.947F)));
            this.entityData.set(DELTA_ROT, (Float)this.entityData.get(DELTA_ROT) * (float) org.joml.Math.max((double)0.76F - (double)0.1F * this.getDeltaMovement().horizontalDistance(), 0.3));
            double s0 = this.getDeltaMovement().dot(this.getViewVector(1.0F));
            this.setLeftWheelRot((float)((double)this.getLeftWheelRot() - (double)1.25F * s0 + (double)Mth.clamp(0.75F * (Float)this.entityData.get(DELTA_ROT), -5.0F, 5.0F)));
            this.setRightWheelRot((float)((double)this.getRightWheelRot() - (double)1.25F * s0 - (double)Mth.clamp(0.75F * (Float)this.entityData.get(DELTA_ROT), -5.0F, 5.0F)));
            this.setLeftTrack((float)((double)this.getLeftTrack() - (java.lang.Math.PI * 1.5D) * s0 + Mth.clamp(1.2566370801612687 * (double)(Float)this.entityData.get(DELTA_ROT), (double)-5.0F, (double)5.0F)));
            this.setRightTrack((float)((double)this.getRightTrack() - (java.lang.Math.PI * 1.5D) * s0 - Mth.clamp(1.2566370801612687 * (double)(Float)this.entityData.get(DELTA_ROT), (double)-5.0F, (double)5.0F)));
            this.setYRot((float)((double)this.getYRot() - (this.isInWater() && !this.onGround() ? (double)2.5F : (double)6.0F) * (double)(Float)this.entityData.get(DELTA_ROT)));
            if (this.isInWater() || this.onGround()) {
                float power = (Float)this.entityData.get(POWER) * Mth.clamp(1.0F + (float)(s0 > (double)0.0F ? 1 : -1) * this.getXRot() / 35.0F, 0.0F, 2.0F);
                this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).scale((double)((!this.isInWater() && !this.onGround() ? 0.13F : (this.isInWater() && !this.onGround() ? 2.0F : 2.4F)) * power))));
            }

        }
    }

    public SoundEvent getEngineSound() {
        return ModSounds.M1A1_ABRAMS_ENGINE.get();
    }

    public float getEngineSoundVolume() {
        return org.joml.Math.max(Mth.abs((Float)this.entityData.get(POWER)), Mth.abs(0.1F * (Float)this.entityData.get(DELTA_ROT))) * 2.5F;
    }

    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getTurretTransform(1.0F);
            int i = this.getOrderedPassengers().indexOf(passenger);
            Vector4f var10000;
            switch (i) {
                case 0 -> var10000 = this.transformPosition(transform, 0.6669625F, 0.07F, 0.4776875F);
                case 1 -> var10000 = this.transformPosition(transform, -0.7580562F, 0.3F, -0.57275623F);
                case 2 -> var10000 = this.transformPosition(transform, 0.86219376F, 0.07F, -0.5696875F);
                default -> throw new IllegalStateException("Unexpected value: " + i);
            }

            Vector4f worldPosition = var10000;
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

    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = this.getTurretTransform(ticks);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 1.0F, 0.6F);
        return new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
    }

    public int getMaxPassengers() {
        return 3;
    }

    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = this.getBarrelTransform(pPartialTicks);
        Vector4f rootPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f targetPosition = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        return (new Vec3((double)rootPosition.x, (double)rootPosition.y, (double)rootPosition.z)).vectorTo(new Vec3((double)targetPosition.x, (double)targetPosition.y, (double)targetPosition.z));
    }

    public Vec3 getTurretVector(float pPartialTicks) {
        Matrix4f transform = this.getTurretTransform(pPartialTicks);
        Vector4f rootPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f targetPosition = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        return (new Vec3((double)rootPosition.x, (double)rootPosition.y, (double)rootPosition.z)).vectorTo(new Vec3((double)targetPosition.x, (double)targetPosition.y, (double)targetPosition.z));
    }

    public Vec3 getGunnerVector(float pPartialTicks) {
        Matrix4f transform = this.getGunnerBarrelTransform(pPartialTicks);
        Vector4f rootPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f targetPosition = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        return (new Vec3((double)rootPosition.x, (double)rootPosition.y, (double)rootPosition.z)).vectorTo(new Vec3((double)targetPosition.x, (double)targetPosition.y, (double)targetPosition.z));
    }

    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = this.getTurretTransform(ticks);
        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.56345F, 0.6477125F);
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
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 2.1059375F, -0.43125F);
        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, this.turretYRotO, this.getTurretYRot())));
        return transformV;
    }

    public Matrix4f getGunTransform(float ticks) {
        Matrix4f transformT = this.getTurretTransform(ticks);
        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = this.transformPosition(transform, -0.7580562F, 1.1446375F, -0.57275623F);
        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformT.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, this.gunYRotO, this.getGunYRot()) - Mth.lerp(ticks, this.turretYRotO, this.getTurretYRot())));
        return transformT;
    }

    public Matrix4f getGunnerBarrelTransform(float ticks) {
        Matrix4f transformG = this.getGunTransform(ticks);
        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.35984376F, 0.0551625F);
        transformG.translate(worldPosition.x, worldPosition.y, worldPosition.z);
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

        float x = Mth.lerp(ticks, this.gunXRotO, this.getGunXRot());
        float xV = Mth.lerp(ticks, this.xRotO, this.getXRot());
        float z = Mth.lerp(ticks, this.prevRoll, this.getRoll());
        transformG.rotate(Axis.XP.rotationDegrees(x + r * xV + r2 * z));
        return transformG;
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
        Minecraft mc = Minecraft.getInstance();
        if (entity.level().isClientSide && entity == this.getFirstPassenger()) {
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

            float min = -30.0F - r * this.getXRot() - r2 * this.getRoll();
            float max = 10.0F - r * this.getXRot() - r2 * this.getRoll();
            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, min, max);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);
            if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
                float f2 = Mth.wrapDegrees(entity.getYRot() - this.getBarrelYRot(1.0F));
                float f3 = Mth.clamp(f2, -20.0F, 20.0F);
                entity.yRotO += f3 - f2;
                entity.setYRot(entity.getYRot() + f3 - f2);
                entity.setYBodyRot(this.getBarrelYRot(1.0F));
            }
        } else if (entity == this.getNthEntity(1)) {
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

            float min = -60.0F - r * this.getXRot() - r2 * this.getRoll();
            float max = 10.0F - r * this.getXRot() - r2 * this.getRoll();
            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, min, max);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);
            if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
                float f2 = Mth.wrapDegrees(entity.getYRot() - this.getGunYRot(1.0F));
                float f3 = Mth.clamp(f2, -150.0F, 150.0F);
                entity.yRotO += f3 - f2;
                entity.setYRot(entity.getYRot() + f3 - f2);
                entity.setYBodyRot(entity.getYRot());
            }
        } else if (entity == this.getNthEntity(2)) {
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

            float min = -90.0F - r * this.getXRot() - r2 * this.getRoll();
            float max = 22.5F - r * this.getXRot() - r2 * this.getRoll();
            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, min, max);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);
        }

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
        if (player == this.getNthEntity(0)) {
            if (this.getWeaponIndex(0) == 0 || this.getWeaponIndex(0) == 1) {
                return 15;
            }

            if (this.getWeaponIndex(0) == 2) {
                return 500;
            }
        }

        if (player == this.getNthEntity(1)) {
            return 500;
        } else if (player == this.getNthEntity(2)) {
            return 600;
        } else {
            return 15;
        }
    }

    public boolean canShoot(Player player) {
        if (player == this.getNthEntity(0)) {
            if (this.getWeaponIndex(0) == 0) {
                return (Integer)this.entityData.get(LOADED_AP) > 0 && this.getEnergy() > (Integer)VehicleConfig.YX_100_SHOOT_COST.get();
            }

            if (this.getWeaponIndex(0) == 1) {
                return (Integer)this.entityData.get(LOADED_HE) > 0 && this.getEnergy() > (Integer)VehicleConfig.YX_100_SHOOT_COST.get();
            }

            if (this.getWeaponIndex(0) == 2) {
                return ((Integer)this.entityData.get(MG_AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !this.cannotFireCoax;
            }
        }
        return ((Integer)this.entityData.get(MG_AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !this.cannotFire;
    }

    public int getAmmoCount(Player player) {
        if (player == this.getNthEntity(0)) {
            if (this.getWeaponIndex(0) == 0) {
                return (Integer)this.entityData.get(LOADED_AP);
            }

            if (this.getWeaponIndex(0) == 1) {
                return (Integer)this.entityData.get(LOADED_HE);
            }

            if (this.getWeaponIndex(0) == 2) {
                return (Integer)this.entityData.get(MG_AMMO);
            }
        }

        return (Integer)this.entityData.get(MG_AMMO);
    }

    public boolean banHand(Player player) {
        if (player != this.getNthEntity(0) && player != this.getNthEntity(1)) {
            return player == this.getNthEntity(2) && !player.isShiftKeyDown();
        } else {
            return true;
        }
    }

    public boolean hidePassenger(Entity entity) {
        return entity == this.getNthEntity(0) || entity == this.getNthEntity(1);
    }

    public int zoomFov() {
        return 3;
    }

    public boolean hasTracks() {
        return true;
    }

    public int getWeaponHeat(Player player) {
        if (player == this.getNthEntity(0)) {
            return (Integer)this.entityData.get(COAX_HEAT);
        } else {
            return player == this.getNthEntity(1) ? (Integer)this.entityData.get(HEAT) : 0;
        }
    }

    public void changeWeapon(int index, int value, boolean isScroll) {
        if (index == 0) {
            List<VehicleWeapon> weapons = this.getAvailableWeapons(index);
            if (!weapons.isEmpty()) {
                int count = weapons.size();
                int typeIndex = isScroll ? (value + this.getWeaponIndex(index) + count) % count : value;
                if (typeIndex == 0 || typeIndex == 1) {
                    if ((Integer)this.entityData.get(LOADED_AP) > 0 && typeIndex == 1) {
                        Entity clientboundstopsoundpacket = this.getFirstPassenger();
                        if (clientboundstopsoundpacket instanceof Player) {
                            Player player = (Player)clientboundstopsoundpacket;
                            if (!InventoryTool.hasCreativeAmmoBox(player)) {
                                this.insertItem((Item)ModItems.AP_5_INCHES.get(), 1);
                            }
                        }

                        this.entityData.set(LOADED_AP, 0);
                    }

                    if ((Integer)this.entityData.get(LOADED_HE) > 0 && typeIndex == 0) {
                        Entity var11 = this.getFirstPassenger();
                        if (var11 instanceof Player) {
                            Player player = (Player)var11;
                            if (!InventoryTool.hasCreativeAmmoBox(player)) {
                                this.insertItem((Item)ModItems.HE_5_INCHES.get(), 1);
                            }
                        }

                        this.entityData.set(LOADED_HE, 0);
                    }

                    if (typeIndex != (Integer)this.entityData.get(LOADED_AMMO_TYPE)) {
                        this.reloadCoolDown = 80;
                    }

                    Entity var12 = this.getFirstPassenger();
                    if (var12 instanceof ServerPlayer) {
                        ServerPlayer player = (ServerPlayer)var12;
                        ClientboundStopSoundPacket clientboundstopsoundpacket = new ClientboundStopSoundPacket(((SoundEvent)ModSounds.YX_100_RELOAD.get()).getLocation(), SoundSource.PLAYERS);
                        player.connection.send(clientboundstopsoundpacket);
                    }
                }

                WeaponVehicleEntity.super.changeWeapon(index, value, isScroll);
            }
        }
    }

    public Vec3 getGunVec(float ticks) {
        return this.getGunnerVector(ticks);
    }

    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/yx_100_icon.png");
    }

    @OnlyIn(Dist.CLIENT)
    public void renderFirstPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        float minWH = (float) Math.min(screenWidth, screenHeight);
        float scaledMinWH = (float)Mth.floor(minWH * scale);
        float centerW = ((float)screenWidth - scaledMinWH) / 2.0F;
        float centerH = ((float)screenHeight - scaledMinWH) / 2.0F;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.getWeaponIndex(0) == 0) {
            RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/land/tank_cannon_cross_ap.png"), centerW, centerH, 0.0F, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
        } else if (this.getWeaponIndex(0) == 1) {
            RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/land/tank_cannon_cross_he.png"), centerW, centerH, 0.0F, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
        } else if (this.getWeaponIndex(0) == 2) {
            RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/land/lav_gun_cross.png"), centerW, centerH, 0.0F, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
        } else if (this.getWeaponIndex(0) == 3) {
            RenderHelper.preciseBlit(guiGraphics, Mod.loc("textures/screens/land/lav_missile_cross.png"), centerW, centerH, 0.0F, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
        }

        if (this.getWeaponIndex(0) == 0) {
            int var10002 = this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal("AP SHELL  " + var10002 + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : (Serializable)this.getEntityData().get(AMMO))), screenWidth / 2 - 33, screenHeight - 65, 6749952, false);
        } else if (this.getWeaponIndex(0) == 1) {
            int var13 = this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal("HE SHELL  " + var13 + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : (Serializable)this.getEntityData().get(AMMO))), screenWidth / 2 - 33, screenHeight - 65, 6749952, false);
        } else if (this.getWeaponIndex(0) == 2) {
            double heat = (double)(1.0F - (float)(Integer)this.getEntityData().get(COAX_HEAT) / 100.0F);
            Object var14 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal(" 7.62MM LMG " + var14), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float)heat / 3.7453184F, 1.0F, 1.0F), false);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        if (this.getWeaponIndex(0) == 0) {
            int var10002 = this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal("AP SHELL " + var10002 + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : (Serializable)this.getEntityData().get(AMMO))), 30, -9, -1, false);
        } else if (this.getWeaponIndex(0) == 1) {
            int var9 = this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal("HE SHELL " + var9 + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : (Serializable)this.getEntityData().get(AMMO))), 30, -9, -1, false);
        } else if (this.getWeaponIndex(0) == 2) {
            double heat2 = (double)((float)(Integer)this.getEntityData().get(COAX_HEAT) / 100.0F);
            Object var10 = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player);
            guiGraphics.drawString(font, Component.literal("12.7MM HMG " + var10), 30, -9, Mth.hsvToRgb(0.0F, (float)heat2, 1.0F), false);
        }

    }

    public boolean hasDecoy() {
        return true;
    }

    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        if (seatIndex == 0) {
            return zoom ? 0.17 : 0.22;
        } else if (seatIndex == 1) {
            return zoom ? (double)0.25F : 0.35;
        } else {
            return original;
        }
    }

    public boolean isEnclosed(int index) {
        return index != 2;
    }

    @OnlyIn(Dist.CLIENT)
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (zoom || isFirstPerson) {
            if (this.getSeatIndex(player) == 0) {
                return new Vec2((float)(-getYRotFromVector(this.getBarrelVec(partialTicks))), (float)(-getXRotFromVector(this.getBarrelVec(partialTicks))));
            }

            if (this.getSeatIndex(player) == 1) {
                return new Vec2((float)(-getYRotFromVector(this.getGunnerVector(partialTicks))), (float)(-getXRotFromVector(this.getGunnerVector(partialTicks))));
            }
        }

        return super.getCameraRotation(partialTicks, player, false, false);
    }

    @OnlyIn(Dist.CLIENT)
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (zoom || isFirstPerson) {
            if (this.getSeatIndex(player) == 0) {
                if (zoom) {
                    return new Vec3(this.driverZoomPos(partialTicks).x, this.driverZoomPos(partialTicks).y, this.driverZoomPos(partialTicks).z);
                }

                return new Vec3(Mth.lerp((double)partialTicks, player.xo, player.getX()), Mth.lerp((double)partialTicks, player.yo + (double)player.getEyeHeight(), player.getEyeY()), Mth.lerp((double)partialTicks, player.zo, player.getZ()));
            }

            if (this.getSeatIndex(player) == 1) {
                return new Vec3(Mth.lerp((double)partialTicks, player.xo, player.getX()), Mth.lerp((double)partialTicks, player.yo + (double)player.getEyeHeight(), player.getEyeY()), Mth.lerp((double)partialTicks, player.zo, player.getZ()));
            }
        }

        return super.getCameraPosition(partialTicks, player, false, false);
    }

    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/land.png");
    }

    static {
        MG_AMMO = SynchedEntityData.defineId(BaseTankEntity.class, EntityDataSerializers.INT);
        LOADED_AP = SynchedEntityData.defineId(BaseTankEntity.class, EntityDataSerializers.INT);
        LOADED_HE = SynchedEntityData.defineId(BaseTankEntity.class, EntityDataSerializers.INT);
        LOADED_AMMO_TYPE = SynchedEntityData.defineId(BaseTankEntity.class, EntityDataSerializers.INT);
        GUN_FIRE_TIME = SynchedEntityData.defineId(BaseTankEntity.class, EntityDataSerializers.INT);
    }
}
