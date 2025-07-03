package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.F16Entity;
import tech.lq0.ashvehicle.entity.F18Entity;

public class F18Model extends GeoModel<F18Entity> {

    @Override
    public ResourceLocation getAnimationResource(F18Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(F18Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/hornet.geo.json");}

    @Override
    public ResourceLocation getTextureResource(F18Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/hornet.png");}
}