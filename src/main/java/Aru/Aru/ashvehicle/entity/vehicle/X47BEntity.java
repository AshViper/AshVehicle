package Aru.Aru.ashvehicle.entity.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * X-47B UCAV - беспилотный боевой летательный аппарат.
 * Управляется дистанционно через монитор.
 */
public class X47BEntity extends RemoteDroneEntity {
    
    public X47BEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
