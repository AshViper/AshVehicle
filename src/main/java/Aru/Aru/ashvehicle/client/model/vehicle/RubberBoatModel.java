package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class RubberBoatModel extends VehicleModel<RubberBoatEntity> {
    public RubberBoatModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<RubberBoatEntity> collectTransform(String boneName) {
        return null;
    }
}