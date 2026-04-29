package Aru.Aru.ashvehicle.entity.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.base.BaseAircraftEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Math;

public class C130Entity extends BaseAircraftEntity {
    public static boolean isOpenHatch = false;
    private static final EntityDataAccessor<Float> HATCH_ROT = SynchedEntityData.defineId(C130Entity.class, EntityDataSerializers.FLOAT);
    public float hatchRotO = 0f;
    public C130Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HATCH_ROT, 0.0F);
    }

    public void setHatchRot(float value) {
        this.entityData.set(HATCH_ROT, value);
    }

    public float getHatchRot() {
        return this.entityData.get(HATCH_ROT);
    }

    public void toggleHatchMode() {
        isOpenHatch = ! isOpenHatch;
    }

    @Override
    public void baseTick() {
        super.baseTick();

        hatchRotO = getHatchRot();
        float target = this.isOpenHatch ? 30.0F : 0.0F;
        float current = getHatchRot();
        float diff = target - current;
        float newRot = current + diff * 0.05f;
        setHatchRot(newRot);
    }
}