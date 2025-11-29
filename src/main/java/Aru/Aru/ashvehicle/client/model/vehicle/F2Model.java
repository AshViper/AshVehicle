package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class F2Model extends VehicleModel<F2Entity> {
    public F2Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<F2Entity> collectTransform(String boneName) {
        return null;
    }
}