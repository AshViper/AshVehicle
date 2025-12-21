package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.F35AModel;
import Aru.Aru.ashvehicle.entity.vehicle.F35AEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class F35ARenderer extends VehicleRenderer<F35AEntity> {
    public F35ARenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F35AModel());
        this.shadowRadius = 0.5F;
        float scale = 1.1f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
