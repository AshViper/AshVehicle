package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class KV2Model extends VehicleModel<KV2Entity> {
    public KV2Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<KV2Entity> collectTransform(String boneName) {
        return null;
    }
}