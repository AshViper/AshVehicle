package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SapsanModel extends VehicleModel<SapsanEntity> {
    public SapsanModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<SapsanEntity> collectTransform(String boneName) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(SapsanEntity object) {
        return ResourceLocation.parse("ashvehicle:geo/sapsan-grim2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SapsanEntity object) {
        return ResourceLocation.parse("ashvehicle:textures/entity/sapsan-grim2.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SapsanEntity animatable) {
        return ResourceLocation.parse("ashvehicle:animations/sapsan-grim2.animation.json");
    }
}
