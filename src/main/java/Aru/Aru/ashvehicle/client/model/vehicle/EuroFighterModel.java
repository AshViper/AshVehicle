package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class EuroFighterModel extends VehicleModel<EuroFighterEntity> {
    public EuroFighterModel() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<EuroFighterEntity> collectTransform(String boneName) {
        return null;
    }
}