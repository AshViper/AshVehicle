package Aru.Aru.ashvehicle.client.model.projectile;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.Agm158Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Agm158Model extends GeoModel<Agm158Entity> {
    public Agm158Model() {
    }

    public ResourceLocation getAnimationResource(Agm158Entity entity) {
        return null;
    }

    public ResourceLocation getModelResource(Agm158Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/agm158.geo.json");
    }

    public ResourceLocation getTextureResource(Agm158Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/agm158.png");
    }
}