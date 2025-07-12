package Aru.Aru.ashvehicle.client.model.vehicle;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.vehicle.Tom7Entity;

public class Tom7Model extends GeoModel<Tom7Entity> {

    @Override
    public ResourceLocation getAnimationResource(Tom7Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(Tom7Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/tom_7.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Tom7Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/tom_7.png");
    }
}
