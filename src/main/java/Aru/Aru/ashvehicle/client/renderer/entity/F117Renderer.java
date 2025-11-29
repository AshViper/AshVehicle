package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.F117Model;
import Aru.Aru.ashvehicle.entity.vehicle.F117Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class F117Renderer extends VehicleRenderer<F117Entity> {
    public F117Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F117Model());
        this.shadowRadius = 0.5F;
        float scale = 0.8f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
