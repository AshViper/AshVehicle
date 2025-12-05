package Aru.Aru.ashvehicle.client.renderer.projectile;

import Aru.Aru.ashvehicle.client.model.projectile.ToiletBombModel;
import Aru.Aru.ashvehicle.entity.projectile.ToiletBombEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ToiletBombRenderer extends GeoEntityRenderer<ToiletBombEntity> {
    public ToiletBombRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ToiletBombModel());
        //this.addRenderLayer(new Ru9m336MissileLayer(this));
    }

    public RenderType getRenderType(ToiletBombEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }

    public void render(ToiletBombEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }
}
