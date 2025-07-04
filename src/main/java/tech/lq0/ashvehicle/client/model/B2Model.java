package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.B2Entity;
import tech.lq0.ashvehicle.entity.F4Entity;

public class B2Model extends GeoModel<B2Entity> {

    @Override
    public ResourceLocation getAnimationResource(B2Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(B2Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/b-2.geo.json");}

    @Override
    public ResourceLocation getTextureResource(B2Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/b-2.png");}
}