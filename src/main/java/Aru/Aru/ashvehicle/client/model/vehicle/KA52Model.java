package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.client.model.VehicleModel;
import Aru.Aru.ashvehicle.entity.vehicle.KA52Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class KA52Model extends VehicleModel<KA52Entity> {
    public KA52Model() {
    }

    @Nullable
    public VehicleModel.TransformContext<KA52Entity> collectTransform(String boneName) {
        TransformContext var1000;
        switch (boneName){
            case "propeller":
                var1000 = (bone, vehicle, state) -> bone.setRotY(-Mth.lerp(state.getPartialTick(), vehicle.propellerRotO, vehicle.getPropellerRot()));
                break;
            case "propeller2":
                var1000 = (bone, vehicle, state) -> bone.setRotY(Mth.lerp(state.getPartialTick(), vehicle.propellerRotO, vehicle.getPropellerRot()));
                break;
            case "bone8":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.propellerRotO, vehicle.getPropellerRot()));
                break;
            case "bone", "cameraX":
                var1000 = (bone, vehicle, state) -> bone.setRotY(this.turretYRot * ((float)Math.PI / 180F));
                break;
            case "bone12", "cameraY":
                float a = this.turretYaw;
                float r = (Mth.abs(a) - 90.0F) / 90.0F;
                float r2;
                if (Mth.abs(a) <= 90.0F) {
                    r2 = a / 90.0F;
                } else if (a < 0.0F) {
                    r2 = -(180.0F + a) / 90.0F;
                } else {
                    r2 = (180.0F - a) / 90.0F;
                }
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.clamp(-this.turretXRot - r * this.pitch - r2 * this.roll, vehicle.getTurretMinPitch(), vehicle.getTurretMaxPitch()) * ((float)Math.PI / 180F));
                break;
            default :
                var1000 = null;
                break;
        }
        return var1000;
    }
    @Override
    public ResourceLocation getModelResource(KA52Entity object) {
        return new ResourceLocation("ashvehicle", "geo/ka52.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KA52Entity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/ka52.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KA52Entity animatable) {
        return new ResourceLocation("ashvehicle", "animations/ka52.animation.json");
    }
}