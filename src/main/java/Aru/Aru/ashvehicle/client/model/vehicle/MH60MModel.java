package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class MH60MModel extends VehicleModel<MH60MEntity> {
    public MH60MModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<MH60MEntity> collectTransform(String boneName) {
        return null;
    }
}
