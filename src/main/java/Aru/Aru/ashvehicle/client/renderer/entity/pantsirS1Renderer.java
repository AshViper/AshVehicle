package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.pantsirS1Model;
import Aru.Aru.ashvehicle.entity.vehicle.SapsanEntity;
import Aru.Aru.ashvehicle.entity.vehicle.pantsirS1Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class pantsirS1Renderer extends VehicleRenderer<pantsirS1Entity> {
    public pantsirS1Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new pantsirS1Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, pantsirS1Entity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();

        // Steering angle for front wheels
        float steeringAngle = Mth.lerp(partialTick, animatable.rudderRotO, animatable.getRudderRot());

        // Wheel rotation speed multiplier (slower = more realistic)
        float wheelSpeedMultiplier = -0.5F;

        // Front left wheel (wheel1) - with steering
        if (name.equals("WHEEL")) {
            bone.setRotX(wheelSpeedMultiplier * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot()));
            bone.setRotY(steeringAngle); // Steering rotation
        }

        // Other left wheels (wheel2, wheel3, wheel4, wheel5) - no steering
        if (name.equals("WHEEL3") || name.equals("WHEEL5") || name.equals("WHEEL7")) {
            bone.setRotX(wheelSpeedMultiplier * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot()));
        }

        // Front right wheel (wheel6) - with steering
        if (name.equals("WHEEL2")) {
            bone.setRotX(wheelSpeedMultiplier * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot()));
            bone.setRotY(steeringAngle); // Steering rotation
        }

        // Other right wheels (wheel7, wheel8, wheel9, wheel10) - no steering
        if (name.equals("WHEEL4") || name.equals("WHEEL6") || name.equals("WHEEL8")) {
            bone.setRotX(wheelSpeedMultiplier * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot()));
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
