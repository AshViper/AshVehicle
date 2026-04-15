package Aru.Aru.ashvehicle.mixin;

import Aru.Aru.ashvehicle.accessor.OBBInfoAccessor;
import com.atsuishio.superbwarfare.data.vehicle.subdata.OBBInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VehicleEntity.class)
public abstract class VehicleEntityMixin {

    @Shadow
    public abstract List<OBBInfo> getOBB();

    @Shadow
    protected abstract Quaterniond getRotationFromString(String key);

    @Inject(method = "updateOBB", at = @At("TAIL"))
    private void onUpdateOBB(CallbackInfo ci) {
        for (OBBInfo obbInfo : this.getOBB()) {

            OBB obb = obbInfo.getOBB();

            // 元の回転をコピー（破壊的変更防止）
            Quaterniond rot = new Quaterniond(
                    this.getRotationFromString(obbInfo.rotation)
            );

            Vec3 angles = ((OBBInfoAccessor) obbInfo)
                    .superbwarfare$getRotationAngles();

            if (angles != null) {
                rot.mul(new Quaterniond().rotationYXZ(
                        Math.toRadians(angles.y), // Yaw
                        Math.toRadians(angles.x), // Pitch
                        Math.toRadians(angles.z)  // Roll
                ));
            }

            obb.setRotation(rot);
        }
    }
}