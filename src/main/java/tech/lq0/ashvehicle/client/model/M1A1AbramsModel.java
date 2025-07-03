package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.M1A1AbramsEntity;

public class M1A1AbramsModel extends GeoModel<M1A1AbramsEntity> {

    @Override
    public ResourceLocation getAnimationResource(M1A1AbramsEntity animatable) {
        return new ResourceLocation(ExtensionTest.MODID, "animations/m1a1abrams.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(M1A1AbramsEntity object) {
        return new ResourceLocation(ExtensionTest.MODID, "geo/m1a1abrams.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(M1A1AbramsEntity object) {
        return new ResourceLocation(ExtensionTest.MODID, "textures/entity/m1a1abrams.png");
    }
} 