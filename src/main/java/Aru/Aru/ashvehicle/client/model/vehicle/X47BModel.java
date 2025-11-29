package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class X47BModel extends VehicleModel<X47BEntity> {
    public X47BModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<X47BEntity> collectTransform(String boneName) {
        return null;
    }
}