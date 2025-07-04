package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.T90Entity;

public class T90Model extends GeoModel<T90Entity> {

    @Override
    public ResourceLocation getAnimationResource(T90Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(T90Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/t-90.geo.json");}

    @Override
    public ResourceLocation getTextureResource(T90Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/t-90.png");}
}