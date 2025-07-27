package Aru.Aru.ashvehicle.client.layer;

import Aru.Aru.ashvehicle.entity.vehicle.F16Entity;
import Aru.Aru.ashvehicle.entity.vehicle.F22Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class F16GlowLayer extends GeoRenderLayer<F16Entity> {
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("ashvehicle", "textures/entity/f-16-glow.png");

    public F16GlowLayer(GeoRenderer<F16Entity> renderer) {super(renderer);}

    @Override
    public void render(PoseStack poseStack, F16Entity animatable, BakedGeoModel bakedModel,
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
