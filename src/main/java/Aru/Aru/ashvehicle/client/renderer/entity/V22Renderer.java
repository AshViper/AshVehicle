package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.V22Model;
import Aru.Aru.ashvehicle.entity.vehicle.V22Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class V22Renderer extends VehicleRenderer<V22Entity> {
    public V22Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new V22Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}