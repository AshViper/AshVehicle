package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ZumwaltModel extends VehicleModel<ZumwaltEntity> {
    public ZumwaltModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<ZumwaltEntity> collectTransform(String boneName) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(ZumwaltEntity object) {
        return ResourceLocation.parse("ashvehicle:geo/zumwalt.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ZumwaltEntity object) {
        return ResourceLocation.parse("ashvehicle:textures/entity/zumwalt.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ZumwaltEntity animatable) {
        return ResourceLocation.parse("ashvehicle:animations/zumwalt.animation.json");
    }
}