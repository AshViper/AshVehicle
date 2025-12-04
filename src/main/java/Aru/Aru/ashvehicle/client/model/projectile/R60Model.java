package Aru.Aru.ashvehicle.client.model.projectile;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.*;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class R60Model extends GeoModel<R60Entity> {
    public R60Model() {
    }

    public ResourceLocation getAnimationResource(R60Entity entity) {
        return null;
    }

    public ResourceLocation getModelResource(R60Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/r60.geo.json");
    }

    public ResourceLocation getTextureResource(R60Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/r60.png");
    }
}