package Aru.Aru.ashvehicle.client.model.projectile;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.NukeBombEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NukeBombModel extends GeoModel<NukeBombEntity> {
    public NukeBombModel() {
    }

    public ResourceLocation getAnimationResource(NukeBombEntity entity) {
        return null;
    }

    public ResourceLocation getModelResource(NukeBombEntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/nuclearbomb.geo.json");
    }

    public ResourceLocation getTextureResource(NukeBombEntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/nuclearbomb.png");
    }
}
