package Aru.Aru.ashvehicle.client.renderer.entity;

import Aru.Aru.ashvehicle.client.model.vehicle.Rah66Model;
import Aru.Aru.ashvehicle.entity.vehicle.Rah66Entity;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

public class Rah66Renderer extends VehicleRenderer<Rah66Entity> {
    public Rah66Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Rah66Model());
        this.shadowRadius = 0.5F;
        float scale = 1.2f;
        scaleHeight = scale;
        scaleWidth = scale;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, Rah66Entity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String name = bone.getName();

        // Weapon bay animation - opens when missiles selected
        float bayRot = Mth.lerp(partialTick, animatable.weaponBayRotO, animatable.getWeaponBayRot());
        float bayRotRad = bayRot * ((float) Math.PI / 180F);

        float gearRot = Mth.lerp(partialTick, animatable.gearRot(1f), animatable.getGearRot());
        float gearRotRad = gearRot * ((float) Math.PI / 140F);
        float gearRotRad2 = (gearRot - 85f) * ((float)Math.PI / 180f);

        // Left weapon bay - rotates outward (negative Z rotation)
        if (name.equals("LeftWeaponsbay")) {
            bone.setRotZ(-bayRotRad);
        }

        // Right weapon bay - rotates outward (positive Z rotation)
        if (name.equals("RightWeaponsbay")) {
            bone.setRotZ(bayRotRad);
        }
        if (name.equals("LeftTyre")) {
            bone.setRotZ(gearRotRad);
        }
        if (name.equals("RightTyreHatch") || name.equals("BackRightTyreHatch")) {
            bone.setRotZ(-gearRotRad2);
        }

        if (name.equals("RightTyre")) {
            bone.setRotZ(-gearRotRad);
        }
        if (name.equals("LeftTyreHatch") || name.equals("BackLeftTyreHatch")) {
            bone.setRotZ(gearRotRad2);
        }

        if (name.equals("BackTyre")) {
            bone.setRotX(gearRotRad);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
