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

        // MASSIVE SCREEN SHAKE - longer duration
        ShakeClientMessage.sendToNearbyPlayers(level, x, y, z, 1000, 80, 50);

        // INITIAL FLASH - blinding white light
        ParticleTool.sendParticle(level, ParticleTypes.FLASH, x, y + 5, z, 800, 20, 20, 20, 1, true);
        ParticleTool.sendParticle(level, ParticleTypes.EXPLOSION, x, y + 10, z, 300, 15, 15, 15, 1, true);

        // FIRE STARS - massive fireball
        ParticleTool.sendParticle(level, ModParticleTypes.FIRE_STAR.get(), x, y + 5, z, 3000, 0, 0, 0, 5, true);

        // SHOCKWAVE - slower, more realistic expanding ring (multiple waves over time)
        for (int wave = 0; wave < 20; wave++) {
            int w = wave;
            Mod.queueServerWork(wave * 2, () -> { // Every 2 ticks = slower expansion
                for (int i = 0; i < 300; i++) {
                    Vec3 v = new Vec3(1, 0, 0).yRot((float) (i * Math.random()));
                    // White/gray dust shockwave
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.9f, 0.9f, 0.85f, 60, 4, 0, false, false), 
                            x, y + 0.5, z, 0, v.x, v.y, v.z, 150 + w * 30, true);
                }
                // Ground dust kicked up by shockwave
                ParticleTool.sendParticle(level, new CustomCloudOption(0.6f, 0.55f, 0.5f, 100, 3, 0, false, false),
                        x, y + 1, z, 50, 5 + w * 3, 0.3, 5 + w * 3, 0.01, true);
            });
        }

        // MUSHROOM CLOUD - extended duration (200 ticks = 10 seconds)
        for (int i = 0; i < 200; i++) {
            int tick = i;
            Mod.queueServerWork(i, () -> {
                
                // Phase 1: Initial fireball rising (0-60 ticks)
                if (tick < 60) {
                    // Bright orange/white fireball core rising
                    float brightness = 1.0f - (tick / 120f);
                    ParticleTool.sendParticle(level, new CustomCloudOption(brightness, brightness * 0.6f, brightness * 0.1f, 200, 10, 0, true, true),
                            x, y + 2 * tick, z, 100, 6 - 0.05 * tick, 2, 6 - 0.05 * tick, 0.015, true);
                    
                    // Dark smoke stem forming
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.35f, 0.3f, 0.25f, 300, 12, 0, true, true),
                            x, y + 1.5 * tick, z, 70, 4 + 0.1 * tick, 1, 4 + 0.1 * tick, 0.01, true);
                }

                // Phase 2: Mushroom cap forming (30-120 ticks)
                if (tick >= 30 && tick < 120) {
                    int k = tick - 30;
                    float capHeight = 80 + k * 0.5f;
                    
                    // Inner bright cap (orange/yellow)
                    if (tick < 80) {
                        ParticleTool.sendParticle(level, new CustomCloudOption(1, 0.7f, 0.2f, 250, 14, 0, true, true),
                                x, y + capHeight, z, 30 + k, 2 + k * 0.15, 3 + k * 0.1, 2 + k * 0.15, 0.006, true);
                    }
                    
                    // Middle cap layer (darker orange)
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.8f, 0.45f, 0.1f, 280, 12, 0, true, true),
                            x, y + capHeight - 5, z, 25 + k, 3 + k * 0.2, 2 + k * 0.08, 3 + k * 0.2, 0.005, true);
                    
                    // Outer dark smoke cap
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.3f, 0.25f, 0.2f, 350, 16, 0, true, true),
                            x, y + capHeight + 5, z, 20 + k, 4 + k * 0.25, 4 + k * 0.12, 4 + k * 0.25, 0.004, true);
                }

                // Phase 3: Stem connecting to cap (20-100 ticks)
                if (tick >= 20 && tick < 100) {
                    // Continuous stem smoke
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.4f, 0.35f, 0.3f, 320, 8, 0, true, true),
                            x, y + 40, z, 40, 5, 20, 5, 0.003, true);
                }

                // Ground smoke ring expanding slowly
                if (tick < 80) {
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.55f, 0.5f, 0.45f, 250, 5, 0, false, false),
                            x, y + 0.5, z, 5 + tick / 2, tick * 1.5, 0.2, tick * 1.5, 0.0003 * tick, true);
                }

                // Secondary explosions in fireball
                if (tick % 8 == 0 && tick < 50) {
                    ParticleTool.sendParticle(level, ParticleTypes.EXPLOSION, x, y + tick * 1.5, z, 40, 10, 6, 10, 1, true);
                    ParticleTool.sendParticle(level, ModParticleTypes.FIRE_STAR.get(), x, y + tick, z, 400, 0, 0, 0, 3.5, true);
                }

                // Phase 4: Lingering smoke and fallout (80-200 ticks)
                if (tick >= 80) {
                    // Ash and debris falling
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.45f, 0.4f, 0.35f, 400, 6, 0, false, false),
                            x, y + 100, z, 30, 40, 30, 40, 0.001, true);
                    
                    // Ground level lingering smoke
                    if (tick % 3 == 0) {
                        ParticleTool.sendParticle(level, new CustomCloudOption(0.5f, 0.45f, 0.4f, 350, 4, 0, false, false),
                                x, y + 2, z, 20, 30, 0.5, 30, 0.002, true);
                    }
                }

                // Persistent mushroom cap (stays visible longer)
                if (tick >= 60 && tick < 180) {
                    // Slowly dissipating cap
                    float fade = 1.0f - ((tick - 60) / 200f);
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.35f * fade, 0.3f * fade, 0.25f * fade, 400, 18, 0, true, true),
                            x, y + 90, z, 15, 25, 15, 25, 0.002, true);
                }
            });
        }

        // Final lingering effects (after main sequence)
        for (int i = 0; i < 100; i++) {
            int tick = i;
            Mod.queueServerWork(200 + i * 2, () -> {
                // Slow dissipating smoke column
                ParticleTool.sendParticle(level, ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 20 + tick, z, 20, 15, 30, 15, 0.005, true);
                
                // Ground fires and embers
                if (tick % 5 == 0) {
                    ParticleTool.sendParticle(level, ParticleTypes.LAVA, x, y + 1, z, 10, 20, 0.5, 20, 0.01, true);
                }
            });
        }
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
