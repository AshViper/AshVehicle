package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.SU57Entity;

public class SU57Model extends GeoModel<SU57Entity> {

    @Override
    public ResourceLocation getAnimationResource(SU57Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(SU57Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/su-57.geo.json");}

    @Override
    public ResourceLocation getTextureResource(SU57Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/su-57.png");}
}