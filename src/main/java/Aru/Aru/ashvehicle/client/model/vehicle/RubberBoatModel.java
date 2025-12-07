package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RubberBoatModel extends VehicleModel<RubberBoatEntity> {
    public RubberBoatModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<RubberBoatEntity> collectTransform(String boneName) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(RubberBoatEntity object) {
        return new ResourceLocation("ashvehicle", "geo/rubber_boat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RubberBoatEntity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/rubber_boat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RubberBoatEntity animatable) {
        return new ResourceLocation("ashvehicle", "animations/rubber_boat.animation.json");
    }
}