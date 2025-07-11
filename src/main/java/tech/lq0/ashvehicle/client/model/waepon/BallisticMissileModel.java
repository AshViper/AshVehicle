package tech.lq0.ashvehicle.client.model.waepon;


import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.weapon.Aam4Entity;
import tech.lq0.ashvehicle.entity.weapon.BallisticMissileEntity;

public class BallisticMissileModel extends GeoModel<BallisticMissileEntity> {
    public BallisticMissileModel() {
    }

    public ResourceLocation getAnimationResource(BallisticMissileEntity entity) {
        return null;
    }

    public ResourceLocation getModelResource(BallisticMissileEntity entity) {
        return new ResourceLocation(ExtensionTest.MODID, "geo/aam-4.geo.json");
    }

    public ResourceLocation getTextureResource(BallisticMissileEntity entity) {
        return new ResourceLocation(ExtensionTest.MODID, "textures/weapon/aam-4.png");
    }
}
