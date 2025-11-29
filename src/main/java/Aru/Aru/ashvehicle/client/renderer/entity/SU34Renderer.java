package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.SU34Model;
import Aru.Aru.ashvehicle.entity.vehicle.SU34Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SU34Renderer extends VehicleRenderer<SU34Entity> {
    public SU34Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SU34Model());
        this.shadowRadius = 0.5F;
        float scale = 1.5f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
