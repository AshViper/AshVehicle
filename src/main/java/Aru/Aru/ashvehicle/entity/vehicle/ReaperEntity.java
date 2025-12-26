package Aru.Aru.ashvehicle.entity.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * MQ-9 Reaper - беспилотный разведывательно-ударный аппарат.
 * Управляется дистанционно через монитор.
 */
public class ReaperEntity extends RemoteDroneEntity {
    
    public ReaperEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
