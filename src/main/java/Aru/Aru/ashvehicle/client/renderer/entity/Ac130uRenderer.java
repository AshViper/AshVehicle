package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.layer.AC130UGlowLayer;
import Aru.Aru.ashvehicle.client.model.vehicle.Ac130uModel;
import Aru.Aru.ashvehicle.entity.vehicle.Ac130uEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Ac130uRenderer extends VehicleRenderer<Ac130uEntity> {
    public Ac130uRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Ac130uModel());
        this.addRenderLayer(new AC130UGlowLayer(this));
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}