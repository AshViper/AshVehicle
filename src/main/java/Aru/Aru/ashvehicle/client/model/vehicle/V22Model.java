package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.vehicle.T90Entity;
import Aru.Aru.ashvehicle.entity.vehicle.V22Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class V22Model extends GeoModel<V22Entity> {

    @Override
    public ResourceLocation getAnimationResource(V22Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(V22Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/v-22.geo.json");}

    @Override
    public ResourceLocation getTextureResource(V22Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/v-22.png");}
}