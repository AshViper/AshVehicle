package Aru.Aru.ashvehicle.client.model.projectile;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.*;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Agm114Model extends GeoModel<Agm114Entity> {
    public Agm114Model() {
    }

    public ResourceLocation getAnimationResource(Agm114Entity entity) {
        return null;
    }

    public ResourceLocation getModelResource(Agm114Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/agm114.geo.json");
    }

    public ResourceLocation getTextureResource(Agm114Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/agm114.png");
    }
}