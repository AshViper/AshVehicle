package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.M3A3BradleyModel;
import Aru.Aru.ashvehicle.entity.vehicle.M3A3BradleyEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class M3A3BradleyRenderer extends VehicleRenderer<M3A3BradleyEntity> {
    public M3A3BradleyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new M3A3BradleyModel());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, M3A3BradleyEntity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();

        // Wheel rotation speed
        float wheelSpeed = 0.5F;
        float leftWheelRot = wheelSpeed * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot());
        float rightWheelRot = wheelSpeed * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot());

        // Left road wheels (WHELL2-7, skip WHELL1 and WHELL8 - drive/idler wheels)
        for (int i = 2; i <= 7; i++) {
            if (name.equals("WHELL" + i)) {
                bone.setRotX(leftWheelRot);
            }
        }

        // Right road wheels (WHELL10-16, skip WHELL9 and WHELL17 - drive/idler wheels)
        for (int i = 10; i <= 16; i++) {
            if (name.equals("WHELL" + i)) {
                bone.setRotX(rightWheelRot);
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
