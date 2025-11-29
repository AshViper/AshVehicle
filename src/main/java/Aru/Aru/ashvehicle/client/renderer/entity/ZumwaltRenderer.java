package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.ZumwaltModel;
import Aru.Aru.ashvehicle.entity.vehicle.ZumwaltEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ZumwaltRenderer extends VehicleRenderer<ZumwaltEntity> {
    public ZumwaltRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ZumwaltModel());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}