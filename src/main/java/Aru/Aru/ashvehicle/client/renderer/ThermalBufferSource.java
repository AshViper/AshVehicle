package Aru.Aru.ashvehicle.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * サーマルビジョン用の MultiBufferSource ラッパー。
 * 
 * エンティティ描画中にこのラッパーを使うことで、
 * 全ての頂点の色をフルブライトの白色に強制します。
 * これにより、エンティティがサーマルカメラで「熱源」として白く光って見えます。
 */
@OnlyIn(Dist.CLIENT)
public class ThermalBufferSource implements MultiBufferSource {

    private final MultiBufferSource delegate;
   public ThermalBufferSource(MultiBufferSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        // 元のバッファを取得して、色を白に強制するラッパーを返す
        VertexConsumer original = delegate.getBuffer(renderType);
        return new WhiteVertexConsumer(original);
    }

    /**
     * 全ての頂点カラーを白 (255,255,255) に、ライトをフルブライト (240,240) に強制するラッパー。
     */
    private static class WhiteVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;

        WhiteVertexConsumer(VertexConsumer delegate) {
            this.delegate = delegate;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            return delegate.vertex(x, y, z);
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            // 強制的に白色にする
            return delegate.color(255, 255, 255, a);
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            return delegate.uv(u, v);
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            // NO_OVERLAY を使用してダメージ赤色フラッシュなどを無効化
            return delegate.overlayCoords(0, 10);
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            // フルブライトに強制 (15 << 4 | 15 << 20 = 全方向最大光量)
            return delegate.uv2(240, 240);
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return delegate.normal(x, y, z);
        }

        @Override
        public void endVertex() {
            delegate.endVertex();
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {
            delegate.defaultColor(255, 255, 255, a);
        }

        @Override
        public void unsetDefaultColor() {
            delegate.unsetDefaultColor();
        }
    }
}
