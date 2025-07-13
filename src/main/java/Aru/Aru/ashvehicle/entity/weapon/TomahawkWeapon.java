package Aru.Aru.ashvehicle.entity.weapon;

import Aru.Aru.ashvehicle.AshVehicle;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class TomahawkWeapon extends VehicleWeapon {
    public TomahawkWeapon() {
        this.icon = new ResourceLocation(AshVehicle.MODID, "textures/icon/weapon/aam4-icon.png");
    }

    public TomahawkEntity create(LivingEntity entity)  {
        return new TomahawkEntity(entity, entity.level());
    }
}