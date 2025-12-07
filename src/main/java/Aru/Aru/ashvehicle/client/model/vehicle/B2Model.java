package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class B2Model extends VehicleModel<B2Entity> {
    public B2Model() {}

    @Nullable
    public VehicleModel.TransformContext<B2Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext var1000;
        switch (boneName){
            case "Left_Gear", "Left_Door":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F));
                break;
            case "Right_Gear", "Right_Door":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * (-(float)Math.PI / 180F));
                break;
            case "Front_Gear", "Front_Door":
                var1000 = (bone, vehicle, state) -> bone.setRotX(vehicle.gearRot(state.getPartialTick()) * (-(float)Math.PI / 180F));
                break;
            default :
                var1000 = null;
                break;
        }
        return var1000;
    }

    @Override
    public ResourceLocation getModelResource(B2Entity object) {
        return new ResourceLocation("ashvehicle", "geo/b-2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(B2Entity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/b-2.png");
    }

    @Override
    public ResourceLocation getAnimationResource(B2Entity animatable) {
        return new ResourceLocation("ashvehicle", "animations/b-2.animation.json");
    }
}