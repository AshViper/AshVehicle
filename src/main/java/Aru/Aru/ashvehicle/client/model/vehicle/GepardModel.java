package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class GepardModel extends VehicleModel<GepardEntity> {
    public GepardModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<GepardEntity> collectTransform(String boneName) {
        return null;
    }
}