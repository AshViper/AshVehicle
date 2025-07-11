package Aru.Aru.ashvehicle.client.model.waepon;


import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.ExtensionTest;
import Aru.Aru.ashvehicle.entity.weapon.JassmXREntity;

public class JassmXRModel extends GeoModel<JassmXREntity> {
    public JassmXRModel() {
    }

    public ResourceLocation getAnimationResource(JassmXREntity entity) {
        return new ResourceLocation(ExtensionTest.MODID, "animations/jassm-xr.animation.json");
    }

    public ResourceLocation getModelResource(JassmXREntity entity) {
        return new ResourceLocation(ExtensionTest.MODID, "geo/jassm-xr.geo.json");
    }

    public ResourceLocation getTextureResource(JassmXREntity entity) {
        return new ResourceLocation(ExtensionTest.MODID, "textures/weapon/jassm-xr.png");
    }
}
