package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.layer.C130GlowLayer;
import Aru.Aru.ashvehicle.client.model.vehicle.C130Model;
import Aru.Aru.ashvehicle.entity.vehicle.C130Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class C130Renderer extends VehicleRenderer<C130Entity> {
    public C130Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new C130Model());
        this.addRenderLayer(new C130GlowLayer(this));
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, C130Entity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();
        // Animate missile pod rotation with smooth interpolation
        if (name.equals("group2"))  {
            float podRot = Mth.lerp(partialTick, animatable.hatchRotO, animatable.getHatchRot());
            bone.setRotX(podRot * ((float) Math.PI / 180F));
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}