package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class F4Model extends VehicleModel<F4Entity> {
    public F4Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<F4Entity> collectTransform(String boneName) {
        return null;
    }
}