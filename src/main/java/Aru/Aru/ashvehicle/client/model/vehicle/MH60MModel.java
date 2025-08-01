package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.MH60MEntity;

public class MH60MModel extends GeoModel<MH60MEntity> {

        @Override
        public ResourceLocation getAnimationResource(MH60MEntity entity) {
            return null;
        }

        @Override
        public ResourceLocation getModelResource(MH60MEntity entity) {return new ResourceLocation(AshVehicle.MODID,"geo/mh-60m.geo.json");}

        @Override
        public ResourceLocation getTextureResource(MH60MEntity entity) {return new ResourceLocation(AshVehicle.MODID,"textures/entity/mh-60m.png");}
    }
