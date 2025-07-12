package Aru.Aru.ashvehicle.client.model.waepon;


import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.weapon.Aam4Entity;

public class Aam4Model extends GeoModel<Aam4Entity> {
    public Aam4Model() {
    }

    public ResourceLocation getAnimationResource(Aam4Entity entity) {
        return null;
    }

    public ResourceLocation getModelResource(Aam4Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "geo/aam-4.geo.json");
    }

    public ResourceLocation getTextureResource(Aam4Entity entity) {
        return new ResourceLocation(AshVehicle.MODID, "textures/weapon/aam-4.png");
    }
}
