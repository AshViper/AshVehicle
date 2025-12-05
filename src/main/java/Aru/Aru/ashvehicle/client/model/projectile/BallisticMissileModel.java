package Aru.Aru.ashvehicle.client.model.projectile;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.Aim9Entity;
import Aru.Aru.ashvehicle.entity.projectile.BallisticMissileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BallisticMissileModel extends GeoModel<BallisticMissileEntity> {
    public BallisticMissileModel() {
    }

    public ResourceLocation getAnimationResource(BallisticMissileEntity entity) {
        return null;
    }

    public ResourceLocation getModelResource(BallisticMissileEntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/aim9.geo.json");
    }

    public ResourceLocation getTextureResource(BallisticMissileEntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/aim9.png");
    }
}
