package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class Tom7Model extends VehicleModel<Tom7Entity> {
    public Tom7Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<Tom7Entity> collectTransform(String boneName) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(Tom7Entity object) {
        return new ResourceLocation("ashvehicle", "geo/tom_7.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Tom7Entity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/tom_7.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Tom7Entity animatable) {
        return new ResourceLocation("ashvehicle", "animations/tom_7.animation.json");
    }
}
