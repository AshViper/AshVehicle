package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class Rah66Model extends VehicleModel<Rah66Entity> {
    public Rah66Model() {
    }

    @Nullable
    public VehicleModel.TransformContext<Rah66Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext var1000;
        switch (boneName){
            case "bone4":
                var1000 = (bone, vehicle, state) -> bone.setRotY(Mth.lerp(state.getPartialTick(), vehicle.propellerRotO, vehicle.getPropellerRot()));
                break;
            case "bone5":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.propellerRotO, vehicle.getPropellerRot()));
                break;
            case "bone2":
                var1000 = (bone, vehicle, state) -> bone.setRotY(this.turretYRot * ((float)Math.PI / 180F));
                break;
            case "bone8":
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
    public ResourceLocation getModelResource(Rah66Entity object) {
        return new ResourceLocation("ashvehicle", "geo/rah_66.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Rah66Entity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/rah_66.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Rah66Entity animatable) {
        return new ResourceLocation("ashvehicle", "animations/rah_66.animation.json");
    }
}