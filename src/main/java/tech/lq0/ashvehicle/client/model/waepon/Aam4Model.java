package tech.lq0.ashvehicle.client.model.waepon;


import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.weapon.Aam4Entity;

public class Aam4Model extends GeoModel<Aam4Entity> {
    public Aam4Model() {
    }

    public ResourceLocation getAnimationResource(Aam4Entity entity) {
        return null;
    }

    public ResourceLocation getModelResource(Aam4Entity entity) {
        return new ResourceLocation(ExtensionTest.MODID, "geo/aam-4.geo.json");
    }

    public ResourceLocation getTextureResource(Aam4Entity entity) {
        return new ResourceLocation(ExtensionTest.MODID, "textures/weapon/aam-4.png");
    }
}
