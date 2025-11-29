package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.M3A3BradleyModel;
import Aru.Aru.ashvehicle.entity.vehicle.M3A3BradleyEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class M3A3BradleyRenderer extends VehicleRenderer<M3A3BradleyEntity> {
    public M3A3BradleyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new M3A3BradleyModel());
        this.shadowRadius = 0.5F;
        float scale = 1.5f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
