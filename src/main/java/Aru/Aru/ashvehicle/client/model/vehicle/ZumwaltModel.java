package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.ZumwaltEntity;

public class ZumwaltModel extends GeoModel<ZumwaltEntity> {

    @Override
    public ResourceLocation getAnimationResource(ZumwaltEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(ZumwaltEntity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/zumwalt.geo.json");}

    @Override
    public ResourceLocation getTextureResource(ZumwaltEntity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/zumwalt.png");}
}
