package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.SapsanEntity;

public class SapsanModel extends GeoModel<SapsanEntity> {
    public SapsanModel() {
    }

    public ResourceLocation getAnimationResource(SapsanEntity entity) {
        return null;
    }

    public ResourceLocation getModelResource(SapsanEntity entity) {
        return new ResourceLocation(ExtensionTest.MODID,"geo/sapsan-grim-2.geo.json");
    }

    public ResourceLocation getTextureResource(SapsanEntity entity) {
        return new ResourceLocation(ExtensionTest.MODID,"textures/entity/sapsan-grim-2.png");
    }
}
