package Aru.Aru.ashvehicle.mixin;

import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to prevent dismounting from drone with sneak/alt
 */
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "isShiftKeyDown", at = @At("HEAD"), cancellable = true)
    private void ashvehicle$preventDismount(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        Entity vehicle = self.getVehicle();
        
        // If riding our drone, don't allow shift to dismount
        if (vehicle instanceof RemoteDroneEntity) {
            cir.setReturnValue(false);
        }
    }
}
