package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.EuroFighterModel;
import Aru.Aru.ashvehicle.entity.vehicle.EuroFighterEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class EuroFighterRenderer extends VehicleRenderer<EuroFighterEntity> {
    public EuroFighterRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EuroFighterModel());
        this.shadowRadius = 0.5F;
        float scale = 2.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
