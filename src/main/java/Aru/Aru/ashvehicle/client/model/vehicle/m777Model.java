package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class m777Model extends VehicleModel<m777Entity> {
    public m777Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<m777Entity> collectTransform(String boneName) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(m777Entity object) {
        return new ResourceLocation("ashvehicle", "geo/m_777.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(m777Entity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/m_777.png");
    }

    @Override
    public ResourceLocation getAnimationResource(m777Entity animatable) {
        return new ResourceLocation("ashvehicle", "animations/m_777.animation.json");
    }
}