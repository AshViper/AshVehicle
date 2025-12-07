package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.KV2Model;
import Aru.Aru.ashvehicle.entity.vehicle.KV2Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class KV2Renderer extends VehicleRenderer<KV2Entity> {
    public KV2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KV2Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, KV2Entity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();

        // Wheel rotation speed
        float wheelSpeed = 0.5F;
        float leftWheelRot = wheelSpeed * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot());
        float rightWheelRot = wheelSpeed * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot());

        // Right wheels (wheel1-wheel11, parent "R")
        for (int i = 1; i <= 11; i++) {
            if (name.equals("wheel" + i)) {
                bone.setRotX(rightWheelRot);
            }
        }

        // Left wheels (wheel12-wheel22, parent "L")
        for (int i = 12; i <= 22; i++) {
            if (name.equals("wheel" + i)) {
                bone.setRotX(leftWheelRot);
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
