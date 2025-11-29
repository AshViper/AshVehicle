package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class ReaperModel extends VehicleModel<ReaperEntity> {
    public ReaperModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<ReaperEntity> collectTransform(String boneName) {
        return null;
    }
}