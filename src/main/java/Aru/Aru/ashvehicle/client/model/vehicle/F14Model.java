package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.client.model.VehicleModel;
import Aru.Aru.ashvehicle.entity.vehicle.F14Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class F14Model extends VehicleModel<F14Entity> {
    public F14Model() {
    }

    @Nullable
    public VehicleModel.TransformContext<F14Entity> collectTransform(String boneName) {
        TransformContext var1000;
        switch (boneName){
            case "LGear", "Rgear", "FGear":
                var1000 = (bone, vehicle, state) -> bone.setRotX(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F));
                break;
            case "LFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2LRotO, vehicle.getFlap2LRot()) * ((float)Math.PI / 180F));
                break;
            case "RFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2RRotO, vehicle.getFlap2RRot()) * ((float)Math.PI / 180F));
                break;
            case "BLFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2LRotO, vehicle.getFlap2LRot()) * ((float)Math.PI / 180F));
                break;
            case "BRFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2RRotO, vehicle.getFlap2RRot()) * ((float)Math.PI / 180F));
                break;
            case "VBLFlap", "VBRFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotY(Mth.clamp(Mth.lerp(state.getPartialTick(), vehicle.flap3RotO, vehicle.getFlap3Rot()), -20.0F, 20.0F) * ((float)Math.PI / 180F));
                break;
            default :
                var1000 = null;
                break;
        }
        return var1000;
    }

    @Override
    public ResourceLocation getModelResource(F14Entity object) {
        return new ResourceLocation("ashvehicle", "geo/f14.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(F14Entity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/f14.png");
    }

    @Override
    public ResourceLocation getAnimationResource(F14Entity animatable) {
        return new ResourceLocation("ashvehicle", "animations/f14.animation.json");
    }
}