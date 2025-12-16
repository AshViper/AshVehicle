package Aru.Aru.ashvehicle.init.event;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.client.renderer.ThermalShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
        modid = AshVehicle.MODID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class ClientRenderEvents {

    // Nuclear explosion red sky effect
    private static long nukeStartTime = 0;
    private static int nukeDuration = 0; // in ticks
    private static Vec3 nukePosition = null;
    private static float nukeRadius = 0;

    /**
     * Activate red sky effect from nuclear explosion
     * @param pos Position of explosion
     * @param radius Effect radius
     * @param durationTicks How long the effect lasts
     */
    public static void activateNukeSky(Vec3 pos, float radius, int durationTicks) {
        nukeStartTime = System.currentTimeMillis();
        nukeDuration = durationTicks;
        nukePosition = pos;
        nukeRadius = radius;
    }

    /**
     * Red sky/fog effect for nuclear explosion
     */
    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        if (nukePosition == null || nukeDuration <= 0) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        long elapsed = System.currentTimeMillis() - nukeStartTime;
        float elapsedTicks = elapsed / 50f; // 50ms per tick
        
        if (elapsedTicks > nukeDuration) {
            // Effect ended
            nukePosition = null;
            nukeDuration = 0;
            return;
        }
        
        // Calculate distance from player to explosion
        double distance = mc.player.position().distanceTo(nukePosition);
        if (distance > nukeRadius) return; // Too far
        
        // Calculate intensity based on time and distance
        float timeProgress = elapsedTicks / nukeDuration;
        float distanceFactor = 1.0f - (float)(distance / nukeRadius);
        
        // Intensity curve: quick rise, slow fade
        float intensity;
        if (timeProgress < 0.1f) {
            // Quick rise (first 10%)
            intensity = timeProgress * 10f;
        } else {
            // Slow fade (remaining 90%)
            intensity = 1.0f - ((timeProgress - 0.1f) / 0.9f);
        }
        intensity *= distanceFactor;
        intensity = Math.max(0, Math.min(1, intensity));
        
        if (intensity > 0.01f) {
            // Blend current fog color with red/orange
            float currentRed = event.getRed();
            float currentGreen = event.getGreen();
            float currentBlue = event.getBlue();
            
            // Target: deep red-orange (0.9, 0.2, 0.05)
            float targetRed = 0.9f;
            float targetGreen = 0.15f;
            float targetBlue = 0.05f;
            
            // Lerp to red
            float newRed = currentRed + (targetRed - currentRed) * intensity;
            float newGreen = currentGreen + (targetGreen - currentGreen) * intensity;
            float newBlue = currentBlue + (targetBlue - currentBlue) * intensity;
            
            event.setRed(newRed);
            event.setGreen(newGreen);
            event.setBlue(newBlue);
        }
    }

    /**
     * エンティティ描画前（マスク初期化）
     */
    @SubscribeEvent
    public static void onRenderLevelPre(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            ThermalShaderManager.beginFrame();
        }
    }

    /**
     * ワールド描画完了後（サーマル合成）
     */
    @SubscribeEvent
    public static void onRenderLevelPost(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            ThermalShaderManager.applyThermalEffect();
        }
    }

    /**
     * 画面リサイズ検知（1.20.1方式）
     */
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        ThermalShaderManager.onResize(
                Minecraft.getInstance().getWindow().getWidth(),
                Minecraft.getInstance().getWindow().getHeight()
        );
    }
}
