package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.GepardModel;
import Aru.Aru.ashvehicle.entity.vehicle.GepardEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class GepardRenderer extends VehicleRenderer<GepardEntity> {
    private static final float PI = (float) Math.PI;
    private static final float TWO_PI = PI * 2;
    private static final float RADAR_SPIN_SPEED = 0.12f / 1.4f;
    private static final float RADAR_RETURN_SPEED = 0.04f;
    
    private float radarAngle = PI;

    public GepardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GepardModel());
        this.shadowRadius = 0.5F;
        scaleHeight = 1.5f;
        scaleWidth = 1.5f;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, GepardEntity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();

        // Wheels
        float leftRot = 0.5f * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot());
        float rightRot = 0.5f * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot());

        for (int i = 1; i <= 9; i++) {
            if (name.equals("wheel" + i)) bone.setRotX(rightRot);
        }
        for (int i = 10; i <= 18; i++) {
            if (name.equals("wheel" + i)) bone.setRotX(leftRot);
        }

        // Radar
        if (name.equals("radar")) {
            if (animatable.getFirstPassenger() != null) {
                radarAngle -= RADAR_SPIN_SPEED;
                if (radarAngle < -TWO_PI) radarAngle += TWO_PI;
            } else {
                float diff = PI - radarAngle;
                while (diff > PI) diff -= TWO_PI;
                while (diff < -PI) diff += TWO_PI;
                
                if (Math.abs(diff) > 0.02f) {
                    radarAngle += diff * 0.08f;
                } else {
                    radarAngle = PI;
                }
            }
            bone.setRotY(radarAngle);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
