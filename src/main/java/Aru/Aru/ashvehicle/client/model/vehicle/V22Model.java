package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class V22Model extends VehicleModel<V22Entity> {
    public V22Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<V22Entity> collectTransform(String boneName) {
        return null;
    }
}