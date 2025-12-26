package Aru.Aru.ashvehicle.mixin;

import Aru.Aru.ashvehicle.client.renderer.ThermalEffectRenderer;
import Aru.Aru.ashvehicle.client.renderer.ThermalEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for thermal vision rendering.
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    /**
     * Initialize thermal buffers at start of level render.
     */
    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void ashvehicle$onRenderLevelStartThermal(PoseStack poseStack, float partialTick, long finishNanoTime,
                                     boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                                     LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        ThermalEffectRenderer.beginFrame();
    }
    
    /**
     * Render entity thermal mask and apply thermal composite at end of level render.
     */
    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void ashvehicle$onRenderLevelEndThermal(PoseStack poseStack, float partialTick, long finishNanoTime,
                                   boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                                   LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        // Render entities to thermal mask buffer
        ThermalEntityRenderer.renderEntityMask(poseStack, partialTick);
        // Apply thermal composite effect
        ThermalEffectRenderer.applyThermalEffect();
    }
}
