package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.MH60MModel;
import Aru.Aru.ashvehicle.client.model.vehicle.V22Model;
import Aru.Aru.ashvehicle.entity.vehicle.MH60MEntity;
import Aru.Aru.ashvehicle.entity.vehicle.V22Entity;
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

public class V22Renderer extends GeoEntityRenderer<V22Entity> {
    public V22Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new V22Model());
    }
    public RenderType getRenderType(V22Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }

    public void preRender(PoseStack poseStack, V22Entity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = 1.0F;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void render(V22Entity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())));
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    public void renderRecursively(PoseStack poseStack, V22Entity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        if (name.equals("bone10")) {
            bone.setRotY(-Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }

        if (name.equals("bone2")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }

        if (name.equals("LeftPropeller")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.gearRotO, (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT)) * ((float)Math.PI / 180F));
        }

        if (name.equals("RightPropeller")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.gearRotO, (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT)) * ((float)Math.PI / 180F));
        }

        if (name.equals("Tyre")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.gearRotO, (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT)) * ((float)Math.PI / 180F));
        }

        if (name.equals("RightTyre")) {
            float degrees = Mth.lerp(partialTick, animatable.gearRotO,
                    (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT));

            bone.setRotX((degrees + 0.0F) * (-(float)Math.PI / 180F));
        }
        if (name.equals("LeftTyre")) {
            float degrees = Mth.lerp(partialTick, animatable.gearRotO,
                    (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT));

            bone.setRotX((degrees + 0.0F) * (-(float)Math.PI / 180F));
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}