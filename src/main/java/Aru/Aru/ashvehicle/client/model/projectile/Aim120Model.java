package Aru.Aru.ashvehicle.client.model.projectile;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.*;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Aim120Model extends GeoModel<Aim120Entity> {
    public Aim120Model() {
    }

    public ResourceLocation getAnimationResource(Aim120Entity entity) {
        return null;
    }

    public ResourceLocation getModelResource(Aim120Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/aim120.geo.json");
    }

    public ResourceLocation getTextureResource(Aim120Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/aim120.png");
    }
}