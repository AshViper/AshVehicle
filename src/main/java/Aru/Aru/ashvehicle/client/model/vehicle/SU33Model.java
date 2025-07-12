package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.SU33Entity;

public class SU33Model extends GeoModel<SU33Entity> {

    @Override
    public ResourceLocation getAnimationResource(SU33Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(SU33Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/su-33.geo.json");}

    @Override
    public ResourceLocation getTextureResource(SU33Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/su-33.png");}
}