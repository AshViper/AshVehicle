package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.T90Entity;

public class T90Model extends GeoModel<T90Entity> {

    @Override
    public ResourceLocation getAnimationResource(T90Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(T90Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/t-90.geo.json");}

    @Override
    public ResourceLocation getTextureResource(T90Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/t-90.png");}
}