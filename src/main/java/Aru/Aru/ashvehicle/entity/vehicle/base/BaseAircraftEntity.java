package Aru.Aru.ashvehicle.entity.vehicle.base;

import Aru.Aru.ashvehicle.init.ModParticleTypes;
import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAircraftEntity extends GeoVehicleEntity {

    public BaseAircraftEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        // Set a high view scale so the client considers this entity visible from much further away.
        this.setViewScale(10.0);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        // Allow rendering up to 512 blocks away (512^2 = 262144)
        // This is essential for massive ships and aircraft.
        double renderDistance = 512.0;
        return pDistance < renderDistance * renderDistance;
    }

    /**
     * Data class to represent an afterburner source point.
     * Includes both local position and the direction the flame should point.
     */
    public static class AfterburnerSource {
        public final Vec3 localPos;
        public final Vec3 localDir;

        public AfterburnerSource(Vec3 localPos, Vec3 localDir) {
            this.localPos = localPos;
            this.localDir = localDir;
        }

        public AfterburnerSource(Vec3 localPos) {
            this(localPos, new Vec3(-1.0, 0.0, 0.0)); // Default points backwards
        }
    }

    public void spawnAfterburnerParticlesFromSources(List<AfterburnerSource> sources) {
        if (sources == null || sources.isEmpty() || !this.level().isClientSide) {
            return;
        }

        Vec3 basePos = this.position();
        Vec3 motion = this.getDeltaMovement();
        double speed = motion.length();

        // Get entity rotations
        float yawDeg = this.getYRot();
        float pitchDeg = this.getXRot();
        float rollDeg = this.getRoll();

        // Match existing coordinate system logic:
        // Model forward is +X, Model up is +Y (mostly), Model side is +Z.
        double yaw = Math.toRadians(yawDeg + 90);
        double pitch = Math.toRadians(pitchDeg);
        double roll = Math.toRadians(-(rollDeg - 90));

        double cy = Math.cos(yaw),   sy = Math.sin(yaw);
        double cp = Math.cos(pitch), sp = Math.sin(pitch);
        double cr = Math.cos(roll),  sr = Math.sin(roll);

        for (AfterburnerSource source : sources) {
            // Transform local position to world space
            Vec3 local = source.localPos;
            double wx = local.x * (cp * cy) + local.y * (sr * sp * cy - cr * sy) + local.z * (cr * sp * cy + sr * sy);
            double wy = local.x * (cp * sy) + local.y * (sr * sp * sy + cr * cy) + local.z * (cr * sp * sy - sr * cy);
            double wz = local.x * (-sp) + local.y * (sr * cp) + local.z * (cr * cp);

            Vec3 spawnPos = basePos.add(wx, wz, wy);

            // Transform local direction to world space
            Vec3 dir = source.localDir;
            double dx = dir.x * (cp * cy) + dir.y * (sr * sp * cy - cr * sy) + dir.z * (cr * sp * cy + sr * sy);
            double dy = dir.x * (cp * sy) + dir.y * (sr * sp * sy + cr * cy) + dir.z * (cr * sp * sy - sr * cy);
            double dz = dir.x * (-sp) + dir.y * (sr * cp) + dir.z * (cr * cp);

            Vec3 worldDir = new Vec3(dx, dz, dy).normalize();

            int particleCount = 2 + (int)(speed * 3);
            particleCount = Math.min(particleCount, 5);

            for (int i = 0; i < particleCount; i++) {
                double velocityMultiplier = 0.3 + speed * 0.1;

                // Base velocity follows the engine direction
                double vx = worldDir.x * velocityMultiplier + (this.random.nextDouble() - 0.5) * 0.05;
                double vy = worldDir.y * velocityMultiplier + 0.02 + (this.random.nextDouble() - 0.5) * 0.03;
                double vz = worldDir.z * velocityMultiplier + (this.random.nextDouble() - 0.5) * 0.05;

                this.level().addParticle(
                        ModParticleTypes.AFTERBURNER_FLAME.get(),
                        spawnPos.x + (this.random.nextDouble() - 0.5) * 0.2,
                        spawnPos.y + (this.random.nextDouble() - 0.5) * 0.2,
                        spawnPos.z + (this.random.nextDouble() - 0.5) * 0.2,
                        vx, vy, vz
                );
            }
        }
    }

    public void spawnAfterburnerEffect() {
        spawnAfterburnerParticlesFromSources(getAfterburnerSources());
    }

    /**
     * Overridable method to provide a list of afterburner sources.
     * By default, it wraps the legacy getAfterburnerParticlePositions() with backward-pointing directions.
     */
    public List<AfterburnerSource> getAfterburnerSources() {
        List<AfterburnerSource> sources = new ArrayList<>();
        List<Vec3> legacyPositions = getAfterburnerParticlePositions();
        if (legacyPositions != null) {
            for (Vec3 pos : legacyPositions) {
                sources.add(new AfterburnerSource(pos));
            }
        }
        return sources;
    }

    /**
     * Deprecated overload to maintain compatibility with existing aircraft entities.
     */
    @Deprecated
    public void spawnAfterburnerParticles(List<Vec3> localPositions) {
        List<AfterburnerSource> sources = new ArrayList<>();
        if (localPositions != null) {
            for (Vec3 pos : localPositions) {
                sources.add(new AfterburnerSource(pos));
            }
        }
        spawnAfterburnerParticlesFromSources(sources);
    }

    @Deprecated
    public List<Vec3> getAfterburnerParticlePositions() {
        List<Vec3> positions = new ArrayList<>();
        positions.add(new Vec3(-10, 2.0, -1));
        positions.add(new Vec3(-10, 2.0, 1));
        return positions;
    }

    @Deprecated
    public List<Vec3> getVtolAfterburnerPositions() {
        return null;
    }

    @Deprecated
    public boolean isVtolActive() {
        return false;
    }

}
