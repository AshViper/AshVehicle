package Aru.Aru.ashvehicle.client.renderer.entity;

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
import Aru.Aru.ashvehicle.client.model.SU57Model;
import Aru.Aru.ashvehicle.entity.SU57Entity;

public class SU57Renderer extends GeoEntityRenderer<SU57Entity> {
    public SU57Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SU57Model());
        this.shadowRadius = 0.0f;
    }
    public RenderType getRenderType(SU57Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }

    public void preRender(PoseStack poseStack, SU57Entity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = 2.0F;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void render(SU57Entity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())));
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    public void renderRecursively(PoseStack poseStack, SU57Entity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();

        if (name.equals("flapLB")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2LRot, animatable.getFlap2LRot()) * ((float)Math.PI / 180F));
        }

        if (name.equals("flapRB")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2RRot, animatable.getFlap2RRot()) * ((float)Math.PI / 180F));
        }

        if (name.equals("flapRV") || name.equals("flapLV")) {
            bone.setRotY(Mth.clamp(Mth.lerp(partialTick, animatable.flap3RotO, animatable.getFlap3Rot()), -20.0F, 20.0F) * ((float)Math.PI / 180F));
        }

        if (name.equals("gearF")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.gearRotO, (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT)) * (-(float)Math.PI / 180F));
        }
        if (name.equals("gearR")) {
            float degrees = Mth.lerp(partialTick, animatable.gearRotO,
                    (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT));

            bone.setRotX((degrees + 0.0F) * ((float)Math.PI / 180F));
        }
        if (name.equals("gearL")) {
            float degrees = Mth.lerp(partialTick, animatable.gearRotO,
                    (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT));

            bone.setRotX((degrees + 0.0F) * ((float)Math.PI / 180F));
        }

        if (name.equals("flapR")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap1LRotO, animatable.getFlap1LRot()) * (-(float)Math.PI / 180F));
        }

        if (name.equals("flapL")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap1RRotO, animatable.getFlap1RRot()) * (-(float)Math.PI / 180F));
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
