package Aru.Aru.ashvehicle.client.model.projectile;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.*;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Aim9Model extends GeoModel<Aim9Entity> {
    public Aim9Model() {
    }

    public ResourceLocation getAnimationResource(Aim9Entity entity) {
        return null;
    }

    public ResourceLocation getModelResource(Aim9Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/aim9.geo.json");
    }

    public ResourceLocation getTextureResource(Aim9Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/aim9.png");
    }
}