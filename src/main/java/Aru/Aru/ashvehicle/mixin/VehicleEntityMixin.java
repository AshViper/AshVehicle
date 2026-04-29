package Aru.Aru.ashvehicle.mixin;

import Aru.Aru.ashvehicle.accessor.OBBInfoAccessor;
import com.atsuishio.superbwarfare.data.vehicle.subdata.OBBInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VehicleEntity.class)
public abstract class VehicleEntityMixin {

    @Inject(method = "updateOBB", at = @At("TAIL"))
    private void onUpdateOBB(CallbackInfo ci) {
        VehicleEntity vehicle = (VehicleEntity) (Object) this;
        List<OBBInfo> obbInfos = vehicle.getOBB();
        List<OBB> obbs = vehicle.getOBBs();

        if (obbInfos != null && obbs != null && obbInfos.size() == obbs.size()) {
            for (int i = 0; i < obbs.size(); i++) {
                OBBInfo obbInfo = obbInfos.get(i);
                OBB obb = obbs.get(i);

                Vec3 angles = ((OBBInfoAccessor) obbInfo).superbwarfare$getRotationAngles();
                if (angles != null) {
                    // Start with the vehicle's current OBB rotation (which already includes vehicle orientation)
                    Quaterniond currentRot = new Quaterniond(obb.rotation());

                    // Create the additional rotation from OBBInfo
                    Quaterniond additionalRot = new Quaterniond()
                            .rotateY(Math.toRadians(angles.y))
                            .rotateX(Math.toRadians(angles.x))
                            .rotateZ(Math.toRadians(angles.z));

                    // Multiply them to combine vehicle rotation + custom OBB rotation
                    currentRot.mul(additionalRot);

                    // Update the OBB with the combined rotation
                    obb.setRotation(currentRot);
                }
            }
        }
    }

    /**
     * @author Antigravity
     * @reason Extend inventory interaction range for large aircraft/ships.
     */
    @Overwrite(remap = false)
    public boolean stillValid(Player player) {
        VehicleEntity vehicle = (VehicleEntity) (Object) this;
        // If the player is riding the vehicle, it's always valid.
        if (player.getVehicle() == vehicle) {
            return true;
        }
        // Otherwise, allow a much larger distance (e.g., 64 blocks) for massive ships.
        return player.distanceToSqr(vehicle) < 4096.0; // 64 * 64
    }
}