package tech.lq0.ashvehicle.client.model.waepon;


import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.weapon.GBU57Entity;
import tech.lq0.ashvehicle.entity.weapon.NuclearBombEntity;

public class NuclearBombModel extends GeoModel<NuclearBombEntity> {
    public NuclearBombModel() {
    }

    public ResourceLocation getAnimationResource(NuclearBombEntity entity) {
        return null;
    }

    public ResourceLocation getModelResource(NuclearBombEntity entity) {
        return new ResourceLocation(ExtensionTest.MODID, "geo/gbu-57.geo.json");
    }

    public ResourceLocation getTextureResource(NuclearBombEntity entity) {
        return new ResourceLocation(ExtensionTest.MODID, "textures/weapon/gbu-57.png");
    }
}
