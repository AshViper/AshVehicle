package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.client.overlay.TargetingCameraOverlay;
import Aru.Aru.ashvehicle.client.overlay.UCAVHudOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client event registration for AshVehicle
 */
@Mod.EventBusSubscriber(modid = AshVehicle.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        // Register UCAV HUD
        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), UCAVHudOverlay.ID, new UCAVHudOverlay());
        
        // Register Targeting Camera overlay (above UCAV HUD)
        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), TargetingCameraOverlay.ID, new TargetingCameraOverlay());
    }
}
