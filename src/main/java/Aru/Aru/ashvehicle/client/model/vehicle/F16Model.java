package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class F16Model extends VehicleModel<F16Entity> {
    public F16Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<F16Entity> collectTransform(String boneName) {
        return null;
    }
}