package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.F18Entity;

public class F18Model extends GeoModel<F18Entity> {

    @Override
    public ResourceLocation getAnimationResource(F18Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(F18Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/hornet.geo.json");}

    @Override
    public ResourceLocation getTextureResource(F18Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/hornet.png");}
}