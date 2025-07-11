package Aru.Aru.ashvehicle.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.ExtensionTest;
import Aru.Aru.ashvehicle.entity.MH60MEntity;

public class MH60MModel extends GeoModel<MH60MEntity> {

        @Override
        public ResourceLocation getAnimationResource(MH60MEntity entity) {
            return null;
        }

        @Override
        public ResourceLocation getModelResource(MH60MEntity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/mh-60m.geo.json");}

        @Override
        public ResourceLocation getTextureResource(MH60MEntity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/mh-60m.png");}
    }
