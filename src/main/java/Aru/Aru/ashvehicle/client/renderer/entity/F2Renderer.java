package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.F2Model;
import Aru.Aru.ashvehicle.entity.vehicle.F2Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class F2Renderer extends VehicleRenderer<F2Entity> {
    public F2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F2Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
