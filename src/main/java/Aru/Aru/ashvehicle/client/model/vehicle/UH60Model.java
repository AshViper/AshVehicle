package Aru.Aru.ashvehicle.client.model.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.client.model.VehicleModel;
import org.jetbrains.annotations.Nullable;

public class UH60Model extends VehicleModel<UH60Entity> {
    public UH60Model() {
    }

    @Nullable
    @Override
    public VehicleModel.TransformContext<UH60Entity> collectTransform(String boneName) {
        return null;
    }
}
