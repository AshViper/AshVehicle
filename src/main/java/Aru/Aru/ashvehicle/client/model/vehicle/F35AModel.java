package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.client.model.VehicleModel;
import Aru.Aru.ashvehicle.entity.vehicle.F35AEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class F35AModel extends VehicleModel<F35AEntity> {
    public F35AModel() {
    }

    @Nullable
    public VehicleModel.TransformContext<F35AEntity> collectTransform(String boneName) {
        TransformContext var1000;
        switch (boneName){
            case "l_wheels2":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * (-(float)Math.PI / 180F));
                break;
            case "r_wheels2":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F));
                break;
            case "LGDRearLeft", "gearFL":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * (-(float)Math.PI / 180F) + 1.5f);
                break;
            case "LGDRearRight", "gearFR":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F) - 1.5f);
                break;
            case "fr_wheels":
                var1000 = (bone, vehicle, state) -> bone.setRotX(vehicle.gearRot(state.getPartialTick()) * ((float)Math.PI / 180F));
                break;
            case "LFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(Mth.lerp(state.getPartialTick(), vehicle.flap2LRotO, vehicle.getFlap2LRot()) * ((float)Math.PI / 180F));
                break;
            case "LRlap":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(Mth.lerp(state.getPartialTick(), vehicle.flap2RRotO, vehicle.getFlap2RRot()) * ((float)Math.PI / 180F));
                break;
            case "BRFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2RRotO, vehicle.getFlap2RRot()) * ((float)Math.PI / 180F));
                break;
            case "BLFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2LRotO, vehicle.getFlap2LRot()) * ((float)Math.PI / 180F));
                break;
            case "VBRFlap", "VBLFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotY(Mth.clamp(Mth.lerp(state.getPartialTick(), vehicle.flap3RotO, vehicle.getFlap3Rot()), -20.0F, 20.0F) * ((float)Math.PI / 180F));
                break;
            case "engine2":
                var1000 = (bone, vehicle, state) -> bone.setRotY(Mth.lerp(state.getPartialTick(), vehicle.propellerRotO, vehicle.getPropellerRot()));
                break;
            default :
                var1000 = null;
                break;
        }
        return var1000;
    }

    @Override
    public ResourceLocation getModelResource(F35AEntity object) {
        return new ResourceLocation("ashvehicle", "geo/f35a.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(F35AEntity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/f35a.png");
    }

    @Override
    public ResourceLocation getAnimationResource(F35AEntity animatable) {
        return new ResourceLocation("ashvehicle", "animations/f-35.animation.json");
    }
}