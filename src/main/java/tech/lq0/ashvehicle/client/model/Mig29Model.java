package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.Mig29Entity;

public class Mig29Model extends GeoModel<Mig29Entity> {

    @Override
    public ResourceLocation getAnimationResource(Mig29Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(Mig29Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/mig-29.geo.json");}

    @Override
    public ResourceLocation getTextureResource(Mig29Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/mig-29.png");}
}