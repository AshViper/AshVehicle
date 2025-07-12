package Aru.Aru.ashvehicle.client.model.vehicle;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.ExtensionTest;
import Aru.Aru.ashvehicle.entity.vehicle.MQ9Entity;

public class MQ9Model extends GeoModel<MQ9Entity> {

    @Override
    public ResourceLocation getAnimationResource(MQ9Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(MQ9Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/mq-9.geo.json");}

    @Override
    public ResourceLocation getTextureResource(MQ9Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/mq-9.png");}
}