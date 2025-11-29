package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.SU57Model;
import Aru.Aru.ashvehicle.entity.vehicle.SU57Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SU57Renderer extends VehicleRenderer<SU57Entity> {
    public SU57Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SU57Model());
        this.shadowRadius = 0.5F;
        float scale = 2.5f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
