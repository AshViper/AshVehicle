package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class M3A3BradleyModel extends VehicleModel<M3A3BradleyEntity> {
    public M3A3BradleyModel() {
    }

    @Nullable
    public VehicleModel.TransformContext<M3A3BradleyEntity> collectTransform(String boneName) {
        TransformContext var1000;
        switch (boneName){
            case "hexadecagon" :
                var1000 = (bone, vehicle, state) -> bone.setRotY(this.turretYRot * ((float)Math.PI / 180F));
                break;
            case "barrel":
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
    public ResourceLocation getModelResource(M3A3BradleyEntity object) {
        return new ResourceLocation("ashvehicle", "geo/m3a3-bradley.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(M3A3BradleyEntity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/m3a3-bradley.png");
    }

    @Override
    public ResourceLocation getAnimationResource(M3A3BradleyEntity animatable) {
        return new ResourceLocation("ashvehicle", "animations/m3a3-bradley.animation.json");
    }
}