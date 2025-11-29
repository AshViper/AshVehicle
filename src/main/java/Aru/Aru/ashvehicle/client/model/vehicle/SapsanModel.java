package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class SapsanModel extends VehicleModel<SapsanEntity> {
    public SapsanModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<SapsanEntity> collectTransform(String boneName) {
        return null;
    }
}
