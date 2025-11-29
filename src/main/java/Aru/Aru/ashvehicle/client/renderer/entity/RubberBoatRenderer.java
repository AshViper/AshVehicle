package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.RubberBoatModel;
import Aru.Aru.ashvehicle.entity.vehicle.RubberBoatEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RubberBoatRenderer extends VehicleRenderer<RubberBoatEntity> {
    public RubberBoatRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RubberBoatModel());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
