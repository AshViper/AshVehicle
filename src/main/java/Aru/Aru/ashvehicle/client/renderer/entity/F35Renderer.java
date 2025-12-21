package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.F35Model;
import Aru.Aru.ashvehicle.entity.vehicle.F35Entity;
import Aru.Aru.ashvehicle.entity.vehicle.SapsanEntity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class F35Renderer extends VehicleRenderer<F35Entity> {
    public F35Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F35Model());
        this.shadowRadius = 0.5F;
        float scale = 1.1f;
        scaleHeight = scale;
        scaleWidth = scale;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, F35Entity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();
        float bayRot = Mth.lerp(partialTick, animatable.weaponBayRotO, animatable.getWeaponBayRot());
        float bayRotRad = bayRot * ((float) Math.PI / 180F);
        // Animate missile pod rotation with smooth interpolation
        if (name.equals("FVtolHacth") || name.equals("engine"))  {
            float podRot = Mth.lerp(partialTick, animatable.vtolRotO, animatable.getPodRot());
            bone.setRotX(podRot * ((float) Math.PI / 180F));
        }
        if (name.equals("FRVtolHacth") || name.equals("FLVtolHacth2") || name.equals("BLVtolHacth"))  {
            float podRot = Mth.lerp(partialTick, animatable.vtolRotO, animatable.getPodRot());
            bone.setRotZ(podRot * ((float) -Math.PI / 180F));
        }
        if (name.equals("FLVtolHacth") || name.equals("FRVtolHacth2") || name.equals("BRVtolHacth"))  {
            float podRot = Mth.lerp(partialTick, animatable.vtolRotO, animatable.getPodRot());
            bone.setRotZ(podRot * ((float) Math.PI / 180F));
        }
        if (name.equals("group3") || name.equals("group2")) {
            bone.setRotZ(-bayRotRad);
        }

        // Right weapon bay - rotates outward (positive Z rotation)
        if (name.equals("group4") || name.equals("group")) {
            bone.setRotZ(bayRotRad);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
