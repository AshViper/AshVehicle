package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.KV2Model;
import Aru.Aru.ashvehicle.entity.vehicle.KV2Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class KV2Renderer extends VehicleRenderer<KV2Entity> {
    public KV2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KV2Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
