package Aru.Aru.ashvehicle.client.event;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.Packet.DroneMouseMovePacket;
import Aru.Aru.ashvehicle.client.screen.TargetingCameraScreen;
import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import Aru.Aru.ashvehicle.init.ModNetwork;
import com.atsuishio.superbwarfare.client.MouseMovementHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Mouse movement handler for drone camera control
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = AshVehicle.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DroneMouseHandler {

    private static Vec2 posO = new Vec2(0, 0);
    private static Vec2 posN = new Vec2(0, 0);
    private static double lerpSpeedX = 0;
    private static double lerpSpeedY = 0;

    private static boolean notInGame() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return true;
        if (mc.getOverlay() != null) return true;
        if (mc.screen != null) return true;
        if (!mc.mouseHandler.isMouseGrabbed()) return true;
        return !mc.isWindowActive();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        // Update mouse position
        posO = posN;
        posN = MouseMovementHandler.getMousePos();

        // Check if controlling our drone
        RemoteDroneEntity drone = DroneControlHandler.getControlledDrone(player);
        if (drone == null) {
            lerpSpeedX = 0;
            lerpSpeedY = 0;
            return;
        }

        if (notInGame()) {
            ModNetwork.INSTANCE.sendToServer(new DroneMouseMovePacket(0, 0));
            lerpSpeedX = 0;
            lerpSpeedY = 0;
            return;
        }

        // Calculate mouse speed with zoom factor
        double sensitivity = 0.15;
        float zoom = TargetingCameraScreen.isActive() ? TargetingCameraScreen.getCurrentZoom() : 1.0f;
        
        double speedX = (sensitivity / zoom) * (posN.x - posO.x);
        double speedY = (sensitivity / zoom) * (posN.y - posO.y);

        // In targeting mode - move camera locally, don't control drone direction
        if (TargetingCameraScreen.isActive()) {
            TargetingCameraScreen.handleRawMouseInput(speedX * 15.0, speedY * 15.0);
            // Don't send to server - drone flies on its own
            ModNetwork.INSTANCE.sendToServer(new DroneMouseMovePacket(0, 0));
            return;
        }

        // Smooth interpolation
        double mouseSpeed = 0.5;
        lerpSpeedX = Mth.lerp(mouseSpeed, lerpSpeedX, speedX);
        lerpSpeedY = Mth.lerp(mouseSpeed, lerpSpeedY, speedY);

        // Send to server
        ModNetwork.INSTANCE.sendToServer(new DroneMouseMovePacket(lerpSpeedX, lerpSpeedY));
    }

    /**
     * Handle scroll for zoom
     */
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        RemoteDroneEntity drone = DroneControlHandler.getControlledDrone(player);
        if (drone == null) return;

        // Only handle scroll when targeting camera is active
        if (TargetingCameraScreen.isActive()) {
            double delta = event.getScrollDelta();
            TargetingCameraScreen.handleScroll(delta);
            event.setCanceled(true);
        }
    }
}
