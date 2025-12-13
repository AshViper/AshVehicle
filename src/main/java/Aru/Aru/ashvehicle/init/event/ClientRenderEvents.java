package Aru.Aru.ashvehicle.init.event;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.client.renderer.ThermalShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
        modid = AshVehicle.MODID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class ClientRenderEvents {

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
