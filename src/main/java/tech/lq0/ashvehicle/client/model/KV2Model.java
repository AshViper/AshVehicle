package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.KV2Entity;
import tech.lq0.ashvehicle.entity.T90Entity;

public class KV2Model extends GeoModel<KV2Entity> {

    @Override
    public ResourceLocation getAnimationResource(KV2Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(KV2Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/kv-2.geo.json");}

    @Override
    public ResourceLocation getTextureResource(KV2Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/kv-2.png");}
}