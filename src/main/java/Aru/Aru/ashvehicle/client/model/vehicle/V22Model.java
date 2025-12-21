package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class V22Model extends VehicleModel<V22Entity> {
    public V22Model() {
    }

    @Nullable
    public VehicleModel.TransformContext<V22Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext var1000;
        switch (boneName){
            case "RightTyre":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F));
                break;
            case "LeftTyre":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * (-(float)Math.PI / 180F));
                break;
            case "LeftTyreHatch", "LeftForwardTyreHatch":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * (-(float)Math.PI / 180F) + 1.5f);
                break;
            case "RightTyreHatch", "RightForwardTyreHatch":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F) - 1.5f);
                break;
            case "Tyre":
                var1000 = (bone, vehicle, state) -> bone.setRotX(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F));
                break;
            case "LeftFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2LRotO, vehicle.getFlap2LRot()) * ((float)Math.PI / 180F));
                break;
            case "RightFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2RRotO, vehicle.getFlap2RRot()) * ((float)Math.PI / 180F));
                break;
            case "flapLB":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap1RRotO, vehicle.getFlap1RRot()) * ((float)Math.PI / 180F));
                break;
            case "flapRB":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap1LRotO, vehicle.getFlap1LRot()) * ((float)Math.PI / 180F));
                break;
            case "LeftAudder", "RightAudder":
                var1000 = (bone, vehicle, state) -> bone.setRotY(Mth.clamp(Mth.lerp(state.getPartialTick(), vehicle.flap3RotO, vehicle.getFlap3Rot()), -20.0F, 20.0F) * ((float)Math.PI / 180F));
                break;
            case "bone2":
                var1000 = (bone, vehicle, state) -> bone.setRotY(Mth.lerp(state.getPartialTick(), vehicle.propellerRotO, vehicle.getPropellerRot()));
                break;
            case "bone10":
                var1000 = (bone, vehicle, state) -> bone.setRotY(-Mth.lerp(state.getPartialTick(), vehicle.propellerRotO, vehicle.getPropellerRot()));
                break;
            default :
                var1000 = null;
                break;
        }
        return var1000;
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