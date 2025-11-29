package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.F35Model;
import Aru.Aru.ashvehicle.entity.vehicle.F35Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class F35Renderer extends VehicleRenderer<F35Entity> {
    public F35Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F35Model());
        this.shadowRadius = 0.5F;
        float scale = 1.5f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
