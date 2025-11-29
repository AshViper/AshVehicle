package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class YF23Model extends VehicleModel<YF23Entity> {
    public YF23Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<YF23Entity> collectTransform(String boneName) {
        return null;
    }
}