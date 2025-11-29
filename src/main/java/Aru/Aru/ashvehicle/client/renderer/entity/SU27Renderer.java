package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.SU27Model;
import Aru.Aru.ashvehicle.entity.vehicle.SU27Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SU27Renderer extends VehicleRenderer<SU27Entity> {
    public SU27Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SU27Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
