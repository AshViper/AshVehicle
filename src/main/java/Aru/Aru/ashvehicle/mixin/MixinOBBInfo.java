package Aru.Aru.ashvehicle.mixin;

import Aru.Aru.ashvehicle.accessor.OBBInfoAccessor;
import com.atsuishio.superbwarfare.data.vehicle.subdata.OBBInfo;
import com.google.gson.annotations.SerializedName;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(OBBInfo.class)
public class MixinOBBInfo implements OBBInfoAccessor {

    @Unique
    @SerializedName("RotationAngles")
    private Vec3 rotationAngles;

    @Override
    public Vec3 superbwarfare$getRotationAngles() {
        return this.rotationAngles;
    }

    @Override
    public void superbwarfare$setRotationAngles(Vec3 v) {
        this.rotationAngles = v;
    }
}