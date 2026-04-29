package Aru.Aru.ashvehicle.mixin;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VehicleEntity.class)
public interface VehicleEntityAccessor {
    @Invoker(value = "setEnergy", remap = false)
    void superbwarfare$invokeSetEnergy(int energy);
}
