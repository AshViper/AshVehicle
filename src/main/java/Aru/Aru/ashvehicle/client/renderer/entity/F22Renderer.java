package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.layer.F22GlowLayer;
import Aru.Aru.ashvehicle.client.model.vehicle.F22Model;
import Aru.Aru.ashvehicle.entity.vehicle.F22Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class F22Renderer extends VehicleRenderer<F22Entity> {
    public F22Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F22Model());
        //this.addRenderLayer(new F22GlowLayer(this));
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
