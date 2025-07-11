package tech.lq0.ashvehicle.entity.weapon;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tech.lq0.ashvehicle.ExtensionTest;

public class GBU57Weapon extends VehicleWeapon {
    public GBU57Weapon() {
        this.icon = new ResourceLocation(ExtensionTest.MODID, "textures/icon/weapon/gbu-57-icon.png");
    }

    public GBU57Entity create(LivingEntity entity) {
        return new GBU57Entity(entity, entity.level());
    }
}