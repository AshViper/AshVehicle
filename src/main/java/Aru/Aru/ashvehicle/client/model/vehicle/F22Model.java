package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.F22Entity;

public class F22Model extends GeoModel<F22Entity> {

    @Override
    public ResourceLocation getAnimationResource(F22Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(F22Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/f-22.geo.json");}

    @Override
    public ResourceLocation getTextureResource(F22Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/f-22.png");}
}