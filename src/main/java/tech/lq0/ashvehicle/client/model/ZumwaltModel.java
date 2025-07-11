package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.B2Entity;
import tech.lq0.ashvehicle.entity.ZumwaltEntity;

public class ZumwaltModel extends GeoModel<ZumwaltEntity> {

    @Override
    public ResourceLocation getAnimationResource(ZumwaltEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(ZumwaltEntity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/zumwalt.geo.json");}

    @Override
    public ResourceLocation getTextureResource(ZumwaltEntity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/zumwalt.png");}
}
