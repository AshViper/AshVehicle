package Aru.Aru.ashvehicle.client.model.vehicle;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.ExtensionTest;
import Aru.Aru.ashvehicle.entity.vehicle.SapsanEntity;

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
