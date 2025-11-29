package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class F22Model extends VehicleModel<F22Entity> {
    public F22Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<F22Entity> collectTransform(String boneName) {
        return null;
    }
}