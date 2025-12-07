package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class SU34Model extends VehicleModel<SU34Entity> {
    public SU34Model() {
    }

    @Nullable
    public VehicleModel.TransformContext<SU34Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext var1000;
        switch (boneName){
            case "gearL":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F));
                break;
            case "gearR":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * (-(float)Math.PI / 180F));
                break;
            case "LGDRearLeft", "LGDFrontRight":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * (-(float)Math.PI / 180F) + 1.5f);
                break;
            case "LGDRearRight", "gearFL":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F) - 1.5f);
                break;
            case "gearF":
                var1000 = (bone, vehicle, state) -> bone.setRotX(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F));
                break;
            case "flapL":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2LRotO, vehicle.getFlap2LRot()) * ((float)Math.PI / 180F));
                break;
            case "flapR":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2RRotO, vehicle.getFlap2RRot()) * ((float)Math.PI / 180F));
                break;
            case "FlapLB":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap1RRotO, vehicle.getFlap1RRot()) * (-(float)Math.PI / 180F));
                break;
            case "FlapRB":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap1LRotO, vehicle.getFlap1LRot()) * (-(float)Math.PI / 180F));
                break;
            case "flapRV":
                var1000 = (bone, vehicle, state) -> bone.setRotY(Mth.clamp(Mth.lerp(state.getPartialTick(), vehicle.flap3RotO, vehicle.getFlap3Rot()), -20.0F, 20.0F) * ((float)Math.PI / 180F));
                break;
            default :
                var1000 = null;
                break;
        }
        return var1000;
    }

    @Override
    public ResourceLocation getModelResource(SU34Entity object) {
        return new ResourceLocation("ashvehicle", "geo/su-34.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SU34Entity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/su-34.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SU34Entity animatable) {
        return new ResourceLocation("ashvehicle", "animations/su-34.animation.json");
    }
}