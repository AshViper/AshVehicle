package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.F117Entity;

public class F117Model extends GeoModel<F117Entity> {

    @Override
    public ResourceLocation getAnimationResource(F117Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(F117Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/f-117.geo.json");}

    @Override
    public ResourceLocation getTextureResource(F117Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/f-117tex.png");}
}