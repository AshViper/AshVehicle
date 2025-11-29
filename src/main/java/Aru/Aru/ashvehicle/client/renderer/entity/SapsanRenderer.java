package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.SapsanModel;
import Aru.Aru.ashvehicle.entity.vehicle.SapsanEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SapsanRenderer extends VehicleRenderer<SapsanEntity> {
    public SapsanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SapsanModel());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
