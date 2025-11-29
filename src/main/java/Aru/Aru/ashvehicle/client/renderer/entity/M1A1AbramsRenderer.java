package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.M1A1AbramsModel;
import Aru.Aru.ashvehicle.entity.vehicle.M1A1AbramsEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class M1A1AbramsRenderer extends VehicleRenderer<M1A1AbramsEntity> {
    public M1A1AbramsRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new M1A1AbramsModel());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}