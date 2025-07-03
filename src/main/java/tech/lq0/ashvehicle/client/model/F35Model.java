package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.F16Entity;
import tech.lq0.ashvehicle.entity.F35Entity;

public class F35Model extends GeoModel<F35Entity> {

    @Override
    public ResourceLocation getAnimationResource(F35Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(F35Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/f-35.geo.json");}

    @Override
    public ResourceLocation getTextureResource(F35Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/f-35.png");}
}