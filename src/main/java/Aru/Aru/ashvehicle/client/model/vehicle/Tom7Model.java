package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class Tom7Model extends VehicleModel<Tom7Entity> {
    public Tom7Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<Tom7Entity> collectTransform(String boneName) {
        return null;
    }
}
