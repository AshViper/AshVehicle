package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class m777Model extends VehicleModel<m777Entity> {
    public m777Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<m777Entity> collectTransform(String boneName) {
        return null;
    }
}