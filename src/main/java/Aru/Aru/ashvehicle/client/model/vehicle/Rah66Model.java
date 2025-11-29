package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class Rah66Model extends VehicleModel<Rah66Entity> {
    public Rah66Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<Rah66Entity> collectTransform(String boneName) {
        return null;
    }
}