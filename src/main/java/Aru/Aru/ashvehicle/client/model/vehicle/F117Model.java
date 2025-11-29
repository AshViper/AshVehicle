package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class F117Model extends VehicleModel<F117Entity> {
    public F117Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<F117Entity> collectTransform(String boneName) {
        return null;
    }
}