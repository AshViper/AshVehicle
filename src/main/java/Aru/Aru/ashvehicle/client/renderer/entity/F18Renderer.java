package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.F18Model;
import Aru.Aru.ashvehicle.entity.vehicle.F18Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class F18Renderer extends VehicleRenderer<F18Entity> {
    public F18Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F18Model());
        this.shadowRadius = 0.5F;
        float scale = 1.2f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
