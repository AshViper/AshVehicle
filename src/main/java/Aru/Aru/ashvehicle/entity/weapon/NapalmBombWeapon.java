package Aru.Aru.ashvehicle.entity.weapon;

import Aru.Aru.ashvehicle.ExtensionTest;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class NapalmBombWeapon extends VehicleWeapon {
    public NapalmBombWeapon() {
        this.icon = new ResourceLocation(ExtensionTest.MODID, "textures/icon/weapon/gbu-57-icon.png");
    }

    public NapalmBombEntity create(LivingEntity entity) {
        return new NapalmBombEntity(entity, entity.level());
    }
}