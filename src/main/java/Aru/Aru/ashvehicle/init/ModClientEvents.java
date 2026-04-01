package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.client.overlay.TargetingCameraOverlay;
import Aru.Aru.ashvehicle.client.overlay.UCAVHudOverlay;
import net.neoforged.api.distmarker.Dist;
// import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
// import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
// import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Client event registration for AshVehicle
 */
// @EventBusSubscriber(modid = AshVehicle.MODID,  value = Dist.CLIENT)
public class ModClientEvents {

//    @SubscribeEvent
//    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
//        // Register UCAV HUD
//        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), UCAVHudOverlay.ID, new UCAVHudOverlay());
//
//        // Register Targeting Camera overlay (above UCAV HUD)
//        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), TargetingCameraOverlay.ID, new TargetingCameraOverlay());
//    }

    public static void registerOverlays() {
        // Placeholder until GUI overlay registration is migrated to 1.21.1 API.
    }
}
