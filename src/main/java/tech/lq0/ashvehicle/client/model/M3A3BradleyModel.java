package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.M3A3BradleyEntity;
import tech.lq0.ashvehicle.entity.T90Entity;

public class M3A3BradleyModel extends GeoModel<M3A3BradleyEntity> {

    @Override
    public ResourceLocation getAnimationResource(M3A3BradleyEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(M3A3BradleyEntity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/m3a3-bradley.geo.json");}

    @Override
    public ResourceLocation getTextureResource(M3A3BradleyEntity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/m3a3-bradley_enhanced.png");}
}