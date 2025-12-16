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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
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
        BlockPos center = BlockPos.containing(x, y, z);

        // Damage explosion
        new CustomExplosion.Builder(this)
                .damageSource(ModDamageTypes.causeCustomExplosionDamage(serverLevel.registryAccess(), this, this.getOwner()))
                .damage(this.explosionDamage)
                .radius(this.explosionRadius)
                .position(pos)
                .damageMultiplier(2.0F)
                .explode();

        // Block destruction - spread over time to prevent lag
        if (ExplosionConfig.EXPLOSION_DESTROY.get()) {
            // Main central explosion (smaller, instant)
            serverLevel.explode(this.getOwner(), x, y, z, 30f, Level.ExplosionInteraction.BLOCK);
            
            // Secondary explosions delayed
            Mod.queueServerWork(2, () -> serverLevel.explode(this.getOwner(), x + 25, y, z, 20f, Level.ExplosionInteraction.BLOCK));
            Mod.queueServerWork(3, () -> serverLevel.explode(this.getOwner(), x - 25, y, z, 20f, Level.ExplosionInteraction.BLOCK));
            Mod.queueServerWork(4, () -> serverLevel.explode(this.getOwner(), x, y, z + 25, 20f, Level.ExplosionInteraction.BLOCK));
            Mod.queueServerWork(5, () -> serverLevel.explode(this.getOwner(), x, y, z - 25, 20f, Level.ExplosionInteraction.BLOCK));
        }

        // Create crater - spread over multiple ticks
        createCrater(serverLevel, center, 50);

        // Shockwave block destruction - fewer waves, more spread out
        if (ExplosionConfig.EXPLOSION_DESTROY.get()) {
            for (int wave = 0; wave < 5; wave++) {
                int w = wave;
                Mod.queueServerWork(40 + wave * 10, () -> { // Start after crater, more delay between waves
                    destroyBlocksInRing(serverLevel, center, 50 + w * 25, 70 + w * 25);
                });
            }
        }

        // Epic nuclear explosion particles
        spawnNuclearExplosionParticles(serverLevel, x, y, z);

        // Apply radiation to nearby entities (long lasting effects)
        applyRadiation(serverLevel, x, y, z, 150); // 150 block radius for radiation
    }

    private void applyRadiation(ServerLevel level, double x, double y, double z, int radius) {
        // Initial radiation burst
        applyRadiationEffects(level, x, y, z, radius, 1200, 3); // 60 sec, level 3

        // Lingering radiation over time (30 seconds of pulses)
        for (int i = 0; i < 30; i++) {
            int tick = i;
            Mod.queueServerWork(i * 20, () -> { // Every second
                // Radiation zone shrinks over time, intensity decreases
                int currentRadius = radius - tick * 3;
                int intensity = Math.max(0, 2 - tick / 10);
                if (currentRadius > 30) {
                    applyRadiationEffects(level, x, y, z, currentRadius, 400, intensity);
                    
                    // Green radiation particles
                    if (tick % 2 == 0) {
                        ParticleTool.sendParticle(level, ParticleTypes.HAPPY_VILLAGER, x, y + 2, z, 
                                50, currentRadius * 0.7, 3, currentRadius * 0.7, 0.01, true);
                        // Spore particles for fallout effect
                        ParticleTool.sendParticle(level, ParticleTypes.SPORE_BLOSSOM_AIR, x, y + 30, z,
                                30, currentRadius * 0.5, 20, currentRadius * 0.5, 0.005, true);
                    }
                }
            });
        }
    }

    private void applyRadiationEffects(ServerLevel level, double x, double y, double z, int radius, int duration, int amplifier) {
        AABB area = new AABB(x - radius, y - radius / 2, z - radius, x + radius, y + radius, z + radius);
        for (Entity entity : level.getEntities(null, area)) {
            if (entity instanceof LivingEntity living && !(entity == this.getOwner())) {
                double distance = entity.distanceToSqr(x, y, z);
                double maxDistSq = radius * radius;
                
                if (distance < maxDistSq) {
                    // Closer = stronger effects
                    float intensity = 1.0f - (float)(distance / maxDistSq);
                    int effectDuration = (int)(duration * intensity);
                    int effectAmplifier = (int)(amplifier * intensity);
                    
                    // Wither - radiation damage
                    living.addEffect(new MobEffectInstance(MobEffects.WITHER, effectDuration, effectAmplifier, false, true));
                    // Poison - sickness
                    living.addEffect(new MobEffectInstance(MobEffects.POISON, effectDuration, effectAmplifier, false, true));
                    // Hunger - radiation sickness
                    living.addEffect(new MobEffectInstance(MobEffects.HUNGER, effectDuration * 2, effectAmplifier + 1, false, true));
                    // Weakness - radiation weakness
                    living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, effectDuration, effectAmplifier, false, true));
                    // Slowness - fatigue
                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, effectDuration / 2, effectAmplifier, false, true));
                    // Nausea - disorientation (short)
                    if (intensity > 0.5f) {
                        living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, true));
                    }
                    // Blindness for very close entities
                    if (intensity > 0.8f) {
                        living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, false, true));
                    }
                }
            }
        }
    }

    private void createCrater(ServerLevel level, BlockPos center, int radius) {
        // Split crater into Y layers for optimization - process layer by layer
        int minY = -radius;
        int maxY = radius / 2; // Less destruction upward (realistic - explosion goes more down/sideways)
        
        for (int layer = 0; layer <= maxY - minY; layer++) {
            int dy = minY + layer;
            int finalDy = dy;
            int delay = layer / 3; // 3 layers per tick
            
            Mod.queueServerWork(delay, () -> {
                createCraterLayer(level, center, radius, finalDy);
            });
        }
    }

    private void createCraterLayer(ServerLevel level, BlockPos center, int radius, int dy) {
        int blocksProcessed = 0;
        int maxBlocksPerLayer = 3000;
        
        // Calculate max horizontal radius at this Y level (sphere formula)
        double yRatio = Math.abs(dy) / (double) radius;
        int horizontalRadius = (int) (radius * Math.sqrt(1 - yRatio * yRatio));
        if (horizontalRadius <= 0) return;
        
        for (int dx = -horizontalRadius; dx <= horizontalRadius && blocksProcessed < maxBlocksPerLayer; dx++) {
            for (int dz = -horizontalRadius; dz <= horizontalRadius && blocksProcessed < maxBlocksPerLayer; dz++) {
                // True spherical distance
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                
                if (distance <= radius) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!level.getBlockState(pos).isAir() && level.getBlockState(pos).getDestroySpeed(level, pos) >= 0) {
                        blocksProcessed++;
                        double distRatio = distance / radius;
                        
                        // Inner sphere - completely empty
                        if (distRatio < 0.5) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        }
                        // Middle sphere - mostly destroyed
                        else if (distRatio < 0.75) {
                            if (level.random.nextFloat() < 0.8f) {
                                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                            } else if (dy >= 0) {
                                level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 2);
                            }
                        }
                        // Outer sphere - partial destruction with fire
                        else {
                            float destroyChance = 0.5f - (float)(distRatio - 0.75) * 2f;
                            if (level.random.nextFloat() < destroyChance) {
                                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                            } else if (dy >= 0 && level.random.nextFloat() < 0.4f) {
                                level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
        }
    }

    private void destroyBlocksInRing(ServerLevel level, BlockPos center, int innerRadius, int outerRadius) {
        // Split into 4 quadrants + Y layers for even distribution
        int minY = -innerRadius / 4;
        int maxY = innerRadius / 3;
        
        int taskId = 0;
        for (int quadrant = 0; quadrant < 4; quadrant++) {
            for (int layer = minY; layer <= maxY; layer++) {
                int q = quadrant;
                int dy = layer;
                Mod.queueServerWork(taskId++, () -> {
                    destroyRingQuadrant(level, center, innerRadius, outerRadius, dy, q);
                });
            }
        }
    }

    private void destroyRingQuadrant(ServerLevel level, BlockPos center, int innerRadius, int outerRadius, int dy, int quadrant) {
        int blocksProcessed = 0;
        int maxBlocks = 1500;
        
        // Determine quadrant bounds
        int dxStart, dxEnd, dzStart, dzEnd;
        switch (quadrant) {
            case 0 -> { dxStart = 0; dxEnd = outerRadius; dzStart = 0; dzEnd = outerRadius; }      // +X +Z
            case 1 -> { dxStart = -outerRadius; dxEnd = 0; dzStart = 0; dzEnd = outerRadius; }     // -X +Z
            case 2 -> { dxStart = -outerRadius; dxEnd = 0; dzStart = -outerRadius; dzEnd = 0; }    // -X -Z
            default -> { dxStart = 0; dxEnd = outerRadius; dzStart = -outerRadius; dzEnd = 0; }    // +X -Z
        }
        
        for (int dx = dxStart; dx <= dxEnd && blocksProcessed < maxBlocks; dx++) {
            for (int dz = dzStart; dz <= dzEnd && blocksProcessed < maxBlocks; dz++) {
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                
                if (distance >= innerRadius && distance <= outerRadius) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!level.getBlockState(pos).isAir() && level.getBlockState(pos).getDestroySpeed(level, pos) >= 0) {
                        blocksProcessed++;
                        float distRatio = (float)(distance - innerRadius) / (outerRadius - innerRadius);
                        float chance = 0.55f - distRatio * 0.35f;
                        
                        if (level.random.nextFloat() < chance) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        } else if (dy >= 0 && level.random.nextFloat() < 0.2f) {
                            level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 2);
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

        // SHOCKWAVE - epic visible expanding ring
        // Initial bright white ring burst
        for (int i = 0; i < 600; i++) {
            Vec3 v = new Vec3(1, 0, 0).yRot((float) (i * 0.01047f)); // Full circle
            ParticleTool.sendParticle(level, new CustomCloudOption(1, 1, 1, 80, 8, 0, false, false),
                    x, y + 2, z, 0, v.x, v.y, v.z, 400, true);
        }
        
        // Multiple expanding shockwave rings over time
        for (int wave = 0; wave < 40; wave++) {
            int w = wave;
            Mod.queueServerWork(wave, () -> {
                // Main shockwave ring - dense white/gray wall of dust
                for (int i = 0; i < 500; i++) {
                    float angle = (float) (i * 0.01257f); // 2*PI/500
                    Vec3 v = new Vec3(1, 0, 0).yRot(angle);
                    // Bright leading edge
                    ParticleTool.sendParticle(level, new CustomCloudOption(1, 1, 0.95f, 100, 10, 0, false, false),
                            x, y + 1.5, z, 0, v.x, v.y, v.z, 200 + w * 20, true);
                    // Dust cloud behind
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.8f, 0.75f, 0.7f, 120, 8, 0, false, false),
                            x, y + 1, z, 0, v.x, v.y, v.z, 180 + w * 18, true);
                }
                
                // Vertical dust wall rising from shockwave
                ParticleTool.sendParticle(level, new CustomCloudOption(0.7f, 0.65f, 0.6f, 150, 12, 0, false, false),
                        x, y + 3, z, 200, 8 + w * 4, 4, 8 + w * 4, 0.02, true);
                
                // Ground debris kicked up
                ParticleTool.sendParticle(level, new CustomCloudOption(0.5f, 0.45f, 0.4f, 180, 6, 0, false, false),
                        x, y + 0.5, z, 100, 10 + w * 5, 0.5, 10 + w * 5, 0.015, true);
                
                // Dirt/debris particles
                ParticleTool.sendParticle(level, ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 2, z, 80, 6 + w * 3, 2, 6 + w * 3, 0.03, true);
            });
        }
        
        // Secondary slower dust wave
        for (int wave = 0; wave < 30; wave++) {
            int w = wave;
            Mod.queueServerWork(20 + wave * 2, () -> {
                // Brown/tan dust following behind main wave
                for (int i = 0; i < 300; i++) {
                    float angle = (float) (i * 0.02094f);
                    Vec3 v = new Vec3(1, 0, 0).yRot(angle);
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.6f, 0.5f, 0.4f, 200, 7, 0, false, false),
                            x, y + 0.8, z, 0, v.x, v.y, v.z, 120 + w * 15, true);
                }
            });
        }

        // MUSHROOM CLOUD - extended duration (300 ticks = 15 seconds)
        for (int i = 0; i < 300; i++) {
            int tick = i;
            Mod.queueServerWork(i, () -> {
                
                // Phase 1: Initial fireball rising (0-80 ticks) - LONGER FIRE
                if (tick < 80) {
                    // Bright orange/white fireball core rising - stays bright longer
                    float brightness = 1.0f - (tick / 160f); // Slower fade
                    ParticleTool.sendParticle(level, new CustomCloudOption(brightness, brightness * 0.6f, brightness * 0.1f, 250, 12, 0, true, true),
                            x, y + 2 * tick, z, 120, 8 - 0.05 * tick, 3, 8 - 0.05 * tick, 0.02, true);
                    
                    // Fire stars in rising column
                    ParticleTool.sendParticle(level, ModParticleTypes.FIRE_STAR.get(), x, y + 1.5 * tick, z, 150, 0, 0, 0, 2, true);
                    
                    // Dark smoke stem forming (behind fire)
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.4f, 0.35f, 0.3f, 350, 14, 0, true, true),
                            x, y + 1.2 * tick, z, 80, 5 + 0.1 * tick, 1.5, 5 + 0.1 * tick, 0.012, true);
                }

                // Phase 2: Mushroom cap forming (40-180 ticks) - EXTENDED FIRE IN CAP
                if (tick >= 40 && tick < 180) {
                    int k = tick - 40;
                    float capHeight = 100 + k * 0.4f;
                    
                    // Inner BRIGHT FIRE cap (orange/yellow/white) - stays fiery until tick 140
                    if (tick < 140) {
                        float fireBrightness = 1.0f - Math.max(0, (tick - 80) / 120f);
                        ParticleTool.sendParticle(level, new CustomCloudOption(fireBrightness, fireBrightness * 0.7f, fireBrightness * 0.2f, 300, 16, 0, true, true),
                                x, y + capHeight, z, 40 + k / 2, 3 + k * 0.12, 4 + k * 0.08, 3 + k * 0.12, 0.007, true);
                        
                        // Fire stars in cap
                        if (tick < 100) {
                            ParticleTool.sendParticle(level, ModParticleTypes.FIRE_STAR.get(), x, y + capHeight, z, 80, 0, 0, 0, 1.5, true);
                        }
                    }
                    
                    // Middle cap layer (orange -> dark orange over time)
                    float orangeFade = Math.max(0.3f, 0.9f - (k / 200f));
                    ParticleTool.sendParticle(level, new CustomCloudOption(orangeFade, orangeFade * 0.5f, 0.1f, 320, 14, 0, true, true),
                            x, y + capHeight - 8, z, 30 + k / 2, 4 + k * 0.18, 3 + k * 0.06, 4 + k * 0.18, 0.006, true);
                    
                    // Outer dark smoke cap
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.35f, 0.3f, 0.25f, 400, 18, 0, true, true),
                            x, y + capHeight + 8, z, 25 + k / 2, 5 + k * 0.22, 5 + k * 0.1, 5 + k * 0.22, 0.005, true);
                }

                // Phase 3: Stem connecting to cap (30-150 ticks) - FIERY STEM
                if (tick >= 30 && tick < 150) {
                    // Fire in stem
                    if (tick < 100) {
                        float stemFire = 1.0f - (tick - 30) / 100f;
                        ParticleTool.sendParticle(level, new CustomCloudOption(stemFire, stemFire * 0.5f, 0.1f, 280, 10, 0, true, true),
                                x, y + 50, z, 50, 6, 25, 6, 0.004, true);
                    }
                    // Smoke stem
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.45f, 0.4f, 0.35f, 380, 10, 0, true, true),
                            x, y + 50, z, 45, 6, 30, 6, 0.003, true);
                }

                // Ground smoke ring expanding slowly
                if (tick < 100) {
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.55f, 0.5f, 0.45f, 300, 6, 0, false, false),
                            x, y + 0.5, z, 6 + tick / 2, tick * 1.2, 0.3, tick * 1.2, 0.0004 * tick, true);
                }

                // Secondary explosions and fire bursts in fireball
                if (tick % 6 == 0 && tick < 70) {
                    ParticleTool.sendParticle(level, ParticleTypes.EXPLOSION, x, y + tick * 1.5, z, 50, 12, 8, 12, 1, true);
                    ParticleTool.sendParticle(level, ModParticleTypes.FIRE_STAR.get(), x, y + tick, z, 500, 0, 0, 0, 4, true);
                    ParticleTool.sendParticle(level, ParticleTypes.FLASH, x, y + tick * 1.2, z, 30, 8, 5, 8, 1, true);
                }

                // Phase 4: Lingering smoke and fallout (120-300 ticks)
                if (tick >= 120) {
                    // Ash and debris falling
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.5f, 0.45f, 0.4f, 450, 8, 0, false, false),
                            x, y + 120, z, 35, 45, 35, 45, 0.0015, true);
                    
                    // Ground level lingering smoke
                    if (tick % 2 == 0) {
                        ParticleTool.sendParticle(level, new CustomCloudOption(0.55f, 0.5f, 0.45f, 400, 5, 0, false, false),
                                x, y + 3, z, 25, 35, 0.8, 35, 0.003, true);
                    }
                }

                // Persistent mushroom cap with slow fire fade (80-280 ticks)
                if (tick >= 80 && tick < 280) {
                    // Fire glow in cap that slowly fades
                    float fireGlow = Math.max(0, 0.8f - ((tick - 80) / 300f));
                    if (fireGlow > 0.2f) {
                        ParticleTool.sendParticle(level, new CustomCloudOption(fireGlow, fireGlow * 0.5f, 0.1f, 450, 20, 0, true, true),
                                x, y + 110, z, 20, 30, 18, 30, 0.002, true);
                    }
                    // Dark smoke cap
                    float smokeFade = 1.0f - ((tick - 80) / 250f);
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.4f * smokeFade, 0.35f * smokeFade, 0.3f * smokeFade, 500, 22, 0, true, true),
                            x, y + 115, z, 18, 35, 20, 35, 0.0025, true);
                }
            });
        }

        // Final lingering effects (after main sequence) - more fire and embers
        for (int i = 0; i < 150; i++) {
            int tick = i;
            Mod.queueServerWork(300 + i * 2, () -> {
                // Slow dissipating smoke column
                ParticleTool.sendParticle(level, ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 30 + tick / 2, z, 25, 20, 40, 20, 0.006, true);
                
                // Ground fires and embers - more frequent
                if (tick % 3 == 0) {
                    ParticleTool.sendParticle(level, ParticleTypes.LAVA, x, y + 1, z, 15, 25, 0.5, 25, 0.015, true);
                    ParticleTool.sendParticle(level, ParticleTypes.FLAME, x, y + 2, z, 10, 20, 1, 20, 0.02, true);
                }
                
                // Residual fire glow in the sky
                if (tick < 50) {
                    float residualFire = 0.4f - (tick / 150f);
                    ParticleTool.sendParticle(level, new CustomCloudOption(residualFire, residualFire * 0.4f, 0.05f, 300, 15, 0, true, true),
                            x, y + 80, z, 10, 25, 15, 25, 0.001, true);
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
