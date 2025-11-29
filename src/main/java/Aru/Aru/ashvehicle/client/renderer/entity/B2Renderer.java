package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.*;
import Aru.Aru.ashvehicle.entity.vehicle.*;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class B2Renderer extends VehicleRenderer<B2Entity> {
    public B2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new B2Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }
}