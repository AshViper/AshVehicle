package Aru.Aru.ashvehicle.client.model.projectile;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.Cbu87Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Cbu87Model extends GeoModel<Cbu87Entity> {
    public Cbu87Model() {
    }

    public ResourceLocation getAnimationResource(Cbu87Entity entity) {
        return null;
    }

    public ResourceLocation getModelResource(Cbu87Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/cbu87.geo.json");
    }

    public ResourceLocation getTextureResource(Cbu87Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/cbu87.png");
    }
}