package Aru.Aru.ashvehicle.client.model.vehicle;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.ExtensionTest;
import Aru.Aru.ashvehicle.entity.vehicle.GepardEntity;

public class GepardModel extends GeoModel<GepardEntity> {

    @Override
    public ResourceLocation getAnimationResource(GepardEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(GepardEntity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/gepard-1a2.geo.json");}

    @Override
    public ResourceLocation getTextureResource(GepardEntity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/gepard-1a2.png");}
}