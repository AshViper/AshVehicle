package Aru.Aru.ashvehicle.entity.projectile;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.particle.CustomCloudOption;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.DestroyableProjectile;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModParticleTypes;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.SoundTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class NukeBombEntity extends DestroyableProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public NukeBombEntity(EntityType<? extends NukeBombEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.explosionRadius = 200.0F;
        this.explosionDamage = 15000.0F;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        Entity entity = source.getDirectEntity();
        if (entity instanceof NukeBombEntity nuke && nuke.getOwner() == this.getOwner()) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.MEDIUM_AERIAL_BOMB.get();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult hit) {
        super.onHitBlock(hit);
        if (this.level() instanceof ServerLevel serverLevel) {
            causeNuclearExplosion(serverLevel);
        }
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 600 || this.entityData.get(HEALTH) <= 0.0F) {
            if (this.level() instanceof ServerLevel serverLevel) {
                causeNuclearExplosion(serverLevel);
            }
            this.discard();
        }
    }

    private void causeNuclearExplosion(ServerLevel serverLevel) {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Vec3 pos = this.position();

        // Damage explosion
        new CustomExplosion.Builder(this)
                .damageSource(ModDamageTypes.causeCustomExplosionDamage(serverLevel.registryAccess(), this, this.getOwner()))
                .damage(this.explosionDamage)
                .radius(this.explosionRadius)
                .position(pos)
                .damageMultiplier(2.0F)
                .explode();

        // Block destruction
        if (ExplosionConfig.EXPLOSION_DESTROY.get()) {
            serverLevel.explode(this.getOwner(), x, y, z, Math.min(this.explosionRadius * 0.3f, 60f), Level.ExplosionInteraction.BLOCK);
        }

        // Create crater
        createCrater(serverLevel, BlockPos.containing(x, y, z), 25);

        // Epic nuclear explosion particles
        spawnNuclearExplosionParticles(serverLevel, x, y, z);
    }

    private void createCrater(ServerLevel level, BlockPos center, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius / 2; dy <= radius / 3; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double distance = Math.sqrt(dx * dx + dy * dy * 4 + dz * dz);
                    if (distance <= radius) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        if (!level.getBlockState(pos).isAir() && level.getBlockState(pos).getDestroySpeed(level, pos) >= 0) {
                            // Inner crater - empty
                            if (distance < radius * 0.7) {
                                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                            }
                            // Outer ring - fire and scorched earth
                            else if (dy >= 0 && level.random.nextFloat() < 0.3f) {
                                level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
        }
    }

    private void spawnNuclearExplosionParticles(ServerLevel level, double x, double y, double z) {
        // EPIC SOUNDS - very loud and far
        SoundTool.playDistantSound(level, ModSounds.HUGE_EXPLOSION_CLOSE.get(), new Vec3(x, y, z), 24, 1, null);
        SoundTool.playDistantSound(level, ModSounds.HUGE_EXPLOSION_FAR.get(), new Vec3(x, y, z), 64, 1, null);
        SoundTool.playDistantSound(level, ModSounds.HUGE_EXPLOSION_VERY_FAR.get(), new Vec3(x, y, z), 512, 1, null);

        // MASSIVE SCREEN SHAKE
        ShakeClientMessage.sendToNearbyPlayers(level, x, y, z, 800, 60, 30);

        // INITIAL FLASH - blinding white light
        ParticleTool.sendParticle(level, ParticleTypes.FLASH, x, y + 5, z, 500, 15, 15, 15, 1, true);
        ParticleTool.sendParticle(level, ParticleTypes.EXPLOSION, x, y + 10, z, 200, 12, 12, 12, 1, true);

        // FIRE STARS - massive fireball
        ParticleTool.sendParticle(level, ModParticleTypes.FIRE_STAR.get(), x, y + 5, z, 2000, 0, 0, 0, 4, true);

        // INITIAL SHOCKWAVE RING - white expanding ring
        for (int h = 0; h < 8; h++) {
            for (int i = 0; i < 400; i++) {
                Vec3 v = new Vec3(1, 0, 0).yRot((float) (i * Math.random()));
                ParticleTool.sendParticle(level, new CustomCloudOption(1, 1, 1, 40, 6, 0, false, false), x, y + 1, z,
                        0, v.x, v.y, v.z, 800 - 5 * h, true);
            }
        }

        // MUSHROOM CLOUD - timed sequence
        for (int i = 0; i < 60; i++) {
            int tick = i;
            Mod.queueServerWork(i, () -> {
                // Rising fireball column (stem of mushroom)
                if (tick < 30) {
                    // Orange/red rising fire
                    ParticleTool.sendParticle(level, new CustomCloudOption(1 - ((float) tick / 60), 0.5f - ((float) tick / 120), 0, 150, 8, 0, true, true),
                            x, y + 3 * tick, z, 80, 4 - 0.1 * tick, 1, 4 - 0.1 * tick, 0.01, true);
                    // Dark smoke around stem
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.3f, 0.25f, 0.2f, 200, 10, 0, true, true),
                            x, y + 2 * tick, z, 60, 5 + 0.3 * tick, 0.5, 5 + 0.3 * tick, 0.008, true);
                }

                // Expanding ground smoke ring
                if (tick < 40) {
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.5f, 0.45f, 0.4f, 180, 6, 0, false, false),
                            x, y + 0.5, z, 8 * tick, tick * 2, 0.2, tick * 2, 0.0005 * tick, true);
                }

                // Mushroom cap formation
                if (tick >= 15 && tick < 45) {
                    int k = tick - 15;
                    // Orange/yellow cap
                    ParticleTool.sendParticle(level, new CustomCloudOption(1, 0.6f, 0.1f, 180, 12, 0, true, true),
                            x, y + 60, z, 40 * k, 3 + k, 2 + 0.3 * k, 3 + k, 0.008, true);
                    // Darker outer cap
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.6f, 0.35f, 0.1f, 180, 10, 0, true, true),
                            x, y + 55, z, 30 * k, 4 + k, 2 + 0.2 * k, 4 + k, 0.006, true);
                    // Dark smoke cap
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.25f, 0.2f, 0.15f, 200, 14, 0, true, true),
                            x, y + 65, z, 25 * k, 5 + k, 3 + 0.3 * k, 5 + k, 0.005, true);
                }

                // Secondary explosions and fire
                if (tick % 5 == 0 && tick < 30) {
                    ParticleTool.sendParticle(level, ParticleTypes.EXPLOSION, x, y + tick * 2, z, 30, 8, 4, 8, 1, true);
                    ParticleTool.sendParticle(level, ModParticleTypes.FIRE_STAR.get(), x, y + tick, z, 300, 0, 0, 0, 3, true);
                }

                // Lingering smoke and ash
                if (tick >= 30) {
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.4f, 0.35f, 0.3f, 250, 8, 0, false, false),
                            x, y + 0.3, z, 6 * tick, tick, 0.15, tick, 0.0004 * tick, true);
                }
            });
        }

        // Extra delayed effects for lingering destruction
        Mod.queueServerWork(60, () -> {
            // Final smoke column
            ParticleTool.sendParticle(level, ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 30, z, 300, 10, 40, 10, 0.01, true);
            ParticleTool.sendParticle(level, ParticleTypes.LARGE_SMOKE, x, y + 5, z, 200, 20, 5, 20, 0.02, true);
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @NotNull SoundEvent getSound() {
        return ModSounds.SHELL_FLY.get();
    }

    @Override
    public float getVolume() {
        return 1.0F;
    }

    @Override
    public boolean shouldSyncMotion() {
        return true;
    }
}
