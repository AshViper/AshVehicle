package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.AH64Model;
import Aru.Aru.ashvehicle.entity.vehicle.AH64Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class AH64Renderer extends VehicleRenderer<AH64Entity> {
    public AH64Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AH64Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
