package Aru.Aru.ashvehicle.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renders entities as white silhouettes for thermal vision.
 */
@OnlyIn(Dist.CLIENT)
public class ThermalEntityRenderer {

    private static final ResourceLocation WHITE_TEXTURE = new ResourceLocation("textures/misc/white.png");
    private static final RenderType THERMAL_WHITE = RenderType.entityTranslucent(WHITE_TEXTURE);
    private static final int THERMAL_ALPHA = 200;

    /**
     * Render all visible entities as white shapes to the entity mask buffer.
     */
    public static void renderEntityMask(PoseStack poseStack, float partialTicks) {
        if (!ThermalEffectRenderer.isActive()) return;
        
        RenderTarget maskTarget = ThermalEffectRenderer.getEntityMaskTarget();
        if (maskTarget == null) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        
        // Bind entity mask framebuffer
        maskTarget.bindWrite(true);
        
        // Clear with transparent black
        RenderSystem.clearColor(0, 0, 0, 0);
        RenderSystem.clear(16384 | 256, Minecraft.ON_OSX);
        
        // Enable depth test for proper 3D rendering
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        
        // Limit search distance for performance
        double renderDist = Math.min(mc.options.renderDistance().get() * 16.0, 128.0);
        AABB searchBox = new AABB(
            cameraPos.x - renderDist, cameraPos.y - renderDist, cameraPos.z - renderDist,
            cameraPos.x + renderDist, cameraPos.y + renderDist, cameraPos.z + renderDist
        );
        
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        EntityRenderDispatcher entityRenderer = mc.getEntityRenderDispatcher();
        
        poseStack.pushPose();
        
        for (Entity entity : mc.level.getEntities(null, searchBox)) {
            if (!shouldRenderThermal(entity, mc.player)) continue;
            
            double x = entity.getX() - cameraPos.x;
            double y = entity.getY() - cameraPos.y;
            double z = entity.getZ() - cameraPos.z;
            
            renderWhiteEntity(poseStack, bufferSource, entityRenderer, entity, x, y, z, partialTicks);
        }
        
        bufferSource.endBatch();
        poseStack.popPose();
        
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        
        mc.getMainRenderTarget().bindWrite(true);
    }
    
    private static boolean shouldRenderThermal(Entity entity, Player viewer) {
        if (entity == viewer) return false;
        if (entity.isInvisible()) return false;
        if (!entity.isAlive()) return false;
        if (entity instanceof Projectile) return false;
        
        if (entity instanceof LivingEntity) return true;
        
        String className = entity.getClass().getName();
        return className.contains("VehicleEntity") || className.contains("vehicle");
    }
    
    private static void renderWhiteEntity(PoseStack poseStack, MultiBufferSource bufferSource,
                                         EntityRenderDispatcher entityRenderer, Entity entity,
                                         double x, double y, double z, float partialTicks) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        
        try {
            MultiBufferSource whiteBuffer = new WhiteBufferSource(bufferSource);
            entityRenderer.render(entity, 0, 0, 0, entity.getYRot(), partialTicks, poseStack, whiteBuffer, 15728880);
        } catch (Exception ignored) {
        }
        
        poseStack.popPose();
    }
    
    private static class WhiteBufferSource implements MultiBufferSource {
        private final MultiBufferSource wrapped;
        
        public WhiteBufferSource(MultiBufferSource wrapped) {
            this.wrapped = wrapped;
        }
        
        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            return new WhiteVertexConsumer(wrapped.getBuffer(THERMAL_WHITE));
        }
    }
    
    private static class WhiteVertexConsumer implements VertexConsumer {
        private final VertexConsumer wrapped;
        
        public WhiteVertexConsumer(VertexConsumer wrapped) {
            this.wrapped = wrapped;
        }
        
        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            return wrapped.vertex(x, y, z);
        }
        
        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            return wrapped.color(255, 255, 255, THERMAL_ALPHA);
        }
        
        @Override
        public VertexConsumer uv(float u, float v) {
            return wrapped.uv(u, v);
        }
        
        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            return wrapped.overlayCoords(OverlayTexture.NO_OVERLAY);
        }
        
        @Override
        public VertexConsumer uv2(int u, int v) {
            return wrapped.uv2(15728880);
        }
        
        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return wrapped.normal(x, y, z);
        }
        
        @Override
        public void endVertex() {
            wrapped.endVertex();
        }
        
        @Override
        public void defaultColor(int r, int g, int b, int a) {
            wrapped.defaultColor(255, 255, 255, THERMAL_ALPHA);
        }
        
        @Override
        public void unsetDefaultColor() {
            wrapped.unsetDefaultColor();
        }
    }
}
