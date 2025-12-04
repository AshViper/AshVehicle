package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.T90Model;
import Aru.Aru.ashvehicle.entity.vehicle.T90Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class T90Renderer extends VehicleRenderer<T90Entity> {
    public T90Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new T90Model());
        this.shadowRadius = 0.5F;
        float scale = 1.5f;
        scaleHeight = scale;
        scaleWidth = scale;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T90Entity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();

        // Wheel rotation speed (slower for realistic tank movement)
        float wheelSpeed = 0.5F;
        float leftWheelRot = wheelSpeed * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot());
        float rightWheelRot = wheelSpeed * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot());

        // Left wheels (wheel1-wheel8)
        for (int i = 1; i <= 8; i++) {
            if (name.equals("wheel" + i)) {
                bone.setRotX(leftWheelRot);
            }
        }

        // Right wheels (wheel9-wheel16)
        for (int i = 9; i <= 16; i++) {
            if (name.equals("wheel" + i)) {
                bone.setRotX(rightWheelRot);
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
