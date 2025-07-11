package Aru.Aru.ashvehicle.entity.weapon;

import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import Aru.Aru.ashvehicle.init.ModEntities;

public class NuclearBombEntity extends ThrowableItemProjectile implements GeoEntity {
    public static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(NuclearBombEntity.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private float explosionDamage = 100.0F;
    private float explosionRadius = 40.0F;

    public NuclearBombEntity(EntityType<? extends NuclearBombEntity> type, Level world) {
        super(type, world);
        this.noCulling = true;
    }

    public NuclearBombEntity(LivingEntity entity, Level level) {
        super(ModEntities.NUCLEAR_BOMB.get(), entity, level);
    }

    public NuclearBombEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.NUCLEAR_BOMB.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEALTH, 50.0F);
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
        if (!this.level().isClientSide && (this.tickCount > 600 || this.entityData.get(HEALTH) <= 0.0F)) {
            explode();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.MEDIUM_AERIAL_BOMB.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.DROWN)) {
            return false;
        }
        this.entityData.set(HEALTH, this.entityData.get(HEALTH) - amount);
        return super.hurt(source, amount);
    }

    private void explode() {
        if (!this.level().isClientSide) {
            level().explode(this, getX(), getY(), getZ(), 6.0F, Level.ExplosionInteraction.BLOCK);
            applyRadiationEffect();
            spawnMushroomCloudParticles();
            startHemisphereExplosions();
            startSurfaceRingExplosions();
            level().playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 10.0F, 0.5F);
        }
        this.discard();
    }

    private void applyRadiationEffect() {
        AABB area = new AABB(this.blockPosition()).inflate(35.0);
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, area)) {
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 30, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 60, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 10));
        }
    }

    private void spawnMushroomCloudParticles() {
        BlockPos pos = this.blockPosition();

        for (int i = 0; i < 20; i++) { // 旧: 50 → 新: 20
            double radius = 3 + i * 0.25; // 半径は緩やかに広げる
            double y = pos.getY() + i * 0.3; // 高さも抑えめに

            for (int angle = 0; angle < 360; angle += 45) { // 旧: 20° → 新: 45°
                double rad = Math.toRadians(angle);
                double x = pos.getX() + 0.5 + radius * Math.cos(rad);
                double z = pos.getZ() + 0.5 + radius * Math.sin(rad);

                level().addParticle(
                        ParticleTypes.LARGE_SMOKE, // 必要なら SMOKE など軽いものに
                        x, y, z,
                        0, 0.05, 0 // わずかに上昇
                );
            }
        }
    }

    private void startHemisphereExplosions() {
        BlockPos center = this.blockPosition();
        int step = 16; // 粗くして軽量化
        int maxRadius = (int) explosionRadius;

        for (int r = step; r <= maxRadius; r += step) {
            int currentRadius = r;
            for (int x = -currentRadius; x <= currentRadius; x += step) {
                for (int y = 0; y <= currentRadius; y += step) {
                    for (int z = -currentRadius; z <= currentRadius; z += step) {
                        double distance = Math.sqrt(x * x + y * y + z * z);
                        if (distance >= currentRadius - step && distance <= currentRadius + 0.5) {
                            BlockPos pos = center.offset(x, -y, z);
                            // 強化された破壊力
                            level().explode(this, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 14.0F, false, Level.ExplosionInteraction.BLOCK);
                        }
                    }
                }
            }
        }
    }

    private void startSurfaceRingExplosions() {
        BlockPos center = this.blockPosition();
        int maxRadius = 80; // 範囲拡大
        int step = 8; // 粗くすることで爆発数を削減

        for (int radius = step; radius <= maxRadius; radius += step) {
            for (int i = 0; i < 360; i += 12) {
                double angle = Math.toRadians(i);
                double dx = radius * Math.cos(angle);
                double dz = radius * Math.sin(angle);
                BlockPos pos = center.offset((int) dx, 0, (int) dz);
                BlockPos ground = level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);

                level().explode(this, ground.getX() + 0.5, ground.getY() + 0.5, ground.getZ() + 0.5, 14.0F, false, Level.ExplosionInteraction.BLOCK);
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Health", this.entityData.get(HEALTH));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Health")) {
            this.entityData.set(HEALTH, tag.getFloat("Health"));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void setExplosionDamage(float damage) {
        this.explosionDamage = damage;
    }

    public void setExplosionRadius(float radius) {
        this.explosionRadius = radius;
    }

    public float getVolume() {
        return 0.7F;
    }

    public SoundEvent getSound() {
        return ModSounds.SHELL_FLY.get();
    }

    public SoundEvent getCloseSound() {
        return SoundEvents.EMPTY;
    }

    protected float getGravity() {
        return 0.06F;
    }
}
