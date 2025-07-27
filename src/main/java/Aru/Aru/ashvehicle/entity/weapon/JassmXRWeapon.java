package Aru.Aru.ashvehicle.entity.weapon;

import Aru.Aru.ashvehicle.AshVehicle;
import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class JassmXRWeapon  extends VehicleWeapon {
    public float damage = 140.0F;
    public float explosionDamage = 60.0F;
    public float explosionRadius = 5.0F;

    public JassmXRWeapon() {
        this.icon = new ResourceLocation(AshVehicle.MODID, "textures/icon/weapon/aam4-icon.png");
    }

    public JassmXRWeapon damage(float damage) {
        this.damage = damage;
        return this;
    }

    public JassmXRWeapon explosionDamage(float explosionDamage) {
        this.explosionDamage = explosionDamage;
        return this;
    }

    public JassmXRWeapon explosionRadius(float explosionRadius) {
        this.explosionRadius = explosionRadius;
        return this;
    }

    public JassmXREntity create(LivingEntity entity) {
        return new JassmXREntity(entity, entity.level(), this.damage, this.explosionDamage, this.explosionRadius);
    }
}
