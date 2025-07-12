package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.Mig29Entity;

public class Mig29Model extends GeoModel<Mig29Entity> {

    @Override
    public ResourceLocation getAnimationResource(Mig29Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(Mig29Entity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/mig-29.geo.json");}

    @Override
    public ResourceLocation getTextureResource(Mig29Entity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/mig-29.png");}
}