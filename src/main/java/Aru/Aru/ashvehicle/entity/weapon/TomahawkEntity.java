package Aru.Aru.ashvehicle.entity.weapon;

import Aru.Aru.ashvehicle.init.ModEntities;
import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TomahawkEntity extends ThrowableProjectile implements GeoAnimatable {
    // --------------------
    // アニメーションキャッシュ
    // --------------------
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // --------------------
    // 放物線飛行用フィールド
    // --------------------
    private Vec3 targetPos;
    private int flightTime;
    private int ticksLived;
    private static final double GRAVITY = 0.08;
    private int explosionDamage = 100;
    private static final TicketType<Entity> MISSILE_TICKET = TicketType.create("tomahawk", (entity1, entity2) -> 0);
    private ChunkPos currentTicketChunk = null;

    // 誘導関連パラメータ
    private static final double MAX_SPEED = 4.5;        // 最高速度（調整可）
    private static final double MAX_ACCELERATION = 0.2; // 毎Tickの最大加速度（推力）

    private static final double HIT_RADIUS = 1.5;       // 着弾判定距離（調整可）

    // --------------------
    // コンストラクター（発射用）
    // --------------------
    public TomahawkEntity(LivingEntity shooter, Level level) {
        super(ModEntities.TOMAHAWK.get(), shooter, level);
    }

    // --------------------
    // コンストラクター（サーバ登録用）
    // --------------------
    public TomahawkEntity(EntityType<? extends TomahawkEntity> type, Level level) {
        super(type, level);
    }

    // --------------------
    // コンストラクター（クライアントスポーン用）
    // --------------------
    public TomahawkEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.TOMAHAWK.get(), level);
    }

    // --------------------
    // 目標座標をセット → 初速＆飛行時間を計算
    // --------------------
    public void setTargetPosition(Vec3 targetPos) {
        this.targetPos = targetPos;
        this.ticksLived = 0;

        // 初速をちょっと上向きにセット（例：X,Zは0、Yを0.5）
        this.setDeltaMovement(new Vec3(0, 5.0, 0));
    }

    @Override
    public void tick() {
        super.tick();
        this.ticksLived++;

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

        if (this.targetPos != null) {
            Vec3 currentPos = this.position();
            Vec3 currentVelocity = this.getDeltaMovement();

            Vec3 toTarget = targetPos.subtract(currentPos);
            double distance = toTarget.length();

            if (distance <= HIT_RADIUS || this.isInWater() || this.onGround()) {
                this.stopChank();
                this.explode();
                this.discard();
                return;
            }

            Vec3 desiredDirection = toTarget.normalize();
            Vec3 desiredVelocity = desiredDirection.scale(MAX_SPEED);

            Vec3 steering = desiredVelocity.subtract(currentVelocity);
            if (steering.length() > MAX_ACCELERATION) {
                steering = steering.normalize().scale(0.07);
            }

            // 重力はずっとかける
            Vec3 gravityAccel = new Vec3(0, -GRAVITY, 0);

            Vec3 newVelocity = currentVelocity.add(steering).add(gravityAccel);

            // 速度倍率で加速表現（例: 1.03倍ずつ加速）
            newVelocity = newVelocity.scale(1.03);

            this.setDeltaMovement(newVelocity);
            this.move(MoverType.SELF, newVelocity);

            // 飛行開始時パーティクル＆音
            if (this.ticksLived == 1 && !this.level().isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), 15, 0.8, 0.8, 0.8, 0.01, true);
                this.level().playSound(null, this.blockPosition(), ModSounds.MISSILE_START.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
            }
        }
    }

    public void onHitBlock(BlockHitResult blockHitResult) {
        if (this.level() instanceof ServerLevel) {
            double x = blockHitResult.getLocation().x;
            double y = blockHitResult.getLocation().y;
            double z = blockHitResult.getLocation().z;
            if ((Boolean) ExplosionConfig.EXPLOSION_DESTROY.get()) {
                float hardness = this.level().getBlockState(BlockPos.containing(x, y, z)).getBlock().defaultDestroyTime();
                if (hardness <= 50.0F && hardness != -1.0F) {
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    Block.dropResources(this.level().getBlockState(blockPos), this.level(), BlockPos.containing(x, y, z), (BlockEntity)null);
                    this.level().destroyBlock(blockPos, true);
                }
            }

            for(int i = 0; i < 5; ++i) {
                this.apExplode(blockHitResult, i);
            }

            this.causeExplode(blockHitResult);
            this.stopChank();
        }

    }

    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (this.level() instanceof ServerLevel && this.tickCount > 8) {
            if (entity == this.getOwner() || this.getOwner() != null && entity == this.getOwner().getVehicle()) {
                return;
            }

            Entity player1 = this.getOwner();
            if (player1 instanceof LivingEntity) {
                LivingEntity living = (LivingEntity)player1;
                if (!living.level().isClientSide() && living instanceof ServerPlayer) {
                    ServerPlayer player = (ServerPlayer)living;
                    living.level().playSound((Player)null, living.blockPosition(), (SoundEvent) ModSounds.INDICATION.get(), SoundSource.VOICE, 1.0F, 1.0F);
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new ClientIndicatorMessage(0, 5));
                }
            }

            entity.hurt(ModDamageTypes.causeCannonFireDamage(this.level().registryAccess(), this, this.getOwner()), 20);
            if (entity instanceof LivingEntity) {
                entity.invulnerableTime = 0;
            }

            for(int i = 0; i < 5; ++i) {
                this.apExplode(result, i);
            }

            this.causeExplode(result);
            this.stopChank();
            this.discard();
        }

    }

    private void apExplode(HitResult result, int index) {
        CustomExplosion explosion = (new CustomExplosion(this.level(), this, ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, this.getOwner()), explosionDamage, result.getLocation().x + (double)index * this.getDeltaMovement().normalize().x, result.getLocation().y + (double)index * this.getDeltaMovement().normalize().y, result.getLocation().z + (double)index * this.getDeltaMovement().normalize().z, 20, (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
        explosion.explode();
        ForgeEventFactory.onExplosionStart(this.level(), explosion);
        explosion.finalizeExplosion(false);
    }

    private void causeExplode(HitResult result) {
        CustomExplosion explosion = (new CustomExplosion(this.level(), this, ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, this.getOwner()), explosionDamage, this.getX(), this.getEyeY(), this.getZ(), 20, (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
        explosion.explode();
        ForgeEventFactory.onExplosionStart(this.level(), explosion);
        explosion.finalizeExplosion(false);
        ParticleTool.spawnHugeExplosionParticles(this.level(), result.getLocation());
    }

    // --------------------
    // 爆発処理
    // --------------------
    private void explode() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                10.0F, true, Level.ExplosionInteraction.BLOCK);
        this.stopChank();
    }

    // --------------------
    // 重力を有効化
    // --------------------
    @Override
    public boolean isNoGravity() {
        return false;
    }

    // --------------------
    // クライアントスポーン同期
    // --------------------
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    // --------------------
    // GeckoLib: キャッシュ取得
    // --------------------
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // (未使用)
    @Override
    public double getTick(Object o) {
        return this.tickCount;
    }

    // --------------------
    // 必要に応じて同期データ定義
    // --------------------
    @Override
    protected void defineSynchedData() {
        // 追加のデータがあればここで定義
    }

    private void stopChank(){
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            if (currentTicketChunk != null) {
                serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, currentTicketChunk, 10, this);
                currentTicketChunk = null; // 忘れずにリセット
            }
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return true; // サーバ再起動後も保持させたい場合
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TomahawkEntity other)) return false;
        return this.getId() == other.getId(); // Entity ID で比較
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.getId()); // Entity ID でハッシュ化
    }
}
