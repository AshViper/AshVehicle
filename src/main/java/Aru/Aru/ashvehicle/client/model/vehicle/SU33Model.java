package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class SU33Model extends VehicleModel<SU33Entity> {
    public SU33Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<SU33Entity> collectTransform(String boneName) {
        return null;
    }
}