package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.F22Entity;
import tech.lq0.ashvehicle.entity.F35Entity;

public class F22Model extends GeoModel<F22Entity> {

    @Override
    public ResourceLocation getAnimationResource(F22Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(F22Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/f-22.geo.json");}

    @Override
    public ResourceLocation getTextureResource(F22Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/f-22.png");}
}