package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.client.model.VehicleModel;
import Aru.Aru.ashvehicle.entity.vehicle.ZelenskyEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class ZelenskyModel extends VehicleModel<ZelenskyEntity> {
    public ZelenskyModel() {
    }

    @Nullable
    public VehicleModel.TransformContext<ZelenskyEntity> collectTransform(String boneName) {
        TransformContext var1000;
        switch (boneName){
            case "Pro":
                var1000 = (bone, vehicle, state) -> bone.setRotZ(Mth.lerp(state.getPartialTick(), vehicle.propellerRotO, vehicle.getPropellerRot()));
                break;
            case "LFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2LRotO, vehicle.getFlap2LRot()) * ((float)Math.PI / 180F));
                break;
            case "RFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2RRotO, vehicle.getFlap2RRot()) * ((float)Math.PI / 180F));
                break;
            case "BLFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2RRotO, vehicle.getFlap2RRot()) * ((float)Math.PI / 180F));
                break;
            case "BRFlap":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.flap2LRotO, vehicle.getFlap2LRot()) * ((float)Math.PI / 180F));
                break;
            default :
                var1000 = null;
                break;
        }
        return var1000;
    }
    @Override
    public ResourceLocation getModelResource(ZelenskyEntity object) {
        return new ResourceLocation("ashvehicle", "geo/zelensky.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ZelenskyEntity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/zelensky.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ZelenskyEntity animatable) {
        return new ResourceLocation("ashvehicle", "animations/ah-64.animation.json");
    }
}