package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class M1A1AbramsModel extends VehicleModel<M1A1AbramsEntity> {
    public M1A1AbramsModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<M1A1AbramsEntity> collectTransform(String boneName) {
        return null;
    }
}