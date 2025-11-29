package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class SU27Model extends VehicleModel<SU27Entity> {
    public SU27Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<SU27Entity> collectTransform(String boneName) {
        return null;
    }
}