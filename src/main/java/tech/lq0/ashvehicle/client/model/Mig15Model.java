package tech.lq0.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.Mig15Entity;

public class Mig15Model extends GeoModel<Mig15Entity> {

    @Override
    public ResourceLocation getAnimationResource(Mig15Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(Mig15Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/mig-15.geo.json");}

    @Override
    public ResourceLocation getTextureResource(Mig15Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/mig-15.png");}
}