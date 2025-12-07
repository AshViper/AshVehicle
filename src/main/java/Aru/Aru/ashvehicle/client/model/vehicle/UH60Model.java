package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class UH60Model extends VehicleModel<UH60Entity> {
    public UH60Model() {
    }

    @Nullable
    public VehicleModel.TransformContext<UH60Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext var1000;
        switch (boneName){
            case "VINT":
                var1000 = (bone, vehicle, state) -> bone.setRotY(Mth.lerp(state.getPartialTick(), vehicle.propellerRotO, vehicle.getPropellerRot()));
                break;
            case "VINT2":
                var1000 = (bone, vehicle, state) -> bone.setRotX(Mth.lerp(state.getPartialTick(), vehicle.propellerRotO, vehicle.getPropellerRot()));
                break;
            default :
                var1000 = null;
                break;
        }
        return var1000;
    }

    @Override
    public ResourceLocation getModelResource(UH60Entity object) {
        return new ResourceLocation("ashvehicle", "geo/uh_60.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(UH60Entity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/uh_60.png");
    }

    @Override
    public ResourceLocation getAnimationResource(UH60Entity animatable) {
        return new ResourceLocation("ashvehicle", "animations/uh_60.animation.json");
    }
}
