package Aru.Aru.ashvehicle.entity.projectile;

import Aru.Aru.ashvehicle.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BallisticMissileEntity extends Entity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final TicketType<Entity> MISSILE_TICKET =
            TicketType.create("ballistic_missile", (a, b) -> 0);

    private ChunkPos currentTicketChunk;

    private static final EntityDataAccessor<Float> MISSILE_HEALTH =
            SynchedEntityData.defineId(BallisticMissileEntity.class, EntityDataSerializers.FLOAT);

    private float maxHealth = 50f;
    private float health = 50f;

    private Vec3 targetPos;
    private double cruiseY;

    private enum FlightPhase { ASCEND, CRUISE, DIVE }
    private FlightPhase phase = FlightPhase.ASCEND;

    public BallisticMissileEntity(EntityType<? extends BallisticMissileEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public void setTargetPosition(Vec3 targetPos) {
        this.targetPos = targetPos;
        this.cruiseY = this.getY() + 50.0;
        this.phase = FlightPhase.ASCEND;
        this.setDeltaMovement(0, 1.5, 0);
    }

    @Override
    public void tick() {
        this.baseTick();

        this.entityData.set(MISSILE_HEALTH, this.health);

        if (!this.level().isClientSide && this.level() instanceof ServerLevel server) {
            ChunkPos newChunk = new ChunkPos(this.blockPosition());
            if (currentTicketChunk == null || !currentTicketChunk.equals(newChunk)) {
                if (currentTicketChunk != null)
                    server.getChunkSource().removeRegionTicket(MISSILE_TICKET, currentTicketChunk, 10, this);
                server.getChunkSource().addRegionTicket(MISSILE_TICKET, newChunk, 10, this);
                currentTicketChunk = newChunk;
            }
        }

        updateMotion();
        this.move(MoverType.SELF, this.getDeltaMovement());

        if (this.tickCount == 1 && !this.level().isClientSide) {
            ParticleTool.sendParticle((ServerLevel) this.level(), ParticleTypes.CLOUD,
                    this.getX(), this.getY(), this.getZ(),
                    15, 0.8, 0.8, 0.8, 0.01, true);
            this.level().playSound(null, this.blockPosition(),
                    ModSounds.MISSILE_START.get(), SoundSource.PLAYERS, 4f, 1f);
        }
    }

    private void updateMotion() {
        if (targetPos == null) return;

        Vec3 pos = this.position();
        Vec3 v = this.getDeltaMovement();
        if (v.lengthSqr() > 0.001) {
            float yaw = (float)(Math.atan2(v.z, v.x) * (180.0 / Math.PI)) - 90F;
            float pitch = (float)(-(Math.atan2(v.y, Math.sqrt(v.x * v.x + v.z * v.z)) * (180.0 / Math.PI)));

            this.setYRot(yaw);
            this.setXRot(pitch);
            this.yRotO = yaw;
            this.xRotO = pitch;
        }
        switch (phase) {
            case ASCEND -> {
                setDeltaMovement(0, 4.5, 0);
                if (pos.y >= cruiseY) phase = FlightPhase.CRUISE;
            }

            case CRUISE -> {
                Vec3 target = new Vec3(targetPos.x, cruiseY, targetPos.z);
                Vec3 dir = target.subtract(pos);
                double dist = Math.sqrt(dir.x * dir.x + dir.z * dir.z);

                if (dist < 1.0) phase = FlightPhase.DIVE;
                else setDeltaMovement(dir.x / dist * 2.5, 0, dir.z / dist * 2.5);
            }

            case DIVE -> {
                setDeltaMovement(0, -4.0, 0);
                if (pos.y <= targetPos.y || this.onGround()) {
                    explode();
                    discard();
                }
            }
        }
    }

    private void explode() {
        this.level().explode(this, getX(), getY(), getZ(), 10f, true, Level.ExplosionInteraction.BLOCK);
        if (!this.level().isClientSide && currentTicketChunk != null) {
            ((ServerLevel)this.level()).getChunkSource().removeRegionTicket(MISSILE_TICKET, currentTicketChunk, 10, this);
            currentTicketChunk = null;
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(MISSILE_HEALTH, maxHealth);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide) return false;
        this.health -= amount;
        if (this.health <= 0) {
            explode();
            discard();
        }
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object animatable) {
        return this.tickCount;
    }
}
