package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.F4Entity;

public class F4Model extends GeoModel<F4Entity> {

    @Override
    public ResourceLocation getAnimationResource(F4Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(F4Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/f-4.geo.json");}

    @Override
    public ResourceLocation getTextureResource(F4Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/f-4.png");}
}