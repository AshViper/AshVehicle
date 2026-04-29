package Aru.Aru.ashvehicle.entity.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.base.BaseAircraftEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class F22Entity extends BaseAircraftEntity {
    public F22Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        
        float power = Math.abs(this.getPower());
        if (this.sprintInputDown() && this.level().isClientSide) {
            this.spawnAfterburnerEffect();
        }
    }

    @Override
    public List<AfterburnerSource> getAfterburnerSources() {
        List<AfterburnerSource> sources = new ArrayList<>();
        sources.add(new AfterburnerSource(new Vec3(-10, 2.0, -1.2)));
        sources.add(new AfterburnerSource(new Vec3(-10, 2.0, 1.2)));
        return sources;
    }

}
