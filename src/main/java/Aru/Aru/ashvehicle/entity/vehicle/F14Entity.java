package Aru.Aru.ashvehicle.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class F14Entity extends GeoVehicleEntity {
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
    }
}
