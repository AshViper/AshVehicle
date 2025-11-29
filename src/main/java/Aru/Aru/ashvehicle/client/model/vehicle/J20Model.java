package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class J20Model extends VehicleModel<J20Entity> {
    public J20Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<J20Entity> collectTransform(String boneName) {
        return null;
    }
}