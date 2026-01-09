package Aru.Aru.ashvehicle.entity.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.base.BaseAircraftEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class F16Entity extends BaseAircraftEntity {
    public F16Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        
        float power = Math.abs(this.getPower());
        if (power > 0.06F && this.level().isClientSide) {
            this.spawnAfterburnerEffect();
        }
    }
}
