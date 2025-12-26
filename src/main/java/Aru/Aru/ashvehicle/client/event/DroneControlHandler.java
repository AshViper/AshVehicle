package Aru.Aru.ashvehicle.client.event;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.Packet.DroneExitPacket;
import Aru.Aru.ashvehicle.Packet.DroneFirePacket;
import Aru.Aru.ashvehicle.Packet.DroneGearPacket;
import Aru.Aru.ashvehicle.Packet.DroneInputPacket;
import Aru.Aru.ashvehicle.client.screen.TargetingCameraScreen;
import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import Aru.Aru.ashvehicle.init.ModKeyBindings;
import Aru.Aru.ashvehicle.init.ModNetwork;
import Aru.Aru.ashvehicle.tools.DroneFindUtil;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.Monitor;
import com.atsuishio.superbwarfare.tools.TraceTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side drone control handler for AshVehicle
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = AshVehicle.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DroneControlHandler {

    // Previous key states
    private static boolean lastForward = false;
    private static boolean lastBackward = false;
    private static boolean lastLeft = false;
    private static boolean lastRight = false;
    private static boolean lastUp = false;
    private static boolean lastDown = false;
    
    // Firing
    private static int fireTickCounter = 0;
    
    // Key states for toggle detection
    private static boolean lastTargetingKey = false;
    private static boolean lastThermalKey = false;
    private static boolean lastLockKey = false;
    private static boolean lastGearKey = false;
    private static boolean lastExitKey = false;
    private static boolean lastZoomInKey = false;
    private static boolean lastZoomOutKey = false;

    private static boolean notInGame() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return true;
        if (mc.getOverlay() != null) return true;
        if (mc.screen != null) return true;
        if (!mc.mouseHandler.isMouseGrabbed()) return true;
        return !mc.isWindowActive();
    }

    /**
     * Check if player is controlling our drone
     */
    public static RemoteDroneEntity getControlledDrone(LocalPlayer player) {
        if (player == null) return null;
        
        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ModItems.MONITOR.get())) return null;
        if (!stack.getOrCreateTag().getBoolean("Using")) return null;
        if (!stack.getOrCreateTag().getBoolean("Linked")) return null;
        
        String droneUUID = stack.getOrCreateTag().getString(Monitor.LINKED_DRONE);
        return DroneFindUtil.findRemoteDrone(player.level(), droneUUID);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        
        if (player == null) return;
        
        RemoteDroneEntity drone = getControlledDrone(player);
        if (drone == null) {
            // Reset state if not controlling drone
            if (lastForward || lastBackward || lastLeft || lastRight || lastUp || lastDown) {
                sendInputPacket(false, false, false, false, false, false);
                resetInputState();
            }
            // Close targeting camera if drone lost
            if (TargetingCameraScreen.isActive()) {
                TargetingCameraScreen.close();
            }
            return;
        }

        // Block Alt key dismount - check if Alt is pressed and cancel
        long window = mc.getWindow().getWindow();
        boolean altPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS 
                          || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
        // Alt is handled by blocking dismount in mixin

        if (notInGame()) {
            sendInputPacket(false, false, false, false, false, false);
            resetInputState();
            return;
        }

        // Handle all keys
        handleCameraKeys(drone);
        handleGearKey();
        handleExitKey();
        
        // Tick targeting camera
        TargetingCameraScreen.tick();

        // Get current key states
        boolean forward = mc.options.keyUp.isDown();
        boolean backward = mc.options.keyDown.isDown();
        boolean left = mc.options.keyLeft.isDown();
        boolean right = mc.options.keyRight.isDown();
        boolean up = mc.options.keyJump.isDown();
        boolean down = mc.options.keyShift.isDown();

        // Send input packet every tick while controlling
        sendInputPacket(forward, backward, left, right, up, down);
        
        lastForward = forward;
        lastBackward = backward;
        lastLeft = left;
        lastRight = right;
        lastUp = up;
        lastDown = down;

        // Handle firing (LMB)
        handleFiring(mc, player, drone);
    }

    /**
     * Handle drone firing
     */
    private static void handleFiring(Minecraft mc, LocalPlayer player, RemoteDroneEntity drone) {
        boolean attackKeyDown = mc.options.keyAttack.isDown();
        
        if (attackKeyDown) {
            fireTickCounter++;
            
            // Fire every 2 ticks (10 shots per second max)
            if (fireTickCounter >= 2) {
                fireTickCounter = 0;
                
                Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
                Vec3 lookVec = drone.getViewVector(1.0f);
                
                // Получаем UUID залоченной цели из таргетинг камеры
                java.util.UUID lockedTargetUUID = TargetingCameraScreen.getLockedTargetUUID();
                
                // Find entity under crosshair (если нет лока)
                Entity targetEntity = null;
                if (lockedTargetUUID == null) {
                    targetEntity = TraceTool.droneFindLookingEntity(drone, cameraPos, 512, 1.0f);
                }
                
                // Find block under crosshair
                BlockHitResult blockResult = drone.level().clip(new ClipContext(
                    cameraPos,
                    cameraPos.add(lookVec.scale(512)),
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    drone
                ));
                
                Vector3f targetPos = null;
                if (blockResult != null) {
                    Vec3 hitPos = blockResult.getLocation();
                    targetPos = new Vector3f((float) hitPos.x, (float) hitPos.y, (float) hitPos.z);
                }
                
                // Send fire packet - приоритет залоченной цели
                java.util.UUID finalTargetUUID = lockedTargetUUID != null ? lockedTargetUUID : 
                    (targetEntity != null ? targetEntity.getUUID() : null);
                
                ModNetwork.INSTANCE.sendToServer(new DroneFirePacket(finalTargetUUID, targetPos));
            }
        } else {
            fireTickCounter = 0;
        }
    }

    private static void sendInputPacket(boolean forward, boolean backward, 
                                        boolean left, boolean right, 
                                        boolean up, boolean down) {
        ModNetwork.INSTANCE.sendToServer(
            new DroneInputPacket(forward, backward, left, right, up, down)
        );
    }

    private static void resetInputState() {
        lastForward = false;
        lastBackward = false;
        lastLeft = false;
        lastRight = false;
        lastUp = false;
        lastDown = false;
    }

    /**
     * Handle targeting camera, thermal, lock, zoom keys
     */
    private static void handleCameraKeys(RemoteDroneEntity drone) {
        // Targeting camera toggle (T by default)
        boolean targetingKey = ModKeyBindings.TARGETING_CAMERA.isDown();
        if (targetingKey && !lastTargetingKey) {
            TargetingCameraScreen.toggle(drone);
        }
        lastTargetingKey = targetingKey;

        // Only handle other keys when targeting camera is active
        if (TargetingCameraScreen.isActive()) {
            // Thermal toggle (N by default)
            boolean thermalKey = ModKeyBindings.THERMAL_TOGGLE.isDown();
            if (thermalKey && !lastThermalKey) {
                TargetingCameraScreen.toggleThermal();
            }
            lastThermalKey = thermalKey;

            // Lock toggle (X by default)
            boolean lockKey = ModKeyBindings.LOCK_TARGET.isDown();
            if (lockKey && !lastLockKey) {
                TargetingCameraScreen.toggleLock();
            }
            lastLockKey = lockKey;

            // Zoom in (= by default)
            boolean zoomInKey = ModKeyBindings.ZOOM_IN.isDown();
            if (zoomInKey && !lastZoomInKey) {
                TargetingCameraScreen.zoomIn();
            }
            lastZoomInKey = zoomInKey;

            // Zoom out (- by default)
            boolean zoomOutKey = ModKeyBindings.ZOOM_OUT.isDown();
            if (zoomOutKey && !lastZoomOutKey) {
                TargetingCameraScreen.zoomOut();
            }
            lastZoomOutKey = zoomOutKey;
        } else {
            lastThermalKey = false;
            lastLockKey = false;
            lastZoomInKey = false;
            lastZoomOutKey = false;
        }
    }

    /**
     * Handle landing gear toggle (G by default)
     */
    private static void handleGearKey() {
        boolean gearKey = ModKeyBindings.TOGGLE_GEAR.isDown();
        if (gearKey && !lastGearKey) {
            ModNetwork.INSTANCE.sendToServer(new DroneGearPacket());
        }
        lastGearKey = gearKey;
    }

    /**
     * Handle exit drone (R by default)
     */
    private static void handleExitKey() {
        boolean exitKey = ModKeyBindings.EXIT_DRONE.isDown();
        if (exitKey && !lastExitKey) {
            ModNetwork.INSTANCE.sendToServer(new DroneExitPacket());
            TargetingCameraScreen.close();
        }
        lastExitKey = exitKey;
    }
}
