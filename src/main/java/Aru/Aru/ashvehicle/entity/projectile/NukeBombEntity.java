package Aru.Aru.ashvehicle.entity.projectile;

import Aru.Aru.ashvehicle.Packet.NukeSkyPacket;
import Aru.Aru.ashvehicle.init.ModNetwork;
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
import net.minecraftforge.network.PacketDistributor;
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

        // Damage explosion - NO DEFAULT PARTICLES (we use our own)
        new CustomExplosion.Builder(this)
                .damageSource(ModDamageTypes.causeCustomExplosionDamage(serverLevel.registryAccess(), this, this.getOwner()))
                .damage(this.explosionDamage)
                .radius(this.explosionRadius)
                .position(pos)
                .damageMultiplier(2.0F)
                .withParticleType(null) // Disable default SBW particles
                .keepBlock() // Don't destroy blocks here, we do it ourselves
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

        // Shockwave block destruction - overlapping waves for complete coverage
        if (ExplosionConfig.EXPLOSION_DESTROY.get()) {
            for (int wave = 0; wave < 8; wave++) {
                int w = wave;
                // Overlapping rings: 50-80, 70-100, 90-120, 110-140, 130-160, 150-180, 170-200, 190-220
                int inner = 50 + w * 20;
                int outer = 80 + w * 20;
                Mod.queueServerWork(35 + wave * 8, () -> {
                    destroyBlocksInRing(serverLevel, center, inner, outer);
                });
            }
        }

        // Epic nuclear explosion particles
        spawnNuclearExplosionParticles(serverLevel, x, y, z);

        // Send red sky effect to all nearby clients (radius 500, duration 1200 ticks = 60 seconds)
        ModNetwork.INSTANCE.send(
                PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, 500, serverLevel.dimension())),
                new NukeSkyPacket(x, y, z, 500f, 1200)
        );

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
        // Determine quadrant bounds
        int dxStart, dxEnd, dzStart, dzEnd;
        switch (quadrant) {
            case 0 -> { dxStart = 0; dxEnd = outerRadius; dzStart = 0; dzEnd = outerRadius; }      // +X +Z
            case 1 -> { dxStart = -outerRadius; dxEnd = 0; dzStart = 0; dzEnd = outerRadius; }     // -X +Z
            case 2 -> { dxStart = -outerRadius; dxEnd = 0; dzStart = -outerRadius; dzEnd = 0; }    // -X -Z
            default -> { dxStart = 0; dxEnd = outerRadius; dzStart = -outerRadius; dzEnd = 0; }    // +X -Z
        }
        
        for (int dx = dxStart; dx <= dxEnd; dx++) {
            for (int dz = dzStart; dz <= dzEnd; dz++) {
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                
                if (distance >= innerRadius && distance <= outerRadius) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!level.getBlockState(pos).isAir() && level.getBlockState(pos).getDestroySpeed(level, pos) >= 0) {
                        // Higher destruction chance, decreasing with distance
                        float distRatio = (float)(distance - innerRadius) / (outerRadius - innerRadius);
                        float chance = 0.85f - distRatio * 0.5f; // 85% at inner edge, 35% at outer
                        
                        if (level.random.nextFloat() < chance) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        } else if (dy >= 0 && level.random.nextFloat() < 0.35f) {
                            level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
    }

    private void spawnNuclearExplosionParticles(ServerLevel level, double x, double y, double z) {
        // ============== MW REMASTERED STYLE NUKE ==============
        
        // EPIC SOUNDS - very loud and far
        SoundTool.playDistantSound(level, ModSounds.HUGE_EXPLOSION_CLOSE.get(), new Vec3(x, y, z), 32, 1, null);
        SoundTool.playDistantSound(level, ModSounds.HUGE_EXPLOSION_FAR.get(), new Vec3(x, y, z), 96, 1, null);
        SoundTool.playDistantSound(level, ModSounds.HUGE_EXPLOSION_VERY_FAR.get(), new Vec3(x, y, z), 600, 1, null);

        // MASSIVE SCREEN SHAKE - very long duration like MW
        ShakeClientMessage.sendToNearbyPlayers(level, x, y, z, 1500, 100, 80);
        
        // Delayed aftershocks
        for (int i = 1; i <= 5; i++) {
            int delay = i * 40;
            float intensity = 60 - i * 10;
            Mod.queueServerWork(delay, () -> ShakeClientMessage.sendToNearbyPlayers(level, x, y, z, 1200, intensity, 30));
        }

        // INITIAL FLASH - blinding white light (MW style - huge flash)
        ParticleTool.sendParticle(level, ParticleTypes.FLASH, x, y + 10, z, 1500, 40, 40, 40, 1, true);
        ParticleTool.sendParticle(level, ParticleTypes.EXPLOSION, x, y + 15, z, 500, 25, 25, 25, 1, true);

        // FIRE STARS - massive initial fireball
        ParticleTool.sendParticle(level, ModParticleTypes.FIRE_STAR.get(), x, y + 10, z, 5000, 0, 0, 0, 6, true);

        // ============== RED SKY EFFECT - MASSIVE (lasts 60+ seconds) ==============
        for (int i = 0; i < 300; i++) {
            int t = i;
            Mod.queueServerWork(i, () -> {
                // Deep red sky dome - very high, very wide
                ParticleTool.sendParticle(level, new CustomCloudOption(0.95f, 0.15f, 0.05f, 800, 40, 0, false, false),
                        x, y + 200 + t / 2, z, 150, 150 + t, 40, 150 + t, 0.0005, true);
                // Orange mid-layer
                ParticleTool.sendParticle(level, new CustomCloudOption(1f, 0.35f, 0.08f, 700, 35, 0, false, false),
                        x, y + 160 + t / 2, z, 120, 120 + t * 0.8, 30, 120 + t * 0.8, 0.0008, true);
                // Yellow-orange lower glow
                ParticleTool.sendParticle(level, new CustomCloudOption(1f, 0.5f, 0.15f, 600, 30, 0, false, false),
                        x, y + 120 + t / 3, z, 100, 100 + t * 0.6, 25, 100 + t * 0.6, 0.001, true);
            });
        }
        
        // Extended red sky (300-600 ticks) - slowly fading
        for (int i = 0; i < 150; i++) {
            int t = i;
            Mod.queueServerWork(300 + i * 2, () -> {
                float fade = 1.0f - t / 200f;
                ParticleTool.sendParticle(level, new CustomCloudOption(0.8f * fade, 0.12f * fade, 0.04f * fade, 700, 35, 0, false, false),
                        x, y + 180, z, 100, 250, 50, 250, 0.0003, true);
            });
        }

        // ============== EPIC SHOCKWAVE - HUGE DUST WALL ==============
        // Initial massive white ring burst
        for (int i = 0; i < 1000; i++) {
            Vec3 v = new Vec3(1, 0, 0).yRot((float) (i * 0.00628f)); // Full circle
            // Tall white wall
            ParticleTool.sendParticle(level, new CustomCloudOption(1, 1, 1, 200, 25, 0, false, false),
                    x, y + 8, z, 0, v.x, v.y, v.z, 200, true);
            ParticleTool.sendParticle(level, new CustomCloudOption(1, 1, 0.95f, 180, 20, 0, false, false),
                    x, y + 4, z, 0, v.x, v.y, v.z, 220, true);
        }
        
        // SLOW expanding shockwave - HUGE dust wall (150 waves over 300 ticks = 15 seconds)
        for (int wave = 0; wave < 150; wave++) {
            int w = wave;
            Mod.queueServerWork(wave * 2, () -> {
                // MAIN DUST WALL - very tall, very dense
                for (int i = 0; i < 500; i++) {
                    float angle = (float) (i * 0.01257f);
                    Vec3 v = new Vec3(1, 0, 0).yRot(angle);
                    
                    // Bright white leading edge - TALL
                    ParticleTool.sendParticle(level, new CustomCloudOption(1, 1, 0.95f, 250, 30, 0, false, false),
                            x, y + 15, z, 0, v.x, v.y, v.z, 60 + w * 5, true);
                    // Mid-height dust
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.95f, 0.9f, 0.85f, 280, 25, 0, false, false),
                            x, y + 8, z, 0, v.x, v.y, v.z, 55 + w * 4.5, true);
                    // Ground level dust
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.85f, 0.8f, 0.75f, 300, 20, 0, false, false),
                            x, y + 2, z, 0, v.x, v.y, v.z, 50 + w * 4, true);
                }
                
                // Vertical dust column rising from wave
                ParticleTool.sendParticle(level, new CustomCloudOption(0.8f, 0.75f, 0.7f, 350, 35, 0, false, false),
                        x, y + 20, z, 250, 10 + w * 1.5, 15, 10 + w * 1.5, 0.03, true);
                
                // Ground debris cloud
                ParticleTool.sendParticle(level, new CustomCloudOption(0.7f, 0.65f, 0.6f, 400, 18, 0, false, false),
                        x, y + 1, z, 150, 12 + w * 2, 2, 12 + w * 2, 0.025, true);
            });
        }
        
        // Secondary brown/tan dust wave - follows behind
        for (int wave = 0; wave < 120; wave++) {
            int w = wave;
            Mod.queueServerWork(30 + wave * 2, () -> {
                for (int i = 0; i < 350; i++) {
                    float angle = (float) (i * 0.01795f);
                    Vec3 v = new Vec3(1, 0, 0).yRot(angle);
                    // Brown dust - tall
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.7f, 0.6f, 0.5f, 350, 22, 0, false, false),
                            x, y + 10, z, 0, v.x, v.y, v.z, 45 + w * 4, true);
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.65f, 0.55f, 0.45f, 380, 18, 0, false, false),
                            x, y + 3, z, 0, v.x, v.y, v.z, 40 + w * 3.5, true);
                }
            });
        }
        
        // Third wave - lingering dust haze
        for (int wave = 0; wave < 80; wave++) {
            int w = wave;
            Mod.queueServerWork(80 + wave * 3, () -> {
                ParticleTool.sendParticle(level, new CustomCloudOption(0.6f, 0.55f, 0.5f, 450, 15, 0, false, false),
                        x, y + 5, z, 200, 20 + w * 3, 8, 20 + w * 3, 0.015, true);
            });
        }

        // ============== MUSHROOM CLOUD - MW STYLE (600 ticks = 30 seconds) ==============
        for (int i = 0; i < 600; i++) {
            int tick = i;
            Mod.queueServerWork(i, () -> {
                
                // Phase 1: Initial fireball rising SLOWLY (0-150 ticks = 7.5 sec)
                if (tick < 150) {
                    float progress = tick / 150f;
                    float brightness = 1.0f - progress * 0.4f; // Stays bright longer
                    float height = tick * 1.5f; // Slower rise
                    
                    // Bright white/orange fireball core
                    ParticleTool.sendParticle(level, new CustomCloudOption(brightness, brightness * 0.7f, brightness * 0.2f, 350, 16, 0, true, true),
                            x, y + height, z, 150, 10 - progress * 4, 4, 10 - progress * 4, 0.025, true);
                    
                    // Fire stars constantly
                    ParticleTool.sendParticle(level, ModParticleTypes.FIRE_STAR.get(), x, y + height * 0.8, z, 200, 0, 0, 0, 2.5, true);
                    
                    // Inner orange glow
                    ParticleTool.sendParticle(level, new CustomCloudOption(1f, 0.5f, 0.1f, 300, 14, 0, true, true),
                            x, y + height * 0.9, z, 100, 8 - progress * 3, 3, 8 - progress * 3, 0.02, true);
                    
                    // Dark smoke forming behind
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.45f, 0.4f, 0.35f, 450, 18, 0, true, true),
                            x, y + height * 0.6, z, 90, 6 + progress * 4, 2, 6 + progress * 4, 0.015, true);
                }

                // Phase 2: Mushroom cap forming and STAYING FIERY (80-400 ticks = 16 sec of fire!)
                if (tick >= 80 && tick < 400) {
                    int k = tick - 80;
                    float capHeight = 150 + Math.min(k * 0.3f, 50); // Cap rises then stabilizes
                    float fireIntensity = Math.max(0, 1.0f - k / 400f); // Very slow fade
                    
                    // INNER FIRE - bright orange/yellow (stays until tick 300)
                    if (tick < 300) {
                        ParticleTool.sendParticle(level, new CustomCloudOption(fireIntensity, fireIntensity * 0.6f, fireIntensity * 0.15f, 400, 20, 0, true, true),
                                x, y + capHeight, z, 50 + k / 4, 5 + k * 0.08, 6 + k * 0.05, 5 + k * 0.08, 0.008, true);
                        
                        // Fire stars in cap
                        if (tick < 200) {
                            ParticleTool.sendParticle(level, ModParticleTypes.FIRE_STAR.get(), x, y + capHeight, z, 100, 0, 0, 0, 2, true);
                        }
                    }
                    
                    // MIDDLE LAYER - orange that slowly darkens
                    float orangeLevel = Math.max(0.25f, 0.95f - k / 500f);
                    ParticleTool.sendParticle(level, new CustomCloudOption(orangeLevel, orangeLevel * 0.45f, 0.08f, 450, 18, 0, true, true),
                            x, y + capHeight - 12, z, 40 + k / 4, 6 + k * 0.1, 5 + k * 0.04, 6 + k * 0.1, 0.007, true);
                    
                    // OUTER DARK SMOKE - expands slowly
                    float smokeAlpha = Math.max(0.15f, 0.5f - k / 800f);
                    ParticleTool.sendParticle(level, new CustomCloudOption(smokeAlpha + 0.1f, smokeAlpha, smokeAlpha - 0.05f, 550, 24, 0, true, true),
                            x, y + capHeight + 15, z, 35 + k / 4, 8 + k * 0.15, 8 + k * 0.08, 8 + k * 0.15, 0.006, true);
                }

                // Phase 3: Stem - fiery then smoky (50-350 ticks)
                if (tick >= 50 && tick < 350) {
                    int stemTick = tick - 50;
                    float stemFire = Math.max(0, 1.0f - stemTick / 200f);
                    
                    // Fire in stem (until tick 200)
                    if (tick < 200) {
                        ParticleTool.sendParticle(level, new CustomCloudOption(stemFire, stemFire * 0.5f, 0.1f, 350, 12, 0, true, true),
                                x, y + 70, z, 60, 8, 40, 8, 0.005, true);
                    }
                    
                    // Smoke stem - always present
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.5f, 0.45f, 0.4f, 500, 14, 0, true, true),
                            x, y + 70, z, 55, 8, 50, 8, 0.004, true);
                }

                // Ground smoke ring - very slow expansion
                if (tick < 200) {
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.6f, 0.55f, 0.5f, 400, 8, 0, false, false),
                            x, y + 1, z, 8 + tick / 3, tick * 0.8, 0.5, tick * 0.8, 0.0003 * tick, true);
                }

                // Secondary explosions in fireball (early phase)
                if (tick % 8 == 0 && tick < 120) {
                    ParticleTool.sendParticle(level, ParticleTypes.EXPLOSION, x, y + tick * 1.2, z, 60, 15, 10, 15, 1, true);
                    ParticleTool.sendParticle(level, ModParticleTypes.FIRE_STAR.get(), x, y + tick, z, 600, 0, 0, 0, 4.5, true);
                    ParticleTool.sendParticle(level, ParticleTypes.FLASH, x, y + tick, z, 40, 10, 8, 10, 1, true);
                }

                // Phase 4: Lingering effects (200-600 ticks)
                if (tick >= 200) {
                    // Ash falling from sky
                    if (tick % 3 == 0) {
                        ParticleTool.sendParticle(level, new CustomCloudOption(0.55f, 0.5f, 0.45f, 550, 10, 0, false, false),
                                x, y + 180, z, 40, 60, 50, 60, 0.002, true);
                    }
                    
                    // Ground smoke
                    if (tick % 4 == 0) {
                        ParticleTool.sendParticle(level, new CustomCloudOption(0.6f, 0.55f, 0.5f, 500, 6, 0, false, false),
                                x, y + 4, z, 30, 50, 1, 50, 0.004, true);
                    }
                }

                // Persistent cap glow (150-550 ticks) - VERY slow fade
                if (tick >= 150 && tick < 550) {
                    float glowFade = 1.0f - (tick - 150) / 500f;
                    float capY = 180;
                    
                    // Fire glow
                    if (glowFade > 0.15f) {
                        ParticleTool.sendParticle(level, new CustomCloudOption(glowFade * 0.8f, glowFade * 0.4f, 0.08f, 600, 26, 0, true, true),
                                x, y + capY, z, 25, 40, 25, 40, 0.003, true);
                    }
                    
                    // Smoke cap - persists longer
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.45f * glowFade, 0.4f * glowFade, 0.35f * glowFade, 650, 28, 0, true, true),
                            x, y + capY + 10, z, 22, 45, 28, 45, 0.003, true);
                }
            });
        }

        // Extended aftermath (600-1000 ticks = another 20 seconds of smoke)
        for (int i = 0; i < 200; i++) {
            int tick = i;
            Mod.queueServerWork(600 + i * 2, () -> {
                // Dissipating smoke column
                float fade = 1.0f - tick / 250f;
                ParticleTool.sendParticle(level, ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 50 + tick / 3, z, 30, 25, 60, 25, 0.008, true);
                
                // Ground fires
                if (tick % 4 == 0 && tick < 100) {
                    ParticleTool.sendParticle(level, ParticleTypes.LAVA, x, y + 1, z, 20, 35, 0.5, 35, 0.02, true);
                    ParticleTool.sendParticle(level, ParticleTypes.FLAME, x, y + 2, z, 15, 30, 1.5, 30, 0.025, true);
                }
                
                // Residual red sky glow
                if (tick < 80) {
                    float redGlow = 0.5f - tick / 200f;
                    ParticleTool.sendParticle(level, new CustomCloudOption(redGlow, redGlow * 0.3f, 0.05f, 400, 20, 0, false, false),
                            x, y + 120, z, 50, 80, 30, 80, 0.001, true);
                }
                
                // Final smoke wisps
                if (tick >= 100) {
                    ParticleTool.sendParticle(level, new CustomCloudOption(0.4f * fade, 0.35f * fade, 0.3f * fade, 500, 15, 0, true, true),
                            x, y + 100, z, 15, 50, 40, 50, 0.002, true);
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
