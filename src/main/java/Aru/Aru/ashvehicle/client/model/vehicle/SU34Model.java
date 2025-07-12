package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.SU34Entity;

public class SU34Model extends GeoModel<SU34Entity> {

    @Override
    public ResourceLocation getAnimationResource(SU34Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(SU34Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/su-34.geo.json");}

    @Override
    public ResourceLocation getTextureResource(SU34Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/su-34.png");}
}