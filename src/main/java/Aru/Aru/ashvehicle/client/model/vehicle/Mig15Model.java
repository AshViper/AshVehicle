package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class Mig15Model extends VehicleModel<Mig15Entity> {
    public Mig15Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<Mig15Entity> collectTransform(String boneName) {
        return null;
    }
}