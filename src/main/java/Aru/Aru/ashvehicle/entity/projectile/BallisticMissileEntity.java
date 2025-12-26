package Aru.Aru.ashvehicle.entity.projectile;

import Aru.Aru.ashvehicle.init.ModEntities;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BallisticMissileEntity extends ThrowableProjectile implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final double GRAVITY = 0.08;
    private static final double HIT_RADIUS = 1.5;

    private Vec3 targetPos;
    private int ticksLived;

    private int explosionDamage = 100;

    private static final TicketType<Entity> MISSILE_TICKET =
            TicketType.create("ballistic_missile", (a, b) -> 0);

    private ChunkPos currentTicketChunk = null;

    private static final EntityDataAccessor<Float> MISSILE_HEALTH =
            SynchedEntityData.defineId(BallisticMissileEntity.class, EntityDataSerializers.FLOAT);

    private float maxHealth = 50.0f;
    private float health = 50.0f;

    public BallisticMissileEntity(LivingEntity shooter, Level level) {
        super(ModEntities.BALLISTIC_MISSILE.get(), shooter, level);
    }

    public BallisticMissileEntity(EntityType<? extends BallisticMissileEntity> type, Level level) {
        super(type, level);
    }

    // ================= 弾道ミサイル初速計算 =================

    public void setTargetPosition(Vec3 targetPos) {
        this.targetPos = targetPos;
        this.ticksLived = 0;

        Vec3 start = this.position();
        Vec3 diff = targetPos.subtract(start);

        double dx = diff.x;
        double dz = diff.z;
        double dy = diff.y;

        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        // 弾速（小さいほど高く山なりになる）
        double flightTime = horizontalDist / 3.5;

        double vx = dx / flightTime;
        double vz = dz / flightTime;
        double vy = (dy + 0.5 * GRAVITY * flightTime * flightTime) / flightTime;

        this.setDeltaMovement(vx, vy, vz);
    }

    // ================= ダメージ処理 =================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide) return false;
        if (this.isInvulnerableTo(source)) return false;

        this.health -= amount;

        ((ServerLevel) this.level()).sendParticles(
                ParticleTypes.CRIT,
                this.getX(), this.getY(), this.getZ(),
                5, 0.2, 0.2, 0.2, 0.01
        );

        if (this.health <= 0) {
            this.stopChunk();
            this.explode();
            this.discard();
        }
        return true;
    }

    // ================= 毎Tick処理（弾道物理） =================

    @Override
    public void tick() {
        super.tick();
        this.ticksLived++;
        this.entityData.set(MISSILE_HEALTH, this.health);

        // チャンクロード維持
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            ChunkPos newChunk = new ChunkPos(this.blockPosition());
            if (currentTicketChunk == null || !newChunk.equals(currentTicketChunk)) {
                if (currentTicketChunk != null) {
                    serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, currentTicketChunk, 10, this);
                }
                serverLevel.getChunkSource().addRegionTicket(MISSILE_TICKET, newChunk, 10, this);
                currentTicketChunk = newChunk;
            }
        }

        // 重力適用
        Vec3 vel = this.getDeltaMovement();
        this.setDeltaMovement(vel.x, vel.y - GRAVITY, vel.z);
        this.move(MoverType.SELF, this.getDeltaMovement());

        // 着弾判定
        if (this.targetPos != null) {
            if (this.position().distanceTo(this.targetPos) <= HIT_RADIUS || this.onGround() || this.isInWater()) {
                this.stopChunk();
                this.explode();
                this.discard();
                return;
            }
        }

        // 発射エフェクト
        if (this.ticksLived == 1 && !this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD,
                    this.getX(), this.getY(), this.getZ(),
                    15, 0.8, 0.8, 0.8, 0.01, true);
            this.level().playSound(null, this.blockPosition(),
                    ModSounds.MISSILE_START.get(),
                    SoundSource.PLAYERS, 4.0F, 1.0F);
        }
    }

    // ================= 爆発 =================

    private void explode() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                10.0F, true, Level.ExplosionInteraction.BLOCK);
        this.stopChunk();
    }

    // ================= その他 =================

    @Override
    protected void defineSynchedData() {
        this.entityData.define(MISSILE_HEALTH, maxHealth);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object o) {
        return this.tickCount;
    }


    private void stopChunk() {
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            if (currentTicketChunk != null) {
                serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, currentTicketChunk, 10, this);
                currentTicketChunk = null;
            }
        }
    }
}
