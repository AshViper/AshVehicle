package Aru.Aru.ashvehicle.client.layer;

import Aru.Aru.ashvehicle.entity.vehicle.EuroFighterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class EuroFighterGlowLayer extends GeoRenderLayer<EuroFighterEntity> {
    private static final ResourceLocation GLOW_TEXTURE = ResourceLocation.parse("ashvehicle:textures/entity/eurofighter-glow.png");

    public EuroFighterGlowLayer(GeoRenderer<EuroFighterEntity> renderer) {super(renderer);}

    @Override
    public void render(PoseStack poseStack, EuroFighterEntity animatable, BakedGeoModel bakedModel,
                       RenderType baseRenderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                       float partialTick, int packedLight, int packedOverlay) {

        // 🔸 発光用レンダータイプ（常に最大光量で描画）
        RenderType glowRenderType = RenderType.entityTranslucent(GLOW_TEXTURE);

        // 🔸 VertexConsumer を取得
        VertexConsumer glowBuffer = bufferSource.getBuffer(glowRenderType);

        float red = 1.0f;
        float green = 1.0f;
        float blue = 1.0f;
        float alpha = 1.0f;
        int color = net.minecraft.util.FastColor.ARGB32.color((int)(alpha * 255), (int)(red * 255), (int)(green * 255), (int)(blue * 255));

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
                color
        );
    }
}
