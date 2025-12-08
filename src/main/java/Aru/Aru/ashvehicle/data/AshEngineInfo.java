package Aru.Aru.ashvehicle.data;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleEngineUtils;
import com.google.gson.annotations.SerializedName;
import org.joml.Math;

public class AshEngineInfo extends EngineInfo.Helicopter {

    @SerializedName("GearRotateAngle")
    public float gearRotateAngle = 85.0F;

    @SerializedName("HasGear")
    public boolean hasGear = true;

    public AshEngineInfo() {
        super();
    }

    @Override
    public void work(VehicleEntity vehicle) {
        // 元のヘリ挙動
        VehicleEngineUtils.helicopterEngine(vehicle, this);

        // ギア挙動（自分で追加）
        if (hasGear) {
            if (vehicle.upInputDown()) {
                vehicle.setUpInputDown(false);
                if ((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) == 0.0F && !vehicle.onGround()) {
                    vehicle.getEntityData().set(VehicleEntity.GEAR_UP, true);
                } else if ((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) == 1.0F) {
                    vehicle.getEntityData().set(VehicleEntity.GEAR_UP, false);
                }
            }

            if (vehicle.onGround()) {
                vehicle.getEntityData().set(VehicleEntity.GEAR_UP, false);
            }

            if ((Boolean)vehicle.getEntityData().get(VehicleEntity.GEAR_UP)) {
                vehicle.getEntityData().set(VehicleEntity.GEAR_ROT, org.joml.Math.min((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) + 0.05F, 1.0F));
            } else {
                vehicle.getEntityData().set(VehicleEntity.GEAR_ROT, Math.max((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) - 0.05F, 0.0F));
            }

            vehicle.setGearRot((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) * gearRotateAngle);
        }
    }
}
