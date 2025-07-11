package tech.lq0.ashvehicle.client.model.waepon;


import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.weapon.Aam4Entity;
import tech.lq0.ashvehicle.entity.weapon.JassmXREntity;

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
