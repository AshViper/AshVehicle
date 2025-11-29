package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.X47BModel;
import Aru.Aru.ashvehicle.entity.vehicle.X47BEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class X47BRenderer extends VehicleRenderer<X47BEntity> {
    public X47BRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new X47BModel());
        this.shadowRadius = 0.5F;
        float scale = 2.5f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
