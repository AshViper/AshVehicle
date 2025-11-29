package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.T90Model;
import Aru.Aru.ashvehicle.entity.vehicle.T90Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class T90Renderer extends VehicleRenderer<T90Entity> {
    public T90Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new T90Model());
        this.shadowRadius = 0.5F;
        float scale = 1.5f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}
