package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class F39EModel extends VehicleModel<F39EEntity> {
    public F39EModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<F39EEntity> collectTransform(String boneName) {
        return null;
    }
}