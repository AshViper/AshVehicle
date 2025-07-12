package Aru.Aru.ashvehicle.entity.weapon;

import Aru.Aru.ashvehicle.AshVehicle;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class BallisticMissileWeapon extends VehicleWeapon {
    public BallisticMissileWeapon() {
        this.icon = new ResourceLocation(AshVehicle.MODID, "textures/icon/weapon/aam4-icon.png");
    }

    public BallisticMissileEntity create(LivingEntity entity)  {
        return new BallisticMissileEntity(entity, entity.level());
    }
}