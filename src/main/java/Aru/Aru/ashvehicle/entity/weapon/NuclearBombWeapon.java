package Aru.Aru.ashvehicle.entity.weapon;

import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import Aru.Aru.ashvehicle.ExtensionTest;

public class NuclearBombWeapon extends VehicleWeapon {
    public NuclearBombWeapon() {
        this.icon = new ResourceLocation(ExtensionTest.MODID, "textures/icon/weapon/gbu-57-icon.png");
    }

    public NuclearBombEntity create(LivingEntity entity) {
        return new NuclearBombEntity(entity, entity.level());
    }
}