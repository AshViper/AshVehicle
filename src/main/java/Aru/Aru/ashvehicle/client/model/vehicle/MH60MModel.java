package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class MH60MModel extends VehicleModel<MH60MEntity> {
    public MH60MModel() {
    }

    @Nullable
    public VehicleModel.TransformContext<MH60MEntity> collectTransform(String boneName) {
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
    public ResourceLocation getModelResource(MH60MEntity object) {
        return new ResourceLocation("ashvehicle", "geo/mh_60m.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MH60MEntity object) {
        return new ResourceLocation("ashvehicle", "textures/entity/mh_60m.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MH60MEntity animatable) {
        return new ResourceLocation("ashvehicle", "animations/mh_60m.animation.json");
    }
}
