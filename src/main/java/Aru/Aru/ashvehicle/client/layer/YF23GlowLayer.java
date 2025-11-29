package Aru.Aru.ashvehicle.client.layer;

import Aru.Aru.ashvehicle.entity.vehicle.YF23Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class YF23GlowLayer extends GeoRenderLayer<YF23Entity> {
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("ashvehicle", "textures/entity/yf-23_glow.png");

    public YF23GlowLayer(GeoRenderer<YF23Entity> renderer) {super(renderer);}

    @Override
    public void render(PoseStack poseStack, YF23Entity animatable, BakedGeoModel bakedModel,
                       RenderType baseRenderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                       float partialTick, int packedLight, int packedOverlay) {

        // 🔸 発光用レンダータイプ（常に最大光量で描画）
        RenderType glowRenderType = RenderType.eyes(GLOW_TEXTURE);

        // 🔸 VertexConsumer を取得
        VertexConsumer glowBuffer = bufferSource.getBuffer(glowRenderType);

        // 🔸 モデル描画（GeckoLib の正しい呼び出し方法）
        this.getRenderer().reRender(
                bakedModel,
                poseStack,
                bufferSource,
                animatable,
                glowRenderType,
                glowBuffer,
                partialTick,
                0xF000F0,         // packedLight
                packedOverlay,
                1.0f, 1.0f, 1.0f, 1.0f // RGBA
        );
    }
}
