package tech.lq0.ashvehicle.entity.weapon;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.FlareDecoyEntity;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.ProjectileTool;
import com.atsuishio.superbwarfare.tools.SeekTool;
import com.atsuishio.superbwarfare.tools.VectorTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
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
import org.joml.Math;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.lq0.ashvehicle.init.ModEntities;

public class Aam4Entity extends FastThrowableProjectile implements GeoEntity {
    public static final EntityDataAccessor<Float> HEALTH;
    public static final EntityDataAccessor<String> TARGET_UUID;
    private final AnimatableInstanceCache cache;
    private float damage;
    private float explosionDamage;
    private float explosionRadius;
    private boolean distracted;
    private int durability;
    private boolean firstHit;

    public Aam4Entity(EntityType<? extends Aam4Entity> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.damage = (float)(Integer)ExplosionConfig.AGM_65_DAMAGE.get();
        this.explosionDamage = (float)(Integer)ExplosionConfig.AGM_65_EXPLOSION_DAMAGE.get();
        this.explosionRadius = ((Double)ExplosionConfig.AGM_65_EXPLOSION_RADIUS.get()).floatValue();
        this.distracted = false;
        this.durability = 40;
        this.firstHit = true;
        this.noCulling = true;
    }

    public Aam4Entity(LivingEntity entity, Level level) {
        super(ModEntities.AAM_4.get(), entity, level);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.damage = (float)(Integer)ExplosionConfig.AGM_65_DAMAGE.get();
        this.explosionDamage = (float)(Integer)ExplosionConfig.AGM_65_EXPLOSION_DAMAGE.get();
        this.explosionRadius = ((Double)ExplosionConfig.AGM_65_EXPLOSION_RADIUS.get()).floatValue();
        this.distracted = false;
        this.durability = 40;
        this.firstHit = true;
    }

    public void setTargetUuid(String uuid) {
        this.entityData.set(TARGET_UUID, uuid);
    }

    public Aam4Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.AAM_4.get(), level);
    }

    protected Item getDefaultItem() {
        return (Item)ModItems.AGM.get();
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
        this.entityData.define(HEALTH, 30.0F);
        this.entityData.define(TARGET_UUID, "none");
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Health")) {
            this.entityData.set(HEALTH, compound.getFloat("Health"));
        }

        if (compound.contains("Damage")) {
            this.damage = compound.getFloat("Damage");
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
        compound.putFloat("Damage", this.damage);
        compound.putFloat("ExplosionDamage", this.explosionDamage);
        compound.putFloat("Radius", this.explosionRadius);
    }

    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
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

            entity.hurt(ModDamageTypes.causeCannonFireDamage(this.level().registryAccess(), this, this.getOwner()), this.damage);
            if (entity instanceof LivingEntity) {
                entity.invulnerableTime = 0;
            }

            for(int i = 0; i < 5; ++i) {
                this.apExplode(result, i);
            }

            this.causeExplode(result);
            this.discard();
        }

    }

    public boolean isNoGravity() {
        return true;
    }

    public void onHitBlock(BlockHitResult blockHitResult) {
        if (this.level() instanceof ServerLevel) {
            double x = blockHitResult.getLocation().x;
            double y = blockHitResult.getLocation().y;
            double z = blockHitResult.getLocation().z;
            if ((Boolean)ExplosionConfig.EXPLOSION_DESTROY.get()) {
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
        }

    }

    private void apExplode(HitResult result, int index) {
        CustomExplosion explosion = (new CustomExplosion(this.level(), this, ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, this.getOwner()), this.explosionDamage, result.getLocation().x + (double)index * this.getDeltaMovement().normalize().x, result.getLocation().y + (double)index * this.getDeltaMovement().normalize().y, result.getLocation().z + (double)index * this.getDeltaMovement().normalize().z, 0.5F * this.explosionRadius, (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
        explosion.explode();
        ForgeEventFactory.onExplosionStart(this.level(), explosion);
        explosion.finalizeExplosion(false);
    }

    public void tick() {
        super.tick();
        Entity entity = EntityFindUtil.findEntity(this.level(), this.entityData.get(TARGET_UUID));

        for (Entity e : SeekTool.seekLivingEntities(this, this.level(), 32.0F, 90.0F)) {
            if (e.getType().is(ModTags.EntityTypes.DECOY) && !this.distracted) {
                this.entityData.set(TARGET_UUID, e.getStringUUID());
                this.distracted = true;
                break;
            }
        }

        if (!"none".equals(this.entityData.get(TARGET_UUID)) && entity != null && entity.level() instanceof ServerLevel) {
            if ((!entity.getPassengers().isEmpty() || entity instanceof VehicleEntity) &&
                    entity.tickCount % (int) Math.max(0.04 * this.distanceTo(entity), 2.0F) == 0) {
                entity.level().playSound(null, entity.getOnPos(),
                        entity instanceof Pig ? SoundEvents.PIG_HURT : ModSounds.MISSILE_WARNING.get(),
                        SoundSource.PLAYERS, 2.0F, 1.0F);
            }

            Vec3 targetPos = new Vec3(
                    entity.getX(),
                    entity.getY() + (entity instanceof EnderDragon ? -2 : 0) + 0.1 * this.distanceTo(entity),
                    entity.getZ()
            );
            Vec3 toVec = this.getEyePosition().vectorTo(targetPos).normalize();
            if (this.tickCount > 8) {
                boolean lostTarget = VectorTool.calculateAngle(this.getDeltaMovement(), toVec) > 120.0F;
                if (!lostTarget) {
                    Vec3 velocity = this.getDeltaMovement()
                            .add(toVec.scale(6.0))
                            .scale(0.5)
                            .add(entity.getDeltaMovement().scale(0.3));
                    this.setDeltaMovement(velocity);
                }
            }
        }

        if (this.tickCount == 8) {
            this.level().playSound(null, BlockPos.containing(this.position()), ModSounds.MISSILE_START.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
            if (!this.level().isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, this.xo, this.yo, this.zo, 15, 0.8, 0.8, 0.8, 0.01, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, this.xo, this.yo, this.zo, 10, 0.8, 0.8, 0.8, 0.01, true);
            }
        }

        if (this.tickCount > 8) {
            if (!this.level().isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, this.xo, this.yo, this.zo, 1, 0.0F, 0.0F, 0.0F, 0.0F, true);
            }
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.06, 1.06, 1.06));
        }

        if (this.tickCount > 200 || this.isInWater() || this.entityData.get(HEALTH) <= 0.0F) {
            if (this.level() instanceof ServerLevel) {
                ProjectileTool.causeCustomExplode(this, ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, this.getOwner()), this, this.explosionDamage, this.explosionRadius, 1.0F);
            }
            this.discard();
        }

        float f = (float) Mth.clamp(1.0F - 0.005 * this.getDeltaMovement().length(), 0.001, 1.0F);
        this.setDeltaMovement(this.getDeltaMovement().multiply(f, f, f));
    }

    private void causeExplode(HitResult result) {
        CustomExplosion explosion = (new CustomExplosion(this.level(), this, ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, this.getOwner()), this.explosionDamage, this.getX(), this.getEyeY(), this.getZ(), this.explosionRadius, (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
        explosion.explode();
        ForgeEventFactory.onExplosionStart(this.level(), explosion);
        explosion.finalizeExplosion(false);
        ParticleTool.spawnHugeExplosionParticles(this.level(), result.getLocation());
    }

    private PlayState movementPredicate(AnimationState<Aam4Entity> event) {
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.jvm.idle"));
    }

    protected float getGravity() {
        return this.tickCount > 8 ? 0.0F : 0.15F;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public boolean shouldSyncMotion() {
        return true;
    }

    public SoundEvent getCloseSound() {
        return (SoundEvent)ModSounds.ROCKET_ENGINE.get();
    }

    public SoundEvent getSound() {
        return (SoundEvent)ModSounds.ROCKET_FLY.get();
    }

    public float getVolume() {
        return 0.7F;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setExplosionDamage(float explosionDamage) {
        this.explosionDamage = explosionDamage;
    }

    public void setExplosionRadius(float radius) {
        this.explosionRadius = radius;
    }

    static {
        HEALTH = SynchedEntityData.defineId(Aam4Entity.class, EntityDataSerializers.FLOAT);
        TARGET_UUID = SynchedEntityData.defineId(Aam4Entity.class, EntityDataSerializers.STRING);
    }
}
