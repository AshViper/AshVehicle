package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.pantsirS1Model;
import Aru.Aru.ashvehicle.entity.vehicle.pantsirS1Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class pantsirS1Renderer extends VehicleRenderer<pantsirS1Entity> {
    public pantsirS1Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new pantsirS1Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
