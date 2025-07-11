package Aru.Aru.ashvehicle.entity.weapon;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.ExplosiveProjectile;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class JassmXREntity  extends FastThrowableProjectile implements GeoEntity, ExplosiveProjectile {
    private final AnimatableInstanceCache cache;
    private float damage;
    private float explosionDamage;
    private float explosionRadius;

    public JassmXREntity(EntityType<? extends JassmXREntity> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.damage = 140.0F;
        this.explosionDamage = 60.0F;
        this.explosionRadius = 5.0F;
        this.noCulling = true;
    }

    public JassmXREntity(EntityType<? extends ThrowableItemProjectile> pEntityType, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pX, pY, pZ, pLevel);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.damage = 140.0F;
        this.explosionDamage = 60.0F;
        this.explosionRadius = 5.0F;
        this.noCulling = true;
        this.durability = 20;
    }

    public JassmXREntity(LivingEntity entity, Level level, float damage, float explosionDamage, float explosionRadius) {
        super(Aru.Aru.ashvehicle.init.ModEntities.JASSM_XR.get(), entity, level);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.damage = 140.0F;
        this.explosionDamage = 60.0F;
        this.explosionRadius = 5.0F;
        this.damage = damage;
        this.explosionDamage = explosionDamage;
        this.explosionRadius = explosionRadius;
        this.durability = 20;
    }

    public JassmXREntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(Aru.Aru.ashvehicle.init.ModEntities.JASSM_XR.get(), level);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Damage", this.damage);
        pCompound.putFloat("ExplosionDamage", this.explosionDamage);
        pCompound.putFloat("Radius", this.explosionRadius);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Damage")) {
            this.damage = pCompound.getFloat("Damage");
        }

        if (pCompound.contains("ExplosionDamage")) {
            this.explosionDamage = pCompound.getFloat("ExplosionDamage");
        }

        if (pCompound.contains("Radius")) {
            this.explosionRadius = pCompound.getFloat("Radius");
        }

    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected Item getDefaultItem() {
        return (Item) ModItems.ROCKET.get();
    }

    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (this.getOwner() == null || this.getOwner().getVehicle() == null || entity != this.getOwner().getVehicle()) {
            if (this.level() instanceof ServerLevel) {
                if (entity == this.getOwner() || this.getOwner() != null && entity == this.getOwner().getVehicle()) {
                    return;
                }

                Entity player = this.getOwner();
                if (player instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity)player;
                    if (!living.level().isClientSide() && living instanceof ServerPlayer) {
                        ServerPlayer player1 = (ServerPlayer)living;
                        living.level().playSound((Player)null, living.blockPosition(), (SoundEvent) ModSounds.INDICATION.get(), SoundSource.VOICE, 1.0F, 1.0F);
                        Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player1), new ClientIndicatorMessage(0, 5));
                    }
                }

                entity.hurt(ModDamageTypes.causeCannonFireDamage(this.level().registryAccess(), this, this.getOwner()), this.damage);
                if (entity instanceof LivingEntity) {
                    entity.invulnerableTime = 0;
                }

                this.causeExplode(result.getLocation());
                this.discard();
            }

        }
    }

    public void onHitBlock(BlockHitResult blockHitResult) {
        if (this.level() instanceof ServerLevel) {
            BlockPos resultPos = blockHitResult.getBlockPos();
            float hardness = this.level().getBlockState(resultPos).getBlock().defaultDestroyTime();
            if (hardness != -1.0F && (Boolean) ExplosionConfig.EXPLOSION_DESTROY.get()) {
                if (this.firstHit) {
                    this.causeExplode(blockHitResult.getLocation());
                    this.firstHit = false;
                    Mod.queueServerWork(3, this::discard);
                }

                this.level().destroyBlock(resultPos, true);
            }

            if (!(Boolean)ExplosionConfig.EXPLOSION_DESTROY.get()) {
                this.causeExplode(blockHitResult.getLocation());
                this.discard();
            }
        }

    }

    private void causeExplode(Vec3 vec3) {
        CustomExplosion explosion = (new CustomExplosion(this.level(), this, ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, this.getOwner()), this.explosionDamage, vec3.x, vec3.y, vec3.z, this.explosionRadius, (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(1.0F);
        explosion.explode();
        ForgeEventFactory.onExplosionStart(this.level(), explosion);
        explosion.finalizeExplosion(false);
        ParticleTool.spawnMediumExplosionParticles(this.level(), vec3);
    }

    public void tick() {
        super.tick();
        if (this.tickCount == 3 && !this.level().isClientSide()) {
            Level var2 = this.level();
            if (var2 instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)var2;
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, this.xo, this.yo, this.zo, 15, 0.8, 0.8, 0.8, 0.01, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, this.xo, this.yo, this.zo, 10, 0.8, 0.8, 0.8, 0.01, true);
            }
        }

        if (this.tickCount > 2 && !this.level().isClientSide()) {
            Level var4 = this.level();
            if (var4 instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)var4;
                ParticleTool.sendParticle(serverLevel, ParticleTypes.SMOKE, this.xo, this.yo, this.zo, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, true);
            }
        }

        if (this.tickCount > 100 || this.isInWater()) {
            if (this.level() instanceof ServerLevel) {
                causeRocketExplode(this, ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, this.getOwner()), this, this.explosionDamage, this.explosionRadius, 1.0F);
            }

            this.discard();
        }

        this.destroyBlock();
    }

    public static void causeRocketExplode(ThrowableItemProjectile projectile, @Nullable DamageSource source, Entity target, float damage, float radius, float damageMultiplier) {
        CustomExplosion explosion = (new CustomExplosion(projectile.level(), projectile, source, damage, projectile.getX(), projectile.getY(), projectile.getZ(), radius, (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true)).setDamageMultiplier(damageMultiplier);
        explosion.explode();
        ForgeEventFactory.onExplosionStart(projectile.level(), explosion);
        explosion.finalizeExplosion(false);
        ParticleTool.spawnMediumExplosionParticles(projectile.level(), projectile.position());
        projectile.discard();
    }

    private PlayState movementPredicate(AnimationState<JassmXREntity> event) {
        return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("open"));
    }

    protected float getGravity() {
        return 0.0F;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController[]{new AnimationController(this, "movement", 0, this::movementPredicate)});
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public @NotNull SoundEvent getCloseSound() {
        return (SoundEvent)ModSounds.ROCKET_ENGINE.get();
    }

    public @NotNull SoundEvent getSound() {
        return (SoundEvent)ModSounds.ROCKET_FLY.get();
    }

    public float getVolume() {
        return 0.1F;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setExplosionDamage(float damage) {
        this.explosionDamage = damage;
    }

    public void setExplosionRadius(float radius) {
        this.explosionRadius = radius;
    }
}
