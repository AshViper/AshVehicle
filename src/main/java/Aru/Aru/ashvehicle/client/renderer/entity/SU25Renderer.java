package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.SU25Model;
import Aru.Aru.ashvehicle.entity.vehicle.SU25Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SU25Renderer extends VehicleRenderer<SU25Entity> {
    public SU25Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SU25Model());
        this.shadowRadius = 0.5F;
        float scale = 0.9f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
