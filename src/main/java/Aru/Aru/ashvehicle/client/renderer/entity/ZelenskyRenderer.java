package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.ZelenskyModel;
import Aru.Aru.ashvehicle.entity.vehicle.ZelenskyEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ZelenskyRenderer extends VehicleRenderer<ZelenskyEntity> {
    public ZelenskyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ZelenskyModel());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
