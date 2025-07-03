package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.SU33Entity;
import tech.lq0.ashvehicle.entity.SU34Entity;

public class SU34Model extends GeoModel<SU34Entity> {

    @Override
    public ResourceLocation getAnimationResource(SU34Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(SU34Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/su-34.geo.json");}

    @Override
    public ResourceLocation getTextureResource(SU34Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/su-34.png");}
}