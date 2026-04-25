package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.layer.B52GlowLayer;
import Aru.Aru.ashvehicle.client.model.vehicle.B52Model;
import Aru.Aru.ashvehicle.entity.vehicle.B52Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class B52Renderer extends VehicleRenderer<B52Entity> {
    public B52Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new B52Model());
        this.addRenderLayer(new B52GlowLayer(this));
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}