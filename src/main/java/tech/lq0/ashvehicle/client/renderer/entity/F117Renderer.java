package tech.lq0.ashvehicle.client.renderer.entity;

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
import tech.lq0.ashvehicle.client.model.B2Model;
import tech.lq0.ashvehicle.client.model.F117Model;
import tech.lq0.ashvehicle.entity.B2Entity;
import tech.lq0.ashvehicle.entity.F117Entity;

public class F117Renderer extends GeoEntityRenderer<F117Entity> {
    public F117Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F117Model());
        this.shadowRadius = 0.0f;
    }
    public RenderType getRenderType(F117Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }

    public void preRender(PoseStack poseStack,F117Entity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = 0.7F;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void render(F117Entity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())));
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    public void renderRecursively(PoseStack poseStack, F117Entity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();

        if (name.equals("LeftFlap")) {
            bone.setRotZ(Mth.lerp(partialTick, animatable.flap2LRotO, animatable.getFlap2LRot()) * (-(float)Math.PI / 180F));
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2LRotO, animatable.getFlap2LRot()) * (-(float)Math.PI / 180F));
        }

        if (name.equals("RightFlap")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2RRotO, animatable.getFlap2RRot()) * ((float)Math.PI / 180F));
            bone.setRotZ(Mth.lerp(partialTick, animatable.flap2RRotO, animatable.getFlap2RRot()) * (-(float)Math.PI / 180F));
        }

        if (name.equals("Tyre")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.gearRotO, (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT)) * ((float)Math.PI / 180F));
        }
        if (name.equals("ForwardTyreHatch")) {
            bone.setRotZ(Mth.lerp(partialTick, animatable.gearRotO, ((float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT))) * ((float)Math.PI / 180F));
        }
        if (name.equals("LeftTyre")) {
            float degrees = Mth.lerp(partialTick, animatable.gearRotO,
                    (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT));

            bone.setRotX((degrees + 0.0F) * ((float)Math.PI / 180F));
        }
        if (name.equals("RightTyre")) {
            float degrees = Mth.lerp(partialTick, animatable.gearRotO,
                    (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT));

            bone.setRotX((degrees + 0.0F) * ((float)Math.PI / 180F));
        }

        if (name.equals("RightTailPlane") || name.equals("LeftTailPlane")) {
            bone.setRotY(Mth.clamp(Mth.lerp(partialTick, animatable.flap3RotO, animatable.getFlap3Rot()), -10.0F, 10.0F) * ((float)Math.PI / 180F));
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
