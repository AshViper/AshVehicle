package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.J20Model;
import Aru.Aru.ashvehicle.entity.vehicle.J20Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class J20Renderer extends VehicleRenderer<J20Entity> {
    public J20Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new J20Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
