package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class F35Model extends VehicleModel<F35Entity> {
    public F35Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<F35Entity> collectTransform(String boneName) {
        return null;
    }
}