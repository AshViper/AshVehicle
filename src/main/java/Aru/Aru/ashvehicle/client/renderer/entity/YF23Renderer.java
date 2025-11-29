package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.YF23Model;
import Aru.Aru.ashvehicle.entity.vehicle.YF23Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class YF23Renderer extends VehicleRenderer<YF23Entity> {
    public YF23Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new YF23Model());
        this.shadowRadius = 0.5F;
        float scale = 2.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
