package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class M3A3BradleyModel extends VehicleModel<M3A3BradleyEntity> {
    public M3A3BradleyModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<M3A3BradleyEntity> collectTransform(String boneName) {
        return null;
    }
}