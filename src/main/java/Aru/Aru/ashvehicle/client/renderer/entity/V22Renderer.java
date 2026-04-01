package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.V22Model;
import Aru.Aru.ashvehicle.entity.vehicle.F35Entity;
import Aru.Aru.ashvehicle.entity.vehicle.V22Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class V22Renderer extends VehicleRenderer<V22Entity> {
    public V22Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new V22Model());
        this.shadowRadius = 0.5F;
        float scale = 1.0f;
        scaleHeight = scale;
        scaleWidth = scale;
    }

    // @Override
    public void renderRecursively(PoseStack poseStack, V22Entity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();
        float bayRot = Mth.lerp(partialTick, animatable.weaponBayRotO, animatable.getWeaponBayRot());
        float bayRotRad = bayRot * ((float) Math.PI / 180F);
        // Animate missile pod rotation with smooth interpolation
        if (name.equals("LeftPropeller") || name.equals("RightPropeller"))  {
            float podRot = Mth.lerp(partialTick, animatable.vtolRotO, animatable.getPodRot());
            bone.setRotX(podRot * ((float) Math.PI / 180F));
        }
        int color = net.minecraft.util.FastColor.ARGB32.color((int) (alpha * 255), (int) (red * 255), (int) (green * 255), (int) (blue * 255));
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
            partialTick, packedLight, packedOverlay, color);
    }
}