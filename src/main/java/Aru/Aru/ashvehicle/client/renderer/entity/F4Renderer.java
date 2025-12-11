package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.F4Model;
import Aru.Aru.ashvehicle.entity.vehicle.F4Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class F4Renderer extends VehicleRenderer<F4Entity> {
    public F4Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F4Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
