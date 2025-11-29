package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class SU34Model extends VehicleModel<SU34Entity> {
    public SU34Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<SU34Entity> collectTransform(String boneName) {
        return null;
    }
}