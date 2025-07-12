package Aru.Aru.ashvehicle.client.layer;

import Aru.Aru.ashvehicle.entity.weapon.NapalmBombEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class NapalmBombLayer extends GeoRenderLayer<NapalmBombEntity> {
    private static final ResourceLocation LAYER = new ResourceLocation("superbwarfare", "textures/entity/rpg_rocket_e.png");

    public NapalmBombLayer(GeoRenderer<NapalmBombEntity> entityRenderer) {
        super(entityRenderer);
    }

    public void render(PoseStack poseStack, NapalmBombEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType glowRenderType = RenderType.eyes(LAYER);
        this.getRenderer().reRender(this.getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
