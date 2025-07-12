package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import Aru.Aru.ashvehicle.entity.vehicle.M1A1AbramsEntity;

public class M1A1AbramsModel extends GeoModel<M1A1AbramsEntity> {

    @Override
    public ResourceLocation getAnimationResource(M1A1AbramsEntity animatable) {
        return new ResourceLocation(AshVehicle.MODID, "animations/m1a1abrams.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(M1A1AbramsEntity object) {
        return new ResourceLocation(AshVehicle.MODID, "geo/m1a1abrams.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(M1A1AbramsEntity object) {
        return new ResourceLocation(AshVehicle.MODID, "textures/entity/m1a1abrams.png");
    }
} 