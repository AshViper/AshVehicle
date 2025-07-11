package Aru.Aru.ashvehicle.entity;

import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.C4Entity;
import com.atsuishio.superbwarfare.entity.projectile.*;
import com.atsuishio.superbwarfare.entity.vehicle.DroneEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.item.Monitor;
import com.atsuishio.superbwarfare.item.common.ammo.MortarShell;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import Aru.Aru.ashvehicle.init.ModEntities;
import java.util.UUID;

public class MQ9Entity extends MobileVehicleEntity implements GeoEntity {
    public static final EntityDataAccessor<Boolean> LINKED;
    public static final EntityDataAccessor<String> CONTROLLER;
    public static final EntityDataAccessor<Integer> KAMIKAZE_MODE;
    public static final EntityDataAccessor<Float> DELTA_X_ROT;
    private final AnimatableInstanceCache cache;
    public boolean fire;
    public int collisionCoolDown;
    public double lastTickSpeed;
    public double lastTickVerticalSpeed;
    public ItemStack currentItem;
    public float pitch;
    public float pitchO;
    private float throttle = 0F;

    public MQ9Entity(EntityType<?> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.currentItem = ItemStack.EMPTY;
    }

    public MQ9Entity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(ModEntities.MQ_9.get(), world);
    }

    public float getBodyPitch() {
        return this.pitch;
    }

    public void setBodyXRot(float rot) {
        this.pitch = rot;
    }

    public boolean sendFireStarParticleOnHurt() {
        return false;
    }

    public boolean playHitSoundOnHurt() {
        return false;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DELTA_X_ROT, 0.0F);
        this.entityData.define(CONTROLLER, "undefined");
        this.entityData.define(LINKED, false);
        this.entityData.define(KAMIKAZE_MODE, 0);
    }

    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Linked", (Boolean)this.entityData.get(LINKED));
        compound.putString("Controller", (String)this.entityData.get(CONTROLLER));
        compound.putInt("Ammo", (Integer)this.entityData.get(AMMO));
        compound.putInt("KamikazeMode", (Integer)this.entityData.get(KAMIKAZE_MODE));
        CompoundTag item = new CompoundTag();
        this.currentItem.save(item);
        compound.put("Item", item);
    }

    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return LazyOptional.empty();
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Linked")) {
            this.entityData.set(LINKED, compound.getBoolean("Linked"));
        }

        if (compound.contains("Controller")) {
            this.entityData.set(CONTROLLER, compound.getString("Controller"));
        }

        if (compound.contains("Ammo")) {
            this.entityData.set(AMMO, compound.getInt("Ammo"));
        }

        if (compound.contains("KamikazeMode")) {
            this.entityData.set(KAMIKAZE_MODE, compound.getInt("KamikazeMode"));
        }

        if (compound.contains("Item")) {
            this.currentItem = ItemStack.of(compound.getCompound("Item"));
        }

    }

    public int maxRepairCoolDown() {
        return -1;
    }

    public void baseTick() {
        this.pitchO = this.getBodyPitch();
        this.setBodyXRot(this.pitch * 0.9F);
        super.baseTick();
        this.setZRot(this.getRoll() * 0.9F);
        this.lastTickSpeed = this.getDeltaMovement().length();
        this.lastTickVerticalSpeed = this.getDeltaMovement().y;
        if (this.collisionCoolDown > 0) {
            --this.collisionCoolDown;
        }

        Player controller = EntityFindUtil.findPlayer(this.level(), (String)this.entityData.get(CONTROLLER));
        if (!this.onGround() && controller != null) {
            ItemStack stack = controller.getMainHandItem();
            if (!stack.is((Item) ModItems.MONITOR.get()) || !stack.getOrCreateTag().getBoolean("Using")) {
                this.upInputDown = false;
                this.downInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.leftInputDown = false;
                this.rightInputDown = false;
            }

            if (this.tickCount % 5 == 0) {
                controller.getInventory().items.stream().filter((pStack) -> pStack.getItem() == ModItems.MONITOR.get()).forEach((pStack) -> {
                    if (pStack.getOrCreateTag().getString("LinkedDrone").equals(this.getStringUUID())) {
                        Monitor.getDronePos(pStack, this.position());
                    }

                });
            }
        }

        if (this.isInWater()) {
            this.hurt(new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.EXPLOSION), controller), 0.25F + (float)((double)2.0F * this.lastTickSpeed));
        }

        if (this.fire) {
            if ((Integer)this.entityData.get(AMMO) > 0) {
                this.entityData.set(AMMO, (Integer)this.entityData.get(AMMO) - 1);
                if (controller != null) {
                    this.droneDrop(controller);
                }
            }

            if ((Integer)this.entityData.get(KAMIKAZE_MODE) != 0 && controller != null) {
                if (controller.getMainHandItem().is((Item)ModItems.MONITOR.get())) {
                    Monitor.disLink(controller.getMainHandItem(), controller);
                }

                this.hurt(new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.EXPLOSION), controller), 10000.0F);
            }

            this.fire = false;
        }

        this.refreshDimensions();
    }

    private void droneDrop(@Nullable Player player) {
        if (!this.level().isClientSide()) {
            RgoGrenadeEntity rgoGrenadeEntity = new RgoGrenadeEntity(player, this.level(), 160);
            rgoGrenadeEntity.setPos(this.getX(), this.getEyeY() - 0.09, this.getZ());
            rgoGrenadeEntity.droneShoot(this);
            this.level().addFreshEntity(rgoGrenadeEntity);
        }

    }

    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == ModItems.MONITOR.get()) {
            if (!player.isCrouching()) {
                if (!(Boolean)this.entityData.get(LINKED)) {
                    if (stack.getOrCreateTag().getBoolean("Linked")) {
                        player.displayClientMessage(Component.translatable("tips.superbwarfare.monitor.already_linked").withStyle(ChatFormatting.RED), true);
                        return InteractionResult.sidedSuccess(this.level().isClientSide());
                    }

                    this.entityData.set(LINKED, true);
                    this.entityData.set(CONTROLLER, player.getStringUUID());
                    Monitor.link(stack, this.getStringUUID());
                    player.displayClientMessage(Component.translatable("tips.superbwarfare.monitor.linked").withStyle(ChatFormatting.GREEN), true);
                    if (player instanceof ServerPlayer) {
                        ServerPlayer serverPlayer = (ServerPlayer)player;
                        serverPlayer.level().playSound((Player)null, serverPlayer.getOnPos(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.5F, 1.0F);
                    }
                } else {
                    player.displayClientMessage(Component.translatable("tips.superbwarfare.drone.already_linked").withStyle(ChatFormatting.RED), true);
                }
            } else if ((Boolean)this.entityData.get(LINKED)) {
                if (!stack.getOrCreateTag().getBoolean("Linked")) {
                    player.displayClientMessage(Component.translatable("tips.superbwarfare.drone.already_linked").withStyle(ChatFormatting.RED), true);
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }

                this.entityData.set(CONTROLLER, "none");
                this.entityData.set(LINKED, false);
                Monitor.disLink(stack, player);
                player.displayClientMessage(Component.translatable("tips.superbwarfare.monitor.unlinked").withStyle(ChatFormatting.RED), true);
                if (player instanceof ServerPlayer) {
                    ServerPlayer serverPlayer = (ServerPlayer)player;
                    serverPlayer.level().playSound((Player)null, serverPlayer.getOnPos(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.5F, 1.0F);
                }
            }
        } else if (stack.is((Item)ModItems.CROWBAR.get()) && player.isCrouching()) {
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack((ItemLike)ModItems.DRONE.get()));

            for(int index0 = 0; index0 < (Integer)this.entityData.get(AMMO); ++index0) {
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack((ItemLike)ModItems.RGO_GRENADE.get()));
            }

            if ((Integer)this.entityData.get(KAMIKAZE_MODE) != 0) {
                ItemHandlerHelper.giveItemToPlayer(player, this.currentItem);
            }

            player.getInventory().items.stream().filter((stack_) -> stack_.getItem() == ModItems.MONITOR.get()).forEach((itemStack) -> {
                if (itemStack.getOrCreateTag().getString("LinkedDrone").equals(this.getStringUUID())) {
                    Monitor.disLink(itemStack, player);
                }

            });
            if (!this.level().isClientSide()) {
                this.discard();
            }
        } else if (stack.getItem() == ModItems.RGO_GRENADE.get() && (Integer)this.entityData.get(KAMIKAZE_MODE) == 0) {
            if ((Integer)this.entityData.get(AMMO) < 6) {
                this.entityData.set(AMMO, (Integer)this.entityData.get(AMMO) + 1);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                if (player instanceof ServerPlayer) {
                    ServerPlayer serverPlayer = (ServerPlayer)player;
                    serverPlayer.level().playSound((Player)null, serverPlayer.getOnPos(), (SoundEvent) ModSounds.BULLET_SUPPLY.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
                }
            }
        } else if (stack.getItem() instanceof MortarShell && (Integer)this.entityData.get(AMMO) == 0 && (Integer)this.entityData.get(KAMIKAZE_MODE) == 0) {
            ItemStack copy = stack.copy();
            copy.setCount(1);
            this.currentItem = copy;
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            this.entityData.set(KAMIKAZE_MODE, 1);
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.level().playSound((Player)null, serverPlayer.getOnPos(), (SoundEvent)ModSounds.BULLET_SUPPLY.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        } else if (stack.getItem() == ModItems.C4_BOMB.get() && (Integer)this.entityData.get(AMMO) == 0 && (Integer)this.entityData.get(KAMIKAZE_MODE) == 0) {
            this.currentItem = new ItemStack(stack.getItem(), 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            this.entityData.set(KAMIKAZE_MODE, 2);
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.level().playSound((Player)null, serverPlayer.getOnPos(), (SoundEvent)ModSounds.BULLET_SUPPLY.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        } else if (stack.getItem() == ModItems.ROCKET.get() && (Integer)this.entityData.get(AMMO) == 0 && (Integer)this.entityData.get(KAMIKAZE_MODE) == 0) {
            this.currentItem = new ItemStack(stack.getItem(), 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            this.entityData.set(KAMIKAZE_MODE, 3);
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.level().playSound((Player)null, serverPlayer.getOnPos(), (SoundEvent)ModSounds.BULLET_SUPPLY.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }

        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    public void travel() {
        float pitch = this.getXRot();
        float yaw = this.getYRot();

        // ▶ スロットル制御（SPACE/SHIFT）
        if (this.upInputDown) {
            throttle = Mth.clamp(throttle + 5.0F, 0F, 100.0F);
        } else if (this.downInputDown) {
            throttle = Mth.clamp(throttle - 5.0F, 0F, 100.0F);
        }
        this.entityData.set(POWER, throttle);

        // ▶ ピッチ（W/S）
        if (this.forwardInputDown) {
            pitch = Mth.clamp(pitch + 0.8F, -45.0F, 45.0F);
        } else if (this.backInputDown) {
            pitch = Mth.clamp(pitch - 0.8F, -45.0F, 45.0F);
        }
        this.setXRot(pitch);

        // ▶ ヨー（A/D）
        if (this.leftInputDown) {
            this.setYRot(this.getYRot() - 1.2F);
        } else if (this.rightInputDown) {
            this.setYRot(this.getYRot() + 1.2F);
        }

        // ▶ ロール演出
        float targetRoll = 0.0F;
        if (this.leftInputDown) targetRoll = 15.0F;
        if (this.rightInputDown) targetRoll = -15.0F;
        this.setZRot(Mth.lerp(0.15F, this.getRoll(), targetRoll));

        // ▶ 向いている方向に進む（揚力なし）
        double speed = throttle * 0.02;
        Vec3 forwardDir = Vec3.directionFromRotation(pitch, yaw).normalize();
        Vec3 motion = forwardDir.scale(speed);

        // 地上ではY成分を無効化（滑走のため）
        if (this.onGround()) {
            motion = new Vec3(motion.x, 0.0, motion.z).scale(0.8); // 摩擦で減速
        }

        this.setDeltaMovement(motion);

        // ▶ モデル見た目更新
        this.pitchO = this.getBodyPitch();
        this.setBodyXRot(pitch);

        super.travel();
    }

    public boolean engineRunning() {
        return !this.onGround();
    }

    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.DRONE_SOUND.get();
    }

    public float getEngineSoundVolume() {
        return (Float)this.entityData.get(POWER) * 2.0F;
    }

    public void move(@NotNull MoverType movementType, @NotNull Vec3 movement) {
        super.move(movementType, movement);
        Player controller = EntityFindUtil.findPlayer(this.level(), (String)this.entityData.get(CONTROLLER));
        if (!(this.lastTickSpeed < 0.2) && this.collisionCoolDown <= 0) {
            if (this.verticalCollision && Mth.abs((float)this.lastTickVerticalSpeed) > 1.0F) {
                this.hurt(ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, (Entity)(controller == null ? this : controller)), (float)((double)20.0F * (double)(Mth.abs((float)this.lastTickVerticalSpeed) - 1.0F) * (this.lastTickSpeed - 0.2) * (this.lastTickSpeed - 0.2)));
                this.collisionCoolDown = 4;
            }

            if (this.horizontalCollision) {
                this.hurt(ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, (Entity)(controller == null ? this : controller)), (float)((double)10.0F * (this.lastTickSpeed - 0.2) * (this.lastTickSpeed - 0.2)));
                this.collisionCoolDown = 4;
            }

        }
    }

    public void destroy() {
        Player controller = EntityFindUtil.findPlayer(this.level(), (String)this.entityData.get(CONTROLLER));
        if (controller != null && controller.getMainHandItem().is((Item)ModItems.MONITOR.get())) {
            Monitor.disLink(controller.getMainHandItem(), controller);
        }

        if (this.level() instanceof ServerLevel) {
            this.level().explode((Entity)null, this.getX(), this.getY(), this.getZ(), 0.0F, Level.ExplosionInteraction.NONE);
        }

        if ((Integer)this.entityData.get(KAMIKAZE_MODE) != 0) {
            this.kamikazeExplosion((Integer)this.entityData.get(KAMIKAZE_MODE));
        }

        if (this.level() instanceof ServerLevel) {
            int count = (Integer)this.entityData.get(AMMO);

            for(int i = 0; i < count; ++i) {
                this.droneDrop(controller);
            }
        }

        String id = (String)this.entityData.get(CONTROLLER);

        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException var5) {
            this.discard();
            return;
        }

        Player player = this.level().getPlayerByUUID(uuid);
        if (player != null) {
            player.getInventory().items.stream().filter((stack) -> stack.getItem() == ModItems.MONITOR.get()).forEach((stack) -> {
                if (stack.getOrCreateTag().getString("LinkedDrone").equals(this.getStringUUID())) {
                    Monitor.disLink(stack, player);
                }

            });
        }

        super.destroy();
    }

    private void kamikazeExplosion(int mode) {
        Entity attacker = EntityFindUtil.findEntity(this.level(), (String)this.entityData.get(LAST_ATTACKER_UUID));
        MortarShellEntity mortarShell = new MortarShellEntity((EntityType) com.atsuishio.superbwarfare.init.ModEntities.MORTAR_SHELL.get(), this.level());
        C4Entity c4 = new C4Entity((EntityType) com.atsuishio.superbwarfare.init.ModEntities.C_4.get(), this.level());
        RpgRocketEntity rpg = new RpgRocketEntity((EntityType) com.atsuishio.superbwarfare.init.ModEntities.RPG_ROCKET.get(), this.level());
        CustomExplosion var10000;
        switch (mode) {
            case 1 -> var10000 = (new CustomExplosion(this.level(), this, ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), mortarShell, attacker), (float)(Integer)ExplosionConfig.DRONE_KAMIKAZE_EXPLOSION_DAMAGE.get(), this.getX(), this.getY(), this.getZ(), (float)(Integer)ExplosionConfig.DRONE_KAMIKAZE_EXPLOSION_RADIUS.get(), (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
            case 2 -> var10000 = (new CustomExplosion(this.level(), this, ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), c4, attacker), (float)(Integer)ExplosionConfig.C4_EXPLOSION_DAMAGE.get(), this.getX(), this.getY(), this.getZ(), (float)(Integer)ExplosionConfig.C4_EXPLOSION_RADIUS.get(), (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
            case 3 -> var10000 = (new CustomExplosion(this.level(), this, ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), rpg, attacker), (float)(Integer)ExplosionConfig.RPG_EXPLOSION_DAMAGE.get(), this.getX(), this.getY(), this.getZ(), (float)(Integer)ExplosionConfig.RPG_EXPLOSION_RADIUS.get(), (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
            default -> var10000 = null;
        }

        CustomExplosion explosion = var10000;
        if (explosion != null) {
            explosion.explode();
            ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            if (mode == 1) {
                ParticleTool.spawnMediumExplosionParticles(this.level(), this.position());
                if (this.currentItem.getItem() instanceof MortarShell) {
                    this.createAreaCloud(PotionUtils.getPotion(this.currentItem), this.level(), (Integer)ExplosionConfig.DRONE_KAMIKAZE_EXPLOSION_DAMAGE.get(), (float)(Integer)ExplosionConfig.DRONE_KAMIKAZE_EXPLOSION_RADIUS.get());
                }
            }

            if (mode == 2 || mode == 3) {
                ParticleTool.spawnHugeExplosionParticles(this.level(), this.position());
            }

        }
    }

    private void createAreaCloud(Potion potion, Level level, int duration, float radius) {
        if (potion != Potions.EMPTY) {
            AreaEffectCloud cloud = new AreaEffectCloud(level, this.getX() + (double)0.75F * this.getDeltaMovement().x, this.getY() + (double)0.5F * (double)this.getBbHeight() + (double)0.75F * this.getDeltaMovement().y, this.getZ() + (double)0.75F * this.getDeltaMovement().z);
            cloud.setPotion(potion);
            cloud.setDuration(duration);
            cloud.setRadius(radius);
            Player controller = EntityFindUtil.findPlayer(this.level(), (String)this.entityData.get(CONTROLLER));
            if (controller != null) {
                cloud.setOwner(controller);
            }

            level.addFreshEntity(cloud);
        }
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public boolean canCrushEntities() {
        return false;
    }

    public float getMaxHealth() {
        return 60.0F; // MQ-9相当のHP
    }

    static {
        LINKED = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.BOOLEAN);
        CONTROLLER = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.STRING);
        KAMIKAZE_MODE = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.INT);
        DELTA_X_ROT = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.FLOAT);
    }
}
