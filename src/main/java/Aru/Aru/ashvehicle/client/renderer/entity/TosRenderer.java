package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.TosModel;
import Aru.Aru.ashvehicle.entity.vehicle.T90Entity;
import Aru.Aru.ashvehicle.entity.vehicle.TosEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class TosRenderer extends VehicleRenderer<TosEntity> {
    public TosRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TosModel());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, TosEntity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();

        // Wheel rotation speed (slower for realistic tank movement)
        float wheelSpeed = 0.5F;
        float leftWheelRot = wheelSpeed * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot());
        float rightWheelRot = wheelSpeed * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot());

        // Left wheels (wheel1-wheel8)
        for (int i = 0; i <= 7; i++) {
            if (name.equals("WHELL" + i)) {
                bone.setRotX(leftWheelRot);
            }
        }

        // Right wheels (wheel9-wheel16)
        for (int i = 8; i <= 15; i++) {
            if (name.equals("WHELL" + i)) {
                bone.setRotX(rightWheelRot);
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
