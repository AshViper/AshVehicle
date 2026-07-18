package Aru.Aru.ashvehicle.mixin;

import Aru.Aru.ashvehicle.accessor.OBBInfoAccessor;
import com.atsuishio.superbwarfare.data.vehicle.subdata.OBBInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VehicleEntity.class)
public abstract class VehicleEntityMixin {

    @Inject(method = "updateOBB", at = @At("TAIL"))
    private void onUpdateOBB(CallbackInfo ci) {
        VehicleEntity vehicle = (VehicleEntity) (Object) this;
        List<OBBInfo> obbInfos = vehicle.getObb();
        List<OBB> obbs = vehicle.getOBBs();

        if (obbInfos != null && obbs != null && obbInfos.size() == obbs.size()) {
            for (int i = 0; i < obbs.size(); i++) {
                OBBInfo obbInfo = obbInfos.get(i);
                OBB obb = obbs.get(i);

                Vec3 angles = ((OBBInfoAccessor) (Object) obbInfo).superbwarfare$getRotationAngles();
                if (angles != null) {
                    Quaterniond currentRot = new Quaterniond(obb.rotation());

                    Quaterniond additionalRot = new Quaterniond()
                            .rotateY(Math.toRadians(angles.y))
                            .rotateX(Math.toRadians(angles.x))
                            .rotateZ(Math.toRadians(angles.z));

                    currentRot.mul(additionalRot);

                    obb.updateRotation(currentRot);
                }
            }
        }
    }
}
