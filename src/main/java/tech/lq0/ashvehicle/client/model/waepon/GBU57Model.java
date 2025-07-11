package tech.lq0.ashvehicle.client.model.waepon;


import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.weapon.Aam4Entity;
import tech.lq0.ashvehicle.entity.weapon.GBU57Entity;

public class GBU57Model extends GeoModel<GBU57Entity> {
    public GBU57Model() {
    }

    public ResourceLocation getAnimationResource(GBU57Entity entity) {
        return null;
    }

    public ResourceLocation getModelResource(GBU57Entity entity) {
        return new ResourceLocation(ExtensionTest.MODID, "geo/gbu-57.geo.json");
    }

    public ResourceLocation getTextureResource(GBU57Entity entity) {
        return new ResourceLocation(ExtensionTest.MODID, "textures/weapon/gbu-57.png");
    }
}
