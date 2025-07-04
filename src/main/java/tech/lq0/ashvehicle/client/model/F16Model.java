package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.F16Entity;

public class F16Model extends GeoModel<F16Entity> {

    @Override
    public ResourceLocation getAnimationResource(F16Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(F16Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/f-16.geo.json");}

    @Override
    public ResourceLocation getTextureResource(F16Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/f-16.png");}
}