package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class B2Model extends VehicleModel<B2Entity> {
    public B2Model() {}

    @Nullable
    @Override
    public VehicleModel.TransformContext<B2Entity> collectTransform(String boneName) {
        return null;
    }
}