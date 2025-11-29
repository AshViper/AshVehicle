package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.F16Model;
import Aru.Aru.ashvehicle.entity.vehicle.F16Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class F16Renderer extends VehicleRenderer<F16Entity> {
    public F16Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F16Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
