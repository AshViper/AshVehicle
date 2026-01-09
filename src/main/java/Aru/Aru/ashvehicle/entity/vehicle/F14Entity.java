package Aru.Aru.ashvehicle.entity.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.base.BaseAircraftEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class F14Entity extends BaseAircraftEntity {
    public F14Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static boolean vtolMode = false;
    private static final EntityDataAccessor<Float> VTOL_ROT = SynchedEntityData.defineId(F14Entity.class, EntityDataSerializers.FLOAT);
    public float vtolRotO = 0f;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VTOL_ROT, 0.0F);
    }

    public void setPodRot(float value) {
        this.entityData.set(VTOL_ROT, value);
    }

    public float getPodRot() {
        return this.entityData.get(VTOL_ROT);
    }

    public void toggleVtolMode() {
        vtolMode = !vtolMode;
    }

    @Override
    public void baseTick() {
        super.baseTick();

        vtolRotO = getPodRot();
        float target = vtolMode ? 45.0F : 0.0F;
        float current = getPodRot();
        float diff = target - current;
        float newRot = current + diff * 0.05f;
        setPodRot(newRot);

        float power = Math.abs(this.getPower());
        if (power > 0.06F && this.level().isClientSide) {
            this.spawnAfterburnerEffect();
        }
    }

    @Override
    public List<Vec3> getAfterburnerParticlePositions() {
        List<Vec3> positions = new ArrayList<>();
        positions.add(new Vec3(-10, 2.0, -1));
        positions.add(new Vec3(-10, 2.0, 1));
        return positions;
    }
}
