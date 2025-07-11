package Aru.Aru.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.ExtensionTest;
import Aru.Aru.ashvehicle.entity.SU33Entity;

public class SU33Model extends GeoModel<SU33Entity> {

    @Override
    public ResourceLocation getAnimationResource(SU33Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(SU33Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/su-33.geo.json");}

    @Override
    public ResourceLocation getTextureResource(SU33Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/su-33.png");}
}