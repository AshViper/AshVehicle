package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.SU33Model;
import Aru.Aru.ashvehicle.entity.vehicle.SU33Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SU33Renderer extends VehicleRenderer<SU33Entity> {
    public SU33Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SU33Model());
        this.shadowRadius = 0.5F;
        float scale = 1.3f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
