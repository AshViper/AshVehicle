package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class SU57Model extends VehicleModel<SU57Entity> {
    public SU57Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<SU57Entity> collectTransform(String boneName) {
        return null;
    }
}