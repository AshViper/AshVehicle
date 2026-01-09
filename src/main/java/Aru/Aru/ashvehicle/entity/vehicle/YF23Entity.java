package Aru.Aru.ashvehicle.entity.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.base.BaseAircraftEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class YF23Entity extends BaseAircraftEntity {
    public YF23Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        
        // 🔥 Afterburner particles (client side)
        float power = Math.abs(this.getPower());
        if (power > 0.06F && this.level().isClientSide) {
            this.spawnAfterburnerParticles(getAfterburnerParticlePositions());
        }
    }
}
