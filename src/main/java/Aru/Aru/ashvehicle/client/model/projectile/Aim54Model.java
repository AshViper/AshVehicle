package Aru.Aru.ashvehicle.client.model.projectile;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.Aim54Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Aim54Model extends GeoModel<Aim54Entity> {
    public Aim54Model() {
    }

    public ResourceLocation getAnimationResource(Aim54Entity entity) {
        return null;
    }

    public ResourceLocation getModelResource(Aim54Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/aim54.geo.json");
    }

    public ResourceLocation getTextureResource(Aim54Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/aim54.png");
    }
}