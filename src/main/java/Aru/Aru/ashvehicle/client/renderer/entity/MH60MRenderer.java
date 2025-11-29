package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.MH60MModel;
import Aru.Aru.ashvehicle.entity.vehicle.MH60MEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MH60MRenderer extends VehicleRenderer<MH60MEntity> {
    public MH60MRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MH60MModel());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}