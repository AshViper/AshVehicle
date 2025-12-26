package Aru.Aru.ashvehicle.mixin;

import Aru.Aru.ashvehicle.client.event.DroneControlHandler;
import Aru.Aru.ashvehicle.client.screen.TargetingCameraScreen;
import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for GameRenderer - applies zoom when controlling drone
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void ashvehicle$modifyFov(net.minecraft.client.Camera camera, float partialTicks, boolean useFovSetting, CallbackInfoReturnable<Double> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        RemoteDroneEntity drone = DroneControlHandler.getControlledDrone(mc.player);
        if (drone == null) return;

        // Only apply zoom when targeting camera is active
        if (TargetingCameraScreen.isActive()) {
            float zoom = TargetingCameraScreen.getCurrentZoom();
            if (zoom > 1.0f) {
                double originalFov = cir.getReturnValue();
                double newFov = originalFov / zoom;
                cir.setReturnValue(newFov);
            }
        }
    }
}
