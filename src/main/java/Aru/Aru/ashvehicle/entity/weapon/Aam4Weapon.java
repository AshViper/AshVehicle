package Aru.Aru.ashvehicle.entity.weapon;

import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import Aru.Aru.ashvehicle.ExtensionTest;

public class Aam4Weapon extends VehicleWeapon {
    public Aam4Weapon() {
        this.icon = new ResourceLocation(ExtensionTest.MODID, "textures/icon/weapon/aam4-icon.png");
    }

    public Aam4Entity create(LivingEntity entity) {
        return new Aam4Entity(entity, entity.level());
    }
}