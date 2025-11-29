package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.GepardModel;
import Aru.Aru.ashvehicle.entity.vehicle.GepardEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GepardRenderer extends VehicleRenderer<GepardEntity> {
    public GepardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GepardModel());
        this.shadowRadius = 0.5F;
        float scale = 1.5f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
