package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.F39EEntity;

public class F39EModel extends GeoModel<F39EEntity> {

    @Override
    public ResourceLocation getAnimationResource(F39EEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(F39EEntity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/f-39e.geo.json");}

    @Override
    public ResourceLocation getTextureResource(F39EEntity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/f-39e.png");}
}