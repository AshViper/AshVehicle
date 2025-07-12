package Aru.Aru.ashvehicle.client.model.waepon;


import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.weapon.JassmXREntity;

public class JassmXRModel extends GeoModel<JassmXREntity> {
    public JassmXRModel() {
    }

    public ResourceLocation getAnimationResource(JassmXREntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "animations/jassm-xr.animation.json");
    }

    public ResourceLocation getModelResource(JassmXREntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/jassm-xr.geo.json");
    }

    public ResourceLocation getTextureResource(JassmXREntity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/weapon/jassm-xr.png");
    }
}
