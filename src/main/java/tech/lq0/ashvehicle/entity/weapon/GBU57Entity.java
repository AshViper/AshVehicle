package tech.lq0.ashvehicle.entity.weapon;

import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import tech.lq0.ashvehicle.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GBU57Entity extends FastThrowableProjectile implements GeoEntity, ExplosiveProjectile {
    public static final EntityDataAccessor<Float> HEALTH;
    private final AnimatableInstanceCache cache;
    private float explosionDamage;
    private float explosionRadius;
    private boolean hasImpacted = false;
    private int lastDrilledY = -1;
    private int depthToDrill = 60;

    public GBU57Entity(EntityType<? extends GBU57Entity> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.explosionDamage = (float)(Integer)ExplosionConfig.MK_82_EXPLOSION_DAMAGE.get();
        this.explosionRadius = ((Double)ExplosionConfig.MK_82_EXPLOSION_RADIUS.get()).floatValue();
        this.noCulling = true;
    }

    public GBU57Entity(LivingEntity entity, Level level) {
        super(ModEntities.GBU_57.get(), entity, level);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.explosionDamage = (float)(Integer)ExplosionConfig.MK_82_EXPLOSION_DAMAGE.get();
        this.explosionRadius = ((Double)ExplosionConfig.MK_82_EXPLOSION_RADIUS.get()).floatValue();
    }

    public GBU57Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this((EntityType)ModEntities.GBU_57.get(), level);
    }

    public GBU57Entity(EntityType<? extends ThrowableItemProjectile> pEntityType, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pX, pY, pZ, pLevel);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.explosionDamage = (float)(Integer)ExplosionConfig.MK_82_EXPLOSION_DAMAGE.get();
        this.explosionRadius = ((Double)ExplosionConfig.MK_82_EXPLOSION_RADIUS.get()).floatValue();
        this.noCulling = true;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected Item getDefaultItem() {
        return (Item)ModItems.MEDIUM_AERIAL_BOMB.get();
    }

    public boolean hurt(DamageSource source, float amount) {
        if (!(source.getDirectEntity() instanceof ThrownPotion) && !(source.getDirectEntity() instanceof AreaEffectCloud)) {
            if (source.is(DamageTypes.FALL)) {
                return false;
            } else if (source.is(DamageTypes.CACTUS)) {
                return false;
            } else if (source.is(DamageTypes.DROWN)) {
                return false;
            } else if (source.is(DamageTypes.DRAGON_BREATH)) {
                return false;
            } else if (source.is(DamageTypes.WITHER)) {
                return false;
            } else if (source.is(DamageTypes.WITHER_SKULL)) {
                return false;
            } else if (source.getDirectEntity() instanceof Mk82Entity) {
                return false;
            } else {
                this.entityData.set(HEALTH, (Float)this.entityData.get(HEALTH) - amount);
                return super.hurt(source, amount);
            }
        } else {
            return false;
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEALTH, 50.0F);
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Health")) {
            this.entityData.set(HEALTH, compound.getFloat("Health"));
        }

        if (compound.contains("ExplosionDamage")) {
            this.explosionDamage = compound.getFloat("ExplosionDamage");
        }

        if (compound.contains("Radius")) {
            this.explosionRadius = compound.getFloat("Radius");
        }

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Health", (Float)this.entityData.get(HEALTH));
        compound.putFloat("ExplosionDamage", this.explosionDamage);
        compound.putFloat("Radius", this.explosionRadius);
    }

    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    public void onHitBlock(BlockHitResult hit) {
        if (this.level().isClientSide) return;

        BlockPos pos = hit.getBlockPos();
        BlockState state = level().getBlockState(pos);

        if (state.getDestroySpeed(level(), pos) >= 50.0F) {
            explode();
        } else {
            this.hasImpacted = true;
            this.setDeltaMovement(0, -0.15, 0);
        }
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        if (hasImpacted) {
            double verticalSpeed = Math.abs(this.getDeltaMovement().y);
            int blocksToDrill = Math.max(1, (int)Math.ceil(verticalSpeed * 20)); // speed に応じて 1～数ブロック掘る

            for (int i = 0; i < blocksToDrill; i++) {
                BlockPos center = this.blockPosition().below();

                if (center.getY() <= level().getMinBuildHeight()) {
                    explode();
                    return;
                }

                if (center.getY() != lastDrilledY) {
                    boolean anyBlockDestroyed = false;

                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            BlockPos target = center.offset(dx, 0, dz);
                            BlockState state = level().getBlockState(target);
                            float hardness = state.getDestroySpeed(level(), target);

                            if (!state.isAir() && hardness >= 0 && hardness < 50.0F) {
                                level().destroyBlock(target, false);
                                level().playSound(null, target, SoundEvents.ANVIL_HIT, SoundSource.BLOCKS, 0.5F, 1.0F);
                                level().addParticle(ParticleTypes.SMOKE,
                                        target.getX() + 0.5, target.getY() + 1.0, target.getZ() + 0.5,
                                        0, 0.1, 0);
                                anyBlockDestroyed = true;
                            }
                        }
                    }

                    if (anyBlockDestroyed) {
                        lastDrilledY = center.getY();
                        depthToDrill--;
                        this.setPos(this.getX(), this.getY() - 0.1, this.getZ()); // 少しずつ沈ませる
                        this.setDeltaMovement(0, -Math.max(0.1, verticalSpeed), 0); // スピード維持
                    }

                    if (depthToDrill <= 0) {
                        explode();
                        return;
                    }
                }
            }
        }

        if (this.tickCount > 600 || this.entityData.get(HEALTH) <= 0.0F) {
            explode();
        }
    }

    protected float getGravity() {
        return 0.06F;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public SoundEvent getCloseSound() {
        return SoundEvents.EMPTY;
    }

    public SoundEvent getSound() {
        return (SoundEvent)ModSounds.SHELL_FLY.get();
    }

    public float getVolume() {
        return 0.7F;
    }

    public boolean shouldSyncMotion() {
        return true;
    }

    public void setDamage(float damage) {
    }

    public void setExplosionDamage(float damage) {
        this.explosionDamage = damage;
    }

    public void setExplosionRadius(float radius) {
        this.explosionRadius = radius;
    }

    static {
        HEALTH = SynchedEntityData.defineId(Mk82Entity.class, EntityDataSerializers.FLOAT);
    }

    private void explode() {
        if (!this.level().isClientSide) {
            ProjectileTool.causeCustomExplode(this, this.explosionDamage, this.explosionRadius, 1.2F);
        }
        this.discard();
    }

    private void causeMiniExplosion(BlockPos pos) {
        if (!level().isClientSide) {
            level().explode(this, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    1.0F, Level.ExplosionInteraction.NONE); // 爆風ダメージなし
        }

        // パーティクルとサウンド（任意）
        level().playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5F, 1.2F);
    }
}
