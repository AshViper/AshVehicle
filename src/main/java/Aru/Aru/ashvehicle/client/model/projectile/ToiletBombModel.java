package Aru.Aru.ashvehicle.client.model.projectile;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.ToiletBombEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ToiletBombModel extends GeoModel<ToiletBombEntity> {
    public ToiletBombModel() {
    }

    public ResourceLocation getAnimationResource(ToiletBombEntity entity) {
        return null;
    }

    public ResourceLocation getModelResource(ToiletBombEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(AshVehicle.MODID, "geo/toiletbomb.geo.json");
    }

    public ResourceLocation getTextureResource(ToiletBombEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(AshVehicle.MODID, "textures/entity/toiletbomb.png");
    }
}