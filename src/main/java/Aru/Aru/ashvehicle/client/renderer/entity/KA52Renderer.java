package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.KA52Model;
import Aru.Aru.ashvehicle.entity.vehicle.KA52Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class KA52Renderer extends VehicleRenderer<KA52Entity> {
    public KA52Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KA52Model());
        this.shadowRadius = 0.5F;
        float scale = 1.2f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
