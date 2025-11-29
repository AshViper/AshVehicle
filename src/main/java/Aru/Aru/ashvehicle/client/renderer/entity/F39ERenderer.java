package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.F39EModel;
import Aru.Aru.ashvehicle.entity.vehicle.F39EEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class F39ERenderer extends VehicleRenderer<F39EEntity> {
    public F39ERenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F39EModel());
        this.shadowRadius = 0.5F;
        float scale = 2.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
