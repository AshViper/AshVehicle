package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class SU27Model extends VehicleModel<SU27Entity> {
    public SU27Model() {
    }

    @Nullable
    public VehicleModel.TransformContext<SU27Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext var1000;
        switch (boneName){
            case "LeftTyre", "RightTyre":
                var1000 = (bone, vehicle, state) -> bone.setRotX(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F));
                break;
            case "Tyre":
                var1000 = (bone, vehicle, state) -> bone.setRotX(vehicle.gearRot(state.getPartialTick()) * (-(float)Math.PI / 180F));
                break;
            case "LeftTyreHatch" :
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F) - 1.57f);
                break;
            case "RightTyreHatch", "ForwardTyreHatch" :
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * (-(float)Math.PI / 180F) + 1.57f);
                break;
            case "LeftFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2LRotO, vehicle.getFlap2LRot()) * ((float)Math.PI / 180F));
                break;
            case "RightFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2RRotO, vehicle.getFlap2RRot()) * ((float)Math.PI / 180F));
                break;
            case "RightTailPlane":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2LRotO, vehicle.getFlap2LRot()) * ((float)Math.PI / 180F));
                break;
            case "LeftTailPlane":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2RRotO, vehicle.getFlap2RRot()) * ((float)Math.PI / 180F));
                break;
            case "RightVerticalTail", "LeftVerticalTail":
                var1000 = (bone, vehicle, state) -> bone.setRotY(Mth.clamp(Mth.lerp(state.getPartialTick(), vehicle.flap3RotO, vehicle.getFlap3Rot()), -20.0F, 20.0F) * ((float)Math.PI / 180F));
                break;
            default :
                var1000 = null;
                break;
        }
        return var1000;
    }

    @Override
    public ResourceLocation getModelResource(SU27Entity object) {
        return new ResourceLocation("ashvehicle", "geo/su-27.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SU27Entity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/su-27.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SU27Entity animatable) {
        return new ResourceLocation("ashvehicle", "animations/su-27.animation.json");
    }
}