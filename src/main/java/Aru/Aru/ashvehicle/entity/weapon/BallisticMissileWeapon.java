package Aru.Aru.ashvehicle.entity.weapon;

import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import Aru.Aru.ashvehicle.ExtensionTest;

public class BallisticMissileWeapon extends VehicleWeapon {
    public BallisticMissileWeapon() {
        this.icon = new ResourceLocation(ExtensionTest.MODID, "textures/icon/weapon/aam4-icon.png");
    }

    public BallisticMissileEntity create(LivingEntity entity)  {
        return new BallisticMissileEntity(entity, entity.level());
    }
}