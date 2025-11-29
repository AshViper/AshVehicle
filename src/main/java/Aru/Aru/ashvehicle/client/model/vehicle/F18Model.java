package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class F18Model extends VehicleModel<F18Entity> {
    public F18Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<F18Entity> collectTransform(String boneName) {
        return null;
    }
}