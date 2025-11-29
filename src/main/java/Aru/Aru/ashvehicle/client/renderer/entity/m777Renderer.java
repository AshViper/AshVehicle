package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.m777Model;
import Aru.Aru.ashvehicle.entity.vehicle.m777Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class m777Renderer extends VehicleRenderer<m777Entity> {
    public m777Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new m777Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
