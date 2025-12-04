package Aru.Aru.ashvehicle.entity.weapon;

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundSource;
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
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BallisticMissileEntity extends ThrowableProjectile implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private Vec3 targetPos;
    private int flightTime;
    private int ticksLived;
    private static final double GRAVITY = 0.08;
    private int explosionDamage = 100;
    private static final TicketType<Entity> MISSILE_TICKET = TicketType.create("ballistic_missile", (entity1, entity2) -> 0);
    private ChunkPos currentTicketChunk = null;

    private static final double MAX_SPEED = 4.5;
    private static final double MAX_ACCELERATION = 0.2;
    private static final double HIT_RADIUS = 1.5;

    public BallisticMissileEntity(LivingEntity shooter, Level level) {
        super(ModEntities.BALLISTIC_MISSILE.get(), shooter, level);
    }

    public BallisticMissileEntity(EntityType<? extends BallisticMissileEntity> type, Level level) {
        super(type, level);
    }

    public BallisticMissileEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.BALLISTIC_MISSILE.get(), level);
    }

    public void setTargetPosition(Vec3 targetPos) {
        this.targetPos = targetPos;
        this.ticksLived = 0;
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
                this.stopChunk();
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

            Vec3 gravityAccel = new Vec3(0, -GRAVITY, 0);
            Vec3 newVelocity = currentVelocity.add(steering).add(gravityAccel);
            newVelocity = newVelocity.scale(1.03);

            this.setDeltaMovement(newVelocity);
            this.move(MoverType.SELF, newVelocity);

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
            this.stopChunk();
        }
    }

    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (this.level() instanceof ServerLevel && this.tickCount > 8) {
            if (entity == this.getOwner() || this.getOwner() != null && entity == this.getOwner().getVehicle()) {
                return;
            }

            entity.hurt(ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, this.getOwner()), 20);
            if (entity instanceof LivingEntity) {
                entity.invulnerableTime = 0;
            }

            for(int i = 0; i < 5; ++i) {
                this.apExplode(result, i);
            }

            this.causeExplode(result);
            this.stopChunk();
            this.discard();
        }
    }

    private void apExplode(HitResult result, int index) {
        CustomExplosion explosion = new CustomExplosion(this.level(), this, 
                ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, this.getOwner()), 
                explosionDamage, 
                result.getLocation().x + (double)index * this.getDeltaMovement().normalize().x, 
                result.getLocation().y + (double)index * this.getDeltaMovement().normalize().y, 
                result.getLocation().z + (double)index * this.getDeltaMovement().normalize().z, 
                20, 
                (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP);
        explosion.setDamageMultiplier(1.0F);
        explosion.explode();
        ForgeEventFactory.onExplosionStart(this.level(), explosion);
        explosion.finalizeExplosion(false);
    }

    private void causeExplode(HitResult result) {
        CustomExplosion explosion = new CustomExplosion(this.level(), this, 
                ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, this.getOwner()), 
                explosionDamage, 
                this.getX(), this.getEyeY(), this.getZ(), 
                20, 
                (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP);
        explosion.setDamageMultiplier(1.0F);
        explosion.explode();
        ForgeEventFactory.onExplosionStart(this.level(), explosion);
        explosion.finalizeExplosion(false);
        ParticleTool.spawnHugeExplosionParticles(this.level(), result.getLocation());
    }

    private void explode() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                10.0F, true, Level.ExplosionInteraction.BLOCK);
        this.stopChunk();
    }

    @Override
    public boolean isNoGravity() {
        return false;
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

    @Override
    protected void defineSynchedData() {
    }

    private void stopChunk() {
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            if (currentTicketChunk != null) {
                serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, currentTicketChunk, 10, this);
                currentTicketChunk = null;
            }
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BallisticMissileEntity other)) return false;
        return this.getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.getId());
    }
}
