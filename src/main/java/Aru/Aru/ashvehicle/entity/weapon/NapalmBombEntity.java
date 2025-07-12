package Aru.Aru.ashvehicle.entity.weapon;

import Aru.Aru.ashvehicle.init.ModEntities;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class NapalmBombEntity extends FastThrowableProjectile implements GeoEntity {

    public static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(NapalmBombEntity.class, EntityDataSerializers.FLOAT);
    private AnimatableInstanceCache cache;
    private float explosionDamage;
    private float explosionRadius;

    public NapalmBombEntity(EntityType<? extends NapalmBombEntity> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.explosionDamage = ExplosionConfig.MK_82_EXPLOSION_DAMAGE.get().floatValue();
        this.explosionRadius = ExplosionConfig.MK_82_EXPLOSION_RADIUS.get().floatValue();
        this.noCulling = true;
    }

    public NapalmBombEntity(LivingEntity entity, Level level) {
        super(ModEntities.NAPALM_BOMB.get(), entity, level);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.explosionDamage = ExplosionConfig.MK_82_EXPLOSION_DAMAGE.get().floatValue();
        this.explosionRadius = ExplosionConfig.MK_82_EXPLOSION_RADIUS.get().floatValue();
    }

    public NapalmBombEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.NAPALM_BOMB.get(), level);
        this.cache = GeckoLibUtil.createInstanceCache(this);
    }

    public NapalmBombEntity(EntityType<? extends ThrowableItemProjectile> type, double x, double y, double z, Level level) {
        super(type, x, y, z, level);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.explosionDamage = ExplosionConfig.MK_82_EXPLOSION_DAMAGE.get().floatValue();
        this.explosionRadius = ExplosionConfig.MK_82_EXPLOSION_RADIUS.get().floatValue();
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEALTH, 50.0F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.MEDIUM_AERIAL_BOMB.get(); // ナパーム用アイテムに変更可能
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void onHitBlock(BlockHitResult hit) {
        if (!this.level().isClientSide) {
            explode();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (this.tickCount > 600 || this.entityData.get(HEALTH) <= 0.0F) {
                explode();
            }
        }
    }

    private void explode() {
        if (this.level().isClientSide) return;

        BlockPos center = this.blockPosition();

        // 爆発（火は設置するが、ブロック破壊はしない）
        level().explode(this, getX(), getY(), getZ(), 1.5F, Level.ExplosionInteraction.NONE);

        // === 🔥 火をばらまく（中心周囲の3Dランダム範囲） ===
        for (int i = 0; i < 50; i++) {
            int dx = random.nextInt(11) - 5;  // -5〜+5 の範囲
            int dy = random.nextInt(5) - 1;   // -1〜+3（地面付近〜上空）
            int dz = random.nextInt(11) - 5;

            BlockPos firePos = center.offset(dx, dy, dz);

            // 空気であり、下が固体ブロックなら火を置く
            if (level().getBlockState(firePos).isAir() &&
                    level().getBlockState(firePos.below()).isSolid()) {
                level().setBlockAndUpdate(firePos, Blocks.FIRE.defaultBlockState());
            }
        }

        // 煙パーティクル
        for (int i = 0; i < 20; i++) {
            level().addParticle(ParticleTypes.LARGE_SMOKE,
                    getX() + random.nextGaussian() * 0.5,
                    getY() + random.nextDouble() * 0.5,
                    getZ() + random.nextGaussian() * 0.5,
                    0, 0.1, 0);
        }

        // サウンド
        level().playSound(null, center, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0F, 1.0F);

        // AreaEffectCloud（残炎演出）
        AreaEffectCloud cloud = new AreaEffectCloud(level(), getX(), getY(), getZ());
        cloud.setRadius(3.0F);
        cloud.setDuration(80);
        cloud.setParticle(ParticleTypes.FLAME);
        cloud.setFixedColor(0xFF4500);
        cloud.setOwner((LivingEntity) this.getOwner());
        level().addFreshEntity(cloud);

        this.discard();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) ||
                source.is(DamageTypes.DROWN) || source.is(DamageTypes.DRAGON_BREATH) ||
                source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) {
            return false;
        } else {
            this.entityData.set(HEALTH, this.entityData.get(HEALTH) - amount);
            return super.hurt(source, amount);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Health", this.entityData.get(HEALTH));
        tag.putFloat("ExplosionDamage", this.explosionDamage);
        tag.putFloat("ExplosionRadius", this.explosionRadius);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Health")) {
            this.entityData.set(HEALTH, tag.getFloat("Health"));
        }
        if (tag.contains("ExplosionDamage")) {
            this.explosionDamage = tag.getFloat("ExplosionDamage");
        }
        if (tag.contains("ExplosionRadius")) {
            this.explosionRadius = tag.getFloat("ExplosionRadius");
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    protected float getGravity() {
        return 0.06F;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
