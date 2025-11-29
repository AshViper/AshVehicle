package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.ReaperModel;
import Aru.Aru.ashvehicle.entity.vehicle.ReaperEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ReaperRenderer extends VehicleRenderer<ReaperEntity> {
    public ReaperRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ReaperModel());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
