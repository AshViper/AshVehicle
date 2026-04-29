package Aru.Aru.ashvehicle.entity.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.base.BaseAircraftEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class F2Entity extends BaseAircraftEntity {
    public F2Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        
        // 🔥 Afterburner particles (client side)
        float power = Math.abs(this.getPower());
        if (this.sprintInputDown() && this.level().isClientSide) {
            this.spawnAfterburnerParticles(getAfterburnerParticlePositions());
        }
    }

    @Override
    public List<Vec3> getAfterburnerParticlePositions() {
        List<Vec3> positions = new ArrayList<>();
        positions.add(new Vec3(-9.7119, 2.4143, 0));
        return positions;
    }
}
