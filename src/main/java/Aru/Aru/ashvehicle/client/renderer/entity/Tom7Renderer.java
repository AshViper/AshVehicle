package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.Tom7Model;
import Aru.Aru.ashvehicle.entity.vehicle.Tom7Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Tom7Renderer extends VehicleRenderer<Tom7Entity> {
    public Tom7Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Tom7Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
