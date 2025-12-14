package Aru.Aru.ashvehicle.client.model.projectile;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.Gbu57Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Gbu57Model extends GeoModel<Gbu57Entity> {
    public Gbu57Model() {
    }

    public ResourceLocation getAnimationResource(Gbu57Entity entity) {
        return null;
    }

    public ResourceLocation getModelResource(Gbu57Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/gbu57.geo.json");
    }

    public ResourceLocation getTextureResource(Gbu57Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/gbu57.png");
    }
}