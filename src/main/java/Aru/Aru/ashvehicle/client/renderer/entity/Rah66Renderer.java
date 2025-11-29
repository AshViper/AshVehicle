package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.Rah66Model;
import Aru.Aru.ashvehicle.entity.vehicle.Rah66Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Rah66Renderer extends VehicleRenderer<Rah66Entity> {
    public Rah66Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Rah66Model());
        this.shadowRadius = 0.5F;
        float scale = 1.2f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
