package Aru.Aru.ashvehicle.client.model.vehicle;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.ExtensionTest;
import Aru.Aru.ashvehicle.entity.vehicle.UH60Entity;

public class UH60Model extends GeoModel<UH60Entity> {

        @Override
        public ResourceLocation getAnimationResource(UH60Entity entity) {
            return null;
        }

        @Override
        public ResourceLocation getModelResource(UH60Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"geo/uh-60.geo.json");}

        @Override
        public ResourceLocation getTextureResource(UH60Entity entity) {return new ResourceLocation(ExtensionTest.MODID,"textures/entity/uh-60.png");}
    }
