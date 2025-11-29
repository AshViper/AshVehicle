package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class F15Model extends VehicleModel<F15Entity> {
    public F15Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<F15Entity> collectTransform(String boneName) {
        return null;
    }
}