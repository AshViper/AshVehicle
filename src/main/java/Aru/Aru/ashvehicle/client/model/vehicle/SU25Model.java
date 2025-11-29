package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class SU25Model extends VehicleModel<SU25Entity> {
    public SU25Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<SU25Entity> collectTransform(String boneName) {
        return null;
    }
}