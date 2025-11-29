package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.Mig29Model;
import Aru.Aru.ashvehicle.entity.vehicle.Mig29Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Mig29Renderer extends VehicleRenderer<Mig29Entity> {
    public Mig29Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Mig29Model());
        this.shadowRadius = 0.5F;
        float scale = 1.3f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
