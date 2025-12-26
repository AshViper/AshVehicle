package Aru.Aru.ashvehicle.client.renderer;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.client.screen.TargetingCameraScreen;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Thermal vision renderer - FLIR style.
 * Background = dark grayscale, Entities = bright white.
 */
@OnlyIn(Dist.CLIENT)
public class ThermalEffectRenderer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ThermalEffectRenderer.class);
    private static final ResourceLocation THERMAL_SHADER = new ResourceLocation(AshVehicle.MODID, "shaders/post/thermal.json");
    
    @Nullable
    private static RenderTarget entityMaskTarget = null;
    @Nullable
    private static PostChain grayscaleShader = null;
    
    private static boolean initialized = false;
    private static int lastWidth = 0;
    private static int lastHeight = 0;
    
    /**
     * Check if thermal mode is active.
     */
    public static boolean isActive() {
        return TargetingCameraScreen.isThermalMode();
    }
    
    /**
     * Initialize or resize framebuffers.
     */
    public static void ensureInitialized() {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();
        
        if (!initialized || width != lastWidth || height != lastHeight) {
            cleanup();
            
            // Create entity mask framebuffer
            entityMaskTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            entityMaskTarget.setClearColor(0, 0, 0, 0);
            
            // Load grayscale post-processing shader
            try {
                grayscaleShader = new PostChain(
                    mc.getTextureManager(),
                    mc.getResourceManager(),
                    mc.getMainRenderTarget(),
                    THERMAL_SHADER
                );
                grayscaleShader.resize(width, height);
                LOGGER.info("Thermal shader loaded successfully");
            } catch (IOException e) {
                LOGGER.error("Failed to load thermal shader", e);
                grayscaleShader = null;
            }
            
            lastWidth = width;
            lastHeight = height;
            initialized = true;
        }
    }
    
    /**
     * Get the entity mask render target for entity rendering pass.
     */
    @Nullable
    public static RenderTarget getEntityMaskTarget() {
        return entityMaskTarget;
    }
    
    /**
     * Called before world render to set up thermal pass.
     */
    public static void beginFrame() {
        if (!isActive()) return;
        ensureInitialized();

        if (entityMaskTarget != null) {
            entityMaskTarget.bindWrite(true);

            RenderSystem.disableScissor();
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.depthMask(true);
            RenderSystem.clearColor(0.0f, 0.0f, 0.0f, 0.0f);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT, Minecraft.ON_OSX);

            Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        }
    }

    /**
     * Apply thermal effect after world render, before GUI.
     */
    public static void applyThermalEffect() {
        if (!isActive()) return;
        
        Minecraft mc = Minecraft.getInstance();
        RenderTarget mainTarget = mc.getMainRenderTarget();
        
        // Step 1: Apply grayscale + darkening to main scene via shader
        if (grayscaleShader != null) {
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            grayscaleShader.process(mc.getFrameTime());
            mainTarget.bindWrite(false);
        }
        
        // Step 2: Composite entity mask (white entities) on top
        if (entityMaskTarget != null) {
            compositeEntityMask(mainTarget);
        }
        
        // Rebind main target
        mainTarget.bindWrite(true);
    }
    
    /**
     * Composite the white entity mask on top of the scene.
     */
    private static void compositeEntityMask(RenderTarget mainTarget) {
        if (entityMaskTarget == null) return;
        
        mainTarget.bindWrite(false);
        
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        // Additive blending for white glow effect
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        
        ShaderInstance shader = GameRenderer.getPositionTexShader();
        if (shader == null) return;
        
        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderTexture(0, entityMaskTarget.getColorTextureId());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        int width = mainTarget.width;
        int height = mainTarget.height;
        
        Matrix4f matrix = new Matrix4f().setOrtho(0, width, height, 0, 1000, 3000);
        RenderSystem.setProjectionMatrix(matrix, VertexSorting.ORTHOGRAPHIC_Z);
        
        Matrix4f modelView = new Matrix4f().translation(0, 0, -2000);
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().last().pose().set(modelView);
        RenderSystem.applyModelViewMatrix();
        
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(0, height, 0).uv(0, 0).endVertex();
        buffer.vertex(width, height, 0).uv(1, 0).endVertex();
        buffer.vertex(width, 0, 0).uv(1, 1).endVertex();
        buffer.vertex(0, 0, 0).uv(0, 1).endVertex();
        BufferUploader.drawWithShader(buffer.end());
        
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
    }
    
    /**
     * Cleanup resources.
     */
    public static void cleanup() {
        if (entityMaskTarget != null) {
            entityMaskTarget.destroyBuffers();
            entityMaskTarget = null;
        }
        if (grayscaleShader != null) {
            grayscaleShader.close();
            grayscaleShader = null;
        }
        initialized = false;
    }
    
    /**
     * Called when window is resized.
     */
    public static void onResize(int width, int height) {
        if (initialized && (width != lastWidth || height != lastHeight)) {
            cleanup();
        }
    }
}
