package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.SapsanModel;
import Aru.Aru.ashvehicle.entity.vehicle.SapsanEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class SapsanRenderer extends VehicleRenderer<SapsanEntity> {
    public SapsanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SapsanModel());
        this.shadowRadius = 1.5F;
        float scale = 1.5f;
        scaleHeight = scale;
        scaleWidth = scale;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SapsanEntity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();

        // Animate missile pod rotation with smooth interpolation
        if (name.equals("missilepod")) {
            float podRot = Mth.lerp(partialTick, animatable.podRotO, animatable.getPodRot());
            bone.setRotX(podRot * ((float) Math.PI / 180F));
        }

        // Steering angle for front wheels
        float steeringAngle = Mth.lerp(partialTick, animatable.rudderRotO, animatable.getRudderRot());
        
        // Wheel rotation speed multiplier (slower = more realistic)
        float wheelSpeedMultiplier = 0.5F;

        // Front left wheel (wheel1) - with steering
        if (name.equals("wheel1")) {
            bone.setRotX(wheelSpeedMultiplier * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot()));
            bone.setRotY(steeringAngle); // Steering rotation
        }

        // Other left wheels (wheel2, wheel3, wheel4, wheel5) - no steering
        if (name.equals("wheel2") || name.equals("wheel3") || name.equals("wheel4") || name.equals("wheel5")) {
            bone.setRotX(wheelSpeedMultiplier * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot()));
        }

        // Front right wheel (wheel6) - with steering
        if (name.equals("wheel6")) {
            bone.setRotX(wheelSpeedMultiplier * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot()));
            bone.setRotY(steeringAngle); // Steering rotation
        }

        // Other right wheels (wheel7, wheel8, wheel9, wheel10) - no steering
        if (name.equals("wheel7") || name.equals("wheel8") || name.equals("wheel9") || name.equals("wheel10")) {
            bone.setRotX(wheelSpeedMultiplier * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot()));
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
