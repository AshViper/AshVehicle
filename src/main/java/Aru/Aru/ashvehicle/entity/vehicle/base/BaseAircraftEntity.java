package Aru.Aru.ashvehicle.entity.vehicle.base;

import Aru.Aru.ashvehicle.init.ModParticleTypes;
import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAircraftEntity extends GeoVehicleEntity {

    public BaseAircraftEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public void spawnAfterburnerParticles(List<Vec3> localPositions) {
        if (localPositions == null || localPositions.isEmpty()) {
            return;
        }

        Vec3 basePos = this.position();
        Vec3 look = this.getViewVector(1.0F);
        Vec3 motion = this.getDeltaMovement();
        double speed = motion.length();

        float yawDeg = this.getYRot();
        float pitchDeg = this.getXRot();
        float rollDeg = this.getRoll();

        double yaw = Math.toRadians(yawDeg + 90);
        double pitch = Math.toRadians(pitchDeg);
        double roll = Math.toRadians(-(rollDeg - 90));

        double cy = Math.cos(yaw),   sy = Math.sin(yaw);
        double cp = Math.cos(pitch), sp = Math.sin(pitch);
        double cr = Math.cos(roll),  sr = Math.sin(roll);

        for (Vec3 local : localPositions) {
            int particleCount = 2 + (int)(speed * 3);
            particleCount = Math.min(particleCount, 5);
            
            for (int i = 0; i < particleCount; i++) {
                double offsetX = local.x + (this.random.nextDouble() - 0.5) * 0.3;
                double offsetY = local.y + (this.random.nextDouble() - 0.5) * 0.3;
                double offsetZ = local.z + (this.random.nextDouble() - 0.5) * 0.3;

                double wx =
                        offsetX * (cp * cy) +
                                offsetY * (sr * sp * cy - cr * sy) +
                                offsetZ * (cr * sp * cy + sr * sy);

                double wy =
                        offsetX * (cp * sy) +
                                offsetY * (sr * sp * sy + cr * cy) +
                                offsetZ * (cr * sp * sy - sr * cy);

                double wz =
                        offsetX * (-sp) +
                                offsetY * (sr * cp) +
                                offsetZ * (cr * cp);

                Vec3 worldOffset = new Vec3(wx, wz, wy);
                Vec3 spawnPos = basePos.add(worldOffset);

                double velocityMultiplier = 0.3 + speed * 0.1;
                double vx = look.x * -velocityMultiplier + (this.random.nextDouble() - 0.5) * 0.05;
                double vy = 0.02 + (this.random.nextDouble() - 0.5) * 0.03;
                double vz = look.z * -velocityMultiplier + (this.random.nextDouble() - 0.5) * 0.05;

                this.level().addParticle(
                        ModParticleTypes.AFTERBURNER_FLAME.get(),
                        spawnPos.x, spawnPos.y, spawnPos.z,
                        vx, vy, vz
                );
            }
        }
    }

    public void spawnAfterburnerEffect() {
        if (isVtolActive()) {
            List<Vec3> vtolPositions = getVtolAfterburnerPositions();
            if (vtolPositions != null && !vtolPositions.isEmpty()) {
                spawnAfterburnerParticles(vtolPositions);
                return;
            }
        }
        spawnAfterburnerParticles(getAfterburnerParticlePositions());
    }

    public List<Vec3> getAfterburnerParticlePositions() {
        List<Vec3> positions = new ArrayList<>();
        positions.add(new Vec3(-10, 2.0, -1));
        positions.add(new Vec3(-10, 2.0, 1));
        return positions;
    }

    public List<Vec3> getVtolAfterburnerPositions() {
        return null;
    }

    public boolean isVtolActive() {
        return false;
    }
}
