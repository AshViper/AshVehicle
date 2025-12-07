package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class V22Model extends VehicleModel<V22Entity> {
    public V22Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<V22Entity> collectTransform(String boneName) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(V22Entity object) {
        return new ResourceLocation("ashvehicle", "geo/v-22.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(V22Entity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/v-22.png");
    }

    @Override
    public ResourceLocation getAnimationResource(V22Entity animatable) {
        return new ResourceLocation("ashvehicle", "animations/v-22.animation.json");
    }
}