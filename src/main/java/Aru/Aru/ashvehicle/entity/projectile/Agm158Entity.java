package Aru.Aru.ashvehicle.entity.projectile;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.MissileProjectile;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags;
import com.atsuishio.superbwarfare.network.NetworkRegistry;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.tools.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Agm158Entity extends MissileProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private enum Phase { DROP, CRUISE, HOMING }
    private Phase phase = Phase.DROP;

    private int dropTicks = 20; // 0.5秒

    public Agm158Entity(EntityType<? extends Agm158Entity> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.damage = 1100.0F;
        this.explosionDamage = 180.0F;
        this.explosionRadius = 12.0F;
        this.distracted = false;
        this.durability = 25;
    }

    protected @NotNull Item getDefaultItem() {
        return (Item)ModItems.LARGE_ANTI_GROUND_MISSILE.get();
    }

    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (entity != this.getOwner() && (this.getOwner() == null || entity != this.getOwner().getVehicle())) {
            if (this.level() instanceof ServerLevel) {
                Entity player = this.getOwner();
                if (player instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity)player;
                    if (!living.level().isClientSide() && living instanceof ServerPlayer) {
                        ServerPlayer player1 = (ServerPlayer)living;
                        living.level().playSound((Player)null, living.blockPosition(), (SoundEvent)ModSounds.INDICATION.get(), SoundSource.VOICE, 1.0F, 1.0F);
                        NetworkRegistry.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player1), new ClientIndicatorMessage(0, 5));
                    }
                }

                DamageHandler.doDamage(entity, ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()), this.damage);
                if (entity instanceof LivingEntity) {
                    entity.invulnerableTime = 0;
                }

                this.causeExplode(result.getLocation());
                this.discard();
            }

        }
    }

    public void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level() instanceof ServerLevel) {
            BlockPos resultPos = blockHitResult.getBlockPos();
            float hardness = this.level().getBlockState(resultPos).getBlock().defaultDestroyTime();
            if (hardness != -1.0F) {
                if ((Boolean)ExplosionConfig.EXPLOSION_DESTROY.get()) {
                    if (this.firstHit) {
                        this.causeExplode(blockHitResult.getLocation());
                        this.firstHit = false;
                        Mod.queueServerWork(3, this::discard);
                    }

                    if ((Boolean)ExplosionConfig.EXTRA_EXPLOSION_EFFECT.get()) {
                        this.level().destroyBlock(resultPos, true);
                    }
                }
            } else {
                this.causeExplode(blockHitResult.getLocation());
                this.discard();
            }

            if (!(Boolean)ExplosionConfig.EXPLOSION_DESTROY.get()) {
                this.causeExplode(blockHitResult.getLocation());
                this.discard();
            }
        }

    }

    public void tick() {
        super.tick();
        this.mediumTrail();
        Entity entity = EntityFindUtil.findEntity(this.level(), (String)this.entityData.get(TARGET_UUID));

        // デコイ処理（元コードそのまま）
        for(Entity e : SeekTool.seekLivingEntities(this, 32.0F, 90.0F)) {
            if (e.getType().is(ModTags.EntityTypes.DECOY) && !this.distracted) {
                this.entityData.set(TARGET_UUID, e.getStringUUID());
                this.distracted = true;
                break;
            }
        }

        //===========================
        //     ◆ フェーズ制御 ◆
        //===========================
        if (entity != null && !this.entityData.get(TARGET_UUID).equals("none")) {

            double dist = this.distanceTo(entity);

            switch (phase) {

                // ----------------------------------
                // ① 0.5秒だけ落下
                // ----------------------------------
                case DROP -> {
                    this.setDeltaMovement(0, -1.1, 0); // 落下速度

                    dropTicks--;
                    if (dropTicks <= 0) {
                        phase = Phase.CRUISE;
                    }
                }

                // ----------------------------------
                // ② 水平巡航
                // ----------------------------------
                case CRUISE -> {

                    // 50m以内なら誘導フェーズへ
                    if (dist < 200) {
                        phase = Phase.HOMING;
                        break;
                    }

                    // ターゲット方向へ水平移動（高度固定）
                    Vec3 horizontalTarget = new Vec3(
                            entity.getX(),
                            this.getY(),     // 高度固定＝水平飛行
                            entity.getZ()
                    );

                    Vec3 toVec = horizontalTarget.subtract(this.position()).normalize();

                    this.turn(toVec, 6.0F);             // 緩やかな旋回
                    this.setDeltaMovement(
                            this.getDeltaMovement().scale(0.05)
                                    .add(this.getLookAngle().scale(8.0F))
                    ); // 巡航速度
                }

                // ----------------------------------
                // ③ 元の誘導方式（HOMING）
                // ----------------------------------
                case HOMING -> {

                    if ((!entity.getPassengers().isEmpty() || entity instanceof VehicleEntity)
                            && entity.tickCount % (int)Math.max(0.04 * dist, 2.0F) == 0) {
                        entity.level().playSound(null, entity.getOnPos(),
                                entity instanceof Pig ? SoundEvents.PIG_HURT : ModSounds.MISSILE_WARNING.get(),
                                SoundSource.PLAYERS, 2.0F, 1.0F);
                    }

                    Vec3 targetPos = new Vec3(
                            entity.getX(),
                            entity.getY() + (0.5F * entity.getBbHeight()) + (entity instanceof EnderDragon ? -3 : 0),
                            entity.getZ()
                    );

                    Vec3 toVec = RangeTool.calculateFiringSolution(
                            this.position(),
                            targetPos,
                            entity.getDeltaMovement(),
                            this.getDeltaMovement().length(),
                            0.0F
                    );

                    if (this.tickCount > 1) {
                        this.lostTarget =
                                VectorTool.calculateAngle(this.getDeltaMovement(), toVec) > 120.0 && !this.lostTarget;

                        if (!this.lostTarget) {
                            this.turn(toVec, Mth.clamp((float)(this.tickCount - 1) * 0.5F, 0.0F, 15.0F));
                            this.setDeltaMovement(
                                    this.getDeltaMovement().scale(0.05)
                                            .add(this.getLookAngle().scale(8.0F))
                            );
                        }

                        if (this.lostTarget) {
                            this.entityData.set(TARGET_UUID, "none");
                        }
                    }
                }
            }
        }

        // 元の寿命・水没処理
        if (this.tickCount > 200 || this.isInWater()) {
            if (this.level() instanceof ServerLevel) {
                ProjectileTool.causeCustomExplode(
                        this,
                        ModDamageTypes.causeProjectileExplosionDamage(this.level().registryAccess(), this, this.getOwner()),
                        this,
                        this.explosionDamage,
                        this.explosionRadius
                );
            }

            this.discard();
        }

        this.destroyBlock();
    }

    public float getGravity() {
        return this.tickCount < 8 ? 0.15F : super.getGravity();
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {}

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public @NotNull SoundEvent getSound() {
        return (SoundEvent)ModSounds.ROCKET_FLY.get();
    }

    public float getVolume() {
        return 0.7F;
    }

    public float getMaxHealth() {
        return 70.0F;
    }
}
