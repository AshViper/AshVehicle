package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.UH60Model;
import Aru.Aru.ashvehicle.entity.vehicle.UH60Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class UH60Renderer extends VehicleRenderer<UH60Entity> {
    public UH60Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new UH60Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}