package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.F4Entity;

public class F4Model extends GeoModel<F4Entity> {

    @Override
    public ResourceLocation getAnimationResource(F4Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(F4Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/f-4.geo.json");}

    @Override
    public ResourceLocation getTextureResource(F4Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/f-4.png");}
}