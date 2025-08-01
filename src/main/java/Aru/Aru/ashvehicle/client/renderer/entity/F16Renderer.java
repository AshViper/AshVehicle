package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.layer.F16GlowLayer;
import Aru.Aru.ashvehicle.client.layer.F22GlowLayer;
import com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import Aru.Aru.ashvehicle.client.model.vehicle.F16Model;
import Aru.Aru.ashvehicle.entity.vehicle.F16Entity;

public class F16Renderer extends GeoEntityRenderer<F16Entity> {
    public F16Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F16Model());
        this.shadowRadius = 0.0f;
        this.addRenderLayer(new F16GlowLayer(this));
    }
    public RenderType getRenderType(F16Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }

    public void preRender(PoseStack poseStack, F16Entity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = 1.0F;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void render(F16Entity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())));
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    public void renderRecursively(PoseStack poseStack, F16Entity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();

        if (name.equals("LeftTailPlane")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2LRotO, animatable.getFlap2LRot()) * ((float)Math.PI / 180F));
        }

        if (name.equals("RightTailPlane")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2RRotO, animatable.getFlap2RRot()) * ((float)Math.PI / 180F));
        }

        if (name.equals("verticalTail")) {
            bone.setRotY(Mth.clamp(Mth.lerp(partialTick, animatable.flap3RotO, animatable.getFlap3Rot()), -20.0F, 20.0F) * ((float)Math.PI / 180F));
        }

        float gearRot = Mth.lerp(
                partialTick,
                animatable.gearRotO,
                (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT)
        );

// ✅ Tyre（タイヤ収納）
        if (name.equals("Tyre")) {
            bone.setRotX(gearRot * (-(float)Math.PI / 135F));
        }

// ✅ Right/LeftTyre（走行回転）
        if (name.equals("RightTyre") || name.equals("LeftTyre")) {
            bone.setRotX(gearRot * ((float) Math.PI / 180F));
        }
        if (name.equals("LeftTyre2")) {
            bone.setRotY(gearRot * (-(float) Math.PI / 180F));
        }
        if (name.equals("RightTyre2")) {
            bone.setRotY(gearRot * ((float) Math.PI / 180F));
        }

        int rot = 214;

        if (name.equals("RightTyreHatch")) {
            bone.setRotZ((gearRot + rot) * (-(float)Math.PI / 150F));
        }
        if (name.equals("LeftTyreHatch")) {
            bone.setRotZ((gearRot + rot) * ((float)Math.PI / 150F));
        }
        if (name.equals("ForwardTyreHatch")) {
            bone.setRotZ((gearRot - 88) * (-(float)Math.PI / 180F));
        }

        if (name.equals("RightFlap")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap1LRotO, animatable.getFlap1LRot()) * (-(float)Math.PI / 180F));
        }

        if (name.equals("LeftFlap")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap1RRotO, animatable.getFlap1RRot()) * (-(float)Math.PI / 180F));
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
