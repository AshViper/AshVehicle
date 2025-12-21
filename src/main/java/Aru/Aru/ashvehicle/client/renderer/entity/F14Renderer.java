package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.F14Model;
import Aru.Aru.ashvehicle.entity.vehicle.F14Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class F14Renderer extends VehicleRenderer<F14Entity> {
    public F14Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F14Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, F14Entity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();
        // Animate missile pod rotation with smooth interpolation
        if (name.equals("Rwing"))  {
            float podRot = Mth.lerp(partialTick, animatable.vtolRotO, animatable.getPodRot());
            bone.setRotY(podRot * (-(float) Math.PI / 180F));
        }
        if (name.equals("Lwing"))  {
            float podRot = Mth.lerp(partialTick, animatable.vtolRotO, animatable.getPodRot());
            bone.setRotY(podRot * ((float) Math.PI / 180F));
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
