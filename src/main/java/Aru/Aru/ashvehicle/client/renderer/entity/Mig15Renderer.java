package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.Mig15Model;
import Aru.Aru.ashvehicle.entity.vehicle.Mig15Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Mig15Renderer extends VehicleRenderer<Mig15Entity> {
    public Mig15Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Mig15Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
