package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.F35Entity;

public class F35Model extends GeoModel<F35Entity> {

    @Override
    public ResourceLocation getAnimationResource(F35Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(F35Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/f-35.geo.json");}

    @Override
    public ResourceLocation getTextureResource(F35Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/f-35.png");}
}