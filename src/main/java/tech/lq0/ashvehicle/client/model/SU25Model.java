package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.SU25Entity;
import tech.lq0.ashvehicle.entity.SU33Entity;

public class SU25Model extends GeoModel<SU25Entity> {

    @Override
    public ResourceLocation getAnimationResource(SU25Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(SU25Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/su-25.geo.json");}

    @Override
    public ResourceLocation getTextureResource(SU25Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/su-25.png");}
}