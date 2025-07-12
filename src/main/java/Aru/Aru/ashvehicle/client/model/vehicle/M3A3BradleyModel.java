package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.M3A3BradleyEntity;

public class M3A3BradleyModel extends GeoModel<M3A3BradleyEntity> {

    @Override
    public ResourceLocation getAnimationResource(M3A3BradleyEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(M3A3BradleyEntity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/m3a3-bradley.geo.json");}

    @Override
    public ResourceLocation getTextureResource(M3A3BradleyEntity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/m3a3-bradley_enhanced.png");}
}