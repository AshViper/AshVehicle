package tech.lq0.ashvehicle.client.renderer.entity;

import com.atsuishio.superbwarfare.client.layer.vehicle.SpeedBoatHeatLayer;
import com.atsuishio.superbwarfare.client.layer.vehicle.SpeedBoatLayer;
import com.atsuishio.superbwarfare.client.layer.vehicle.SpeedBoatPowerLayer;
import com.atsuishio.superbwarfare.client.model.entity.SpeedboatModel;
import com.atsuishio.superbwarfare.entity.WaterMaskEntity;
import com.atsuishio.superbwarfare.entity.vehicle.SpeedboatEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.lq0.ashvehicle.client.model.ZumwaltModel;
import tech.lq0.ashvehicle.entity.GepardEntity;
import tech.lq0.ashvehicle.entity.ZumwaltEntity;

public class ZumwaltRenderer extends GeoEntityRenderer<ZumwaltEntity> {
    public ZumwaltRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ZumwaltModel());
    }

    public RenderType getRenderType(ZumwaltEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }

    public void preRender(PoseStack poseStack, ZumwaltEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = 1.0F;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void render(ZumwaltEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        Vec3 root = new Vec3((double)0.0F, 0.9, (double)0.0F);
        poseStack.rotateAround(Axis.YP.rotationDegrees(-entityYaw), (float)root.x, (float)root.y, (float)root.z);
        poseStack.rotateAround(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())), (float)root.x, (float)root.y, (float)root.z);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())), (float)root.x, (float)root.y, (float)root.z);
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.pushPose();
        poseStack.scale(2.4F, 0.4F, 4.05F);
        poseStack.translate((double)0.0F, (double)1.5F, -0.22);
        Entity entity = new WaterMaskEntity((EntityType) ModEntities.WATER_MASK.get(), entityIn.level());
        this.entityRenderDispatcher.render(entity, (double)0.0F, (double)0.0F, (double)0.0F, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
        poseStack.popPose();
    }
    public void renderRecursively(PoseStack poseStack, ZumwaltEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();

        for(int i = 1; i < 3; ++i) {
            if (name.equals("turret" + i)) {
                Player player = Minecraft.getInstance().player;
                bone.setHidden(ClientEventHandler.zoomVehicle && animatable.getFirstPassenger() == player);
                bone.setRotY(Mth.lerp(partialTick, animatable.turretYRotO, animatable.getTurretYRot()) * ((float)Math.PI / 180F));
            }
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}