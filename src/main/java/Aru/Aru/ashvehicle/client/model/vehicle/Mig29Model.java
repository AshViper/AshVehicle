package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class Mig29Model extends VehicleModel<Mig29Entity> {
    public Mig29Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<Mig29Entity> collectTransform(String boneName) {
        return null;
    }
}