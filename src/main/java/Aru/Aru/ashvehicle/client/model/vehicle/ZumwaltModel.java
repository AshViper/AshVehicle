package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class ZumwaltModel extends VehicleModel<ZumwaltEntity> {
    public ZumwaltModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<ZumwaltEntity> collectTransform(String boneName) {
        return null;
    }
}
