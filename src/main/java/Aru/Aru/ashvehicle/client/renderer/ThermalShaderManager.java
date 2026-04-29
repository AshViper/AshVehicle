package Aru.Aru.ashvehicle.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Client-side thermal vision manager.
 *
 * 2段階でサーマルビジョンを実現します：
 * 1. PostChain (thermal.fsh) でシーン全体をグレースケール化
 * 2. EntityRenderThermalMixin で LivingEntity の描画を白色に強制
 */
@OnlyIn(Dist.CLIENT)
public class ThermalShaderManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThermalShaderManager.class);
    private static final ResourceLocation THERMAL_SHADER = new ResourceLocation("ashvehicle", "shaders/post/thermal.json");

    @Nullable
    private static PostChain grayscaleShader = null;

    private static boolean initialized = false;
    private static int lastWidth = 0;
    private static int lastHeight = 0;
    private static boolean enabled = false;

    // エンティティ描画中かどうかのフラグ（Mixinから使用）
    private static boolean renderingEntity = false;

    /**
     * Initialize or resize the post-processing shader.
     */
    public static void ensureInitialized() {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();

        if (!initialized || width != lastWidth || height != lastHeight) {
            cleanup();

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
     * Called before world render to set up thermal pass.
     */
    public static void beginFrame() {
        if (!enabled) return;
        ensureInitialized();
    }

    /**
     * Apply thermal effect after world render, before GUI.
     * グレースケール化のみ行う。エンティティの白色化は Mixin 側で処理。
     */
    public static void applyThermalEffect() {
        if (!enabled) return;

        Minecraft mc = Minecraft.getInstance();

        if (grayscaleShader != null) {
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            grayscaleShader.process(mc.getFrameTime());
            mc.getMainRenderTarget().bindWrite(true);
        }
    }

    /**
     * Mixinからエンティティ描画中のフラグを設定する。
     */
    public static void markEntityRendering(boolean rendering) {
        renderingEntity = rendering;
    }

    /**
     * エンティティが描画中かどうか。
     */
    public static boolean isRenderingEntity() {
        return renderingEntity;
    }

    /**
     * Cleanup resources.
     */
    public static void cleanup() {
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
            // Will reinitialize on next frame
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void enable() {
        enabled = true;
    }

    public static void disable() {
        enabled = false;
        cleanup(); // FBO と PostChain を確実に解放
    }
}