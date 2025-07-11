package Aru.Aru.ashvehicle.client.renderer.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import Aru.Aru.ashvehicle.client.layer.F35GlowLayer;
import Aru.Aru.ashvehicle.client.model.F35Model;
import Aru.Aru.ashvehicle.entity.F35Entity;

public class F35Renderer extends GeoEntityRenderer<F35Entity> {
    public F35Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F35Model());
        this.shadowRadius = 0.0f;
        this.addRenderLayer(new F35GlowLayer(this));
    }
    public RenderType getRenderType(F35Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }

    public void preRender(PoseStack poseStack, F35Entity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = 1.3F;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void render(F35Entity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())));
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    public void renderRecursively(PoseStack poseStack, F35Entity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();

        if (name.equals("flapLB")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2LRotO, animatable.getFlap2LRot()) * ((float)Math.PI / 180F));
        }

        if (name.equals("flapRB")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2RRotO, animatable.getFlap2RRot()) * ((float)Math.PI / 180F));
        }

        if (name.equals("flapRV")) {
            bone.setRotY(Mth.clamp(Mth.lerp(partialTick, animatable.flap3RotO, animatable.getFlap3Rot()), -20.0F, 20.0F) * ((float)Math.PI / 180F));
        }

        if (name.equals("flapLV")) {
            bone.setRotY(Mth.clamp(Mth.lerp(partialTick, animatable.flap3RotO, animatable.getFlap3Rot()), -20.0F, 20.0F) * ((float)Math.PI / 180F));
        }

        if (name.equals("gearF")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.gearRotO, (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT)) * ((float)Math.PI / 180F));
        }
        if (name.equals("gearR") || name.equals("gearL")) {
            float degrees = Mth.lerp(partialTick, animatable.gearRotO,
                    (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT));

            bone.setRotX((degrees) * ((float)Math.PI / 180F));
        }
        if ( name.equals("gearFL")) {
            float degrees = Mth.lerp(partialTick, animatable.gearRotO,
                    (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT));

            bone.setRotZ((degrees - 90) * (-(float)Math.PI / 180F));
        }
        if (name.equals("gearFR")) {
            float degrees = Mth.lerp(partialTick, animatable.gearRotO,
                    (float)(Integer)animatable.getEntityData().get(MobileVehicleEntity.GEAR_ROT));

            bone.setRotZ((degrees - 90) * ((float)Math.PI / 180F));
        }

        if (name.equals("flapR")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap1LRotO, animatable.getFlap1LRot()) * (-(float)Math.PI / 180F));
        }

        if (name.equals("flapL")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap1RRotO, animatable.getFlap1RRot()) * (-(float)Math.PI / 180F));
        }

        if (name.equals("jassm-xr")) {
            bone.setHidden((Integer)animatable.getEntityData().get(F35Entity.LOADED_ROCKET) < 4);
        }

        if (name.equals("jassm-xr2")) {
            bone.setHidden((Integer)animatable.getEntityData().get(F35Entity.LOADED_ROCKET) < 3);
        }

        if (name.equals("jassm-xr4")) {
            bone.setHidden((Integer)animatable.getEntityData().get(F35Entity.LOADED_ROCKET) < 2);
        }

        if (name.equals("jassm-xr3")) {
            bone.setHidden((Integer)animatable.getEntityData().get(F35Entity.LOADED_ROCKET) < 1);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public boolean shouldRender(F35Entity entity, Frustum camera, double camX, double camY, double camZ) {
        return true; // ← これで常に描画されるように
    }
}
