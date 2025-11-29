package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.F15Model;
import Aru.Aru.ashvehicle.entity.vehicle.F15Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class F15Renderer extends VehicleRenderer<F15Entity> {
    public F15Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F15Model());
        this.shadowRadius = 0.5F;
        float scale = 1.1f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
