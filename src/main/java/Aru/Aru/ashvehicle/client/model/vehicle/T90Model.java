package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class T90Model extends VehicleModel<T90Entity> {
    public T90Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<T90Entity> collectTransform(String boneName) {
        return null;
    }
}