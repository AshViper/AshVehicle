package Aru.Aru.ashvehicle.client.renderer.entity;

import com.atsuishio.superbwarfare.entity.vehicle.Lav150Entity;
import com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import Aru.Aru.ashvehicle.client.model.vehicle.SapsanModel;
import Aru.Aru.ashvehicle.entity.vehicle.SapsanEntity;

public class SapsanRenderer extends GeoEntityRenderer<SapsanEntity> {
    public SapsanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SapsanModel());
    }

    public RenderType getRenderType(SapsanEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }

    public void preRender(PoseStack poseStack, SapsanEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = 1.5F;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void render(SapsanEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())));
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    public void renderRecursively(PoseStack poseStack, SapsanEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        for(int i= 1; i < 3; i++ ){
            if (name.equals("wheel" + i)) {
                bone.setRotY(Mth.lerp(partialTick, animatable.rudderRotO, animatable.getRudderRot()));
                bone.setRotX(1.5F * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot()));
            }
            if (name.equals("wheel" + (i + 5))) {
                bone.setRotY(Mth.lerp(partialTick, animatable.rudderRotO, animatable.getRudderRot()));
                bone.setRotX(1.5F * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot()));
            }
        }
        for(int i= 1; i < 4; i++ ){
            if (name.equals("wheel" + (i + 2))) {
                bone.setRotX(1.5F * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot()));
            }
            if (name.equals("wheel" + (i + 7))) {
                bone.setRotX(1.5F * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot()));
            }
        }

        if (name.equals("base")) {
            Player player = Minecraft.getInstance().player;
            bone.setHidden(ClientEventHandler.zoomVehicle && animatable.getFirstPassenger() == player);
            float a = (Float)animatable.getEntityData().get(MobileVehicleEntity.YAW);
            float r = (Mth.abs(a) - 90.0F) / 90.0F;
            bone.setPosZ(r * Mth.lerp(partialTick, (float)animatable.recoilShakeO, (float)animatable.getRecoilShake()) * 0.2F);
            bone.setRotX(r * Mth.lerp(partialTick, (float)animatable.recoilShakeO, (float)animatable.getRecoilShake()) * ((float)Math.PI / 180F) * 0.3F);
            float r2;
            if (Mth.abs(a) <= 90.0F) {
                r2 = a / 90.0F;
            } else if (a < 0.0F) {
                r2 = -(180.0F + a) / 90.0F;
            } else {
                r2 = (180.0F - a) / 90.0F;
            }

            bone.setPosX(r2 * Mth.lerp(partialTick, (float)animatable.recoilShakeO, (float)animatable.getRecoilShake()) * 0.15F);
            bone.setRotZ(r2 * Mth.lerp(partialTick, (float)animatable.recoilShakeO, (float)animatable.getRecoilShake()) * ((float)Math.PI / 180F) * 0.5F);
        }

        if (name.equals("missilepod")) {
            bone.setRotX(animatable.getPodRot() * ((float)Math.PI / 180F));
        }

        if (name.equals("cannon")) {
            Player player = Minecraft.getInstance().player;
            bone.setHidden(ClientEventHandler.zoomVehicle && animatable.getFirstPassenger() == player);
            bone.setRotY(Mth.lerp(partialTick, animatable.turretYRotO, animatable.getTurretYRot()) * ((float)Math.PI / 180F));
        }

        if (name.equals("barrel")) {
            float a = animatable.getTurretYaw(partialTick);
            float r = (Mth.abs(a) - 90.0F) / 90.0F;
            float r2;
            if (Mth.abs(a) <= 90.0F) {
                r2 = a / 90.0F;
            } else if (a < 0.0F) {
                r2 = -(180.0F + a) / 90.0F;
            } else {
                r2 = (180.0F - a) / 90.0F;
            }

            bone.setRotX(-Mth.lerp(partialTick, animatable.turretXRotO, animatable.getTurretXRot()) * ((float)Math.PI / 180F) - r * animatable.getPitch(partialTick) * ((float)Math.PI / 180F) - r2 * animatable.getRoll(partialTick) * ((float)Math.PI / 180F));
        }

        if (name.equals("flare")) {
            bone.setRotZ((float)((double)0.5F * (Math.random() - (double)0.5F)));
        }

        if (name.equals("flare2")) {
            bone.setRotZ((float)((double)0.5F * (Math.random() - (double)0.5F)));
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    protected float getDeathMaxRotation(Lav150Entity entityLivingBaseIn) {
        return 0.0F;
    }
}
