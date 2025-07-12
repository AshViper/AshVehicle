package Aru.Aru.ashvehicle.client.model.vehicle;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.vehicle.SU25Entity;

public class SU25Model extends GeoModel<SU25Entity> {

    @Override
    public ResourceLocation getAnimationResource(SU25Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(SU25Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/su-25.geo.json");}

    @Override
    public ResourceLocation getTextureResource(SU25Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/su-25.png");}
}