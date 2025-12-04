package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.TosModel;
import Aru.Aru.ashvehicle.entity.vehicle.TosEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TosRenderer extends VehicleRenderer<TosEntity> {
    public TosRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TosModel());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
