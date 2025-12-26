package Aru.Aru.ashvehicle.mixin;

import Aru.Aru.ashvehicle.client.screen.TargetingCameraScreen;
import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import Aru.Aru.ashvehicle.tools.DroneFindUtil;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.Monitor;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import org.joml.Matrix4d;
import org.joml.Vector4d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for camera - moves camera to drone when player controls it via monitor.
 * Supports targeting mode with independent camera angle.
 */
@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 0), cancellable = true)
    private void ashvehicle$onSetup(BlockGetter level, Entity entity, boolean detached, boolean mirrored, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ModItems.MONITOR.get())) return;
        if (!stack.getOrCreateTag().getBoolean("Using")) return;
        if (!stack.getOrCreateTag().getBoolean("Linked")) return;

        String droneUUID = stack.getOrCreateTag().getString(Monitor.LINKED_DRONE);
        RemoteDroneEntity drone = DroneFindUtil.findRemoteDrone(player.level(), droneUUID);
        
        if (drone == null) return;

        boolean firstPerson = mc.options.getCameraType() == CameraType.FIRST_PERSON 
                           || mc.options.getCameraType() == CameraType.THIRD_PERSON_BACK;

        if (firstPerson) {
            // Transform camera position relative to drone
            Matrix4d transform = ashvehicle$getDroneTransform(drone, partialTicks);
            
            // Camera offset
            double x0 = 0;
            double y0 = 0.5;
            double z0 = 0.3;

            Vector4d worldPosition = ashvehicle$transformPosition(transform, x0, y0, z0);

            float yaw, pitch;
            
            if (TargetingCameraScreen.isActive()) {
                // Targeting mode - camera looks independently from drone
                yaw = TargetingCameraScreen.getCameraYaw();
                pitch = TargetingCameraScreen.getCameraPitch();
            } else {
                // Normal mode - camera follows drone
                yaw = Mth.lerp(partialTicks, drone.yRotO, drone.getYRot());
                pitch = Mth.lerp(partialTicks, drone.xRotO, drone.getXRot());
            }

            setPosition(worldPosition.x, worldPosition.y, worldPosition.z);
            setRotation(yaw, pitch);
            ci.cancel();
        }
    }

    @Unique
    private static Matrix4d ashvehicle$getDroneTransform(RemoteDroneEntity drone, float ticks) {
        Matrix4d transform = new Matrix4d();
        transform.translate(
            Mth.lerp(ticks, drone.xo, drone.getX()), 
            Mth.lerp(ticks, drone.yo, drone.getY()), 
            Mth.lerp(ticks, drone.zo, drone.getZ())
        );
        transform.rotate(Axis.YP.rotationDegrees(-Mth.lerp(ticks, drone.yRotO, drone.getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, drone.xRotO, drone.getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(drone.getRoll()));
        return transform;
    }

    @Unique
    private static Vector4d ashvehicle$transformPosition(Matrix4d transform, double x, double y, double z) {
        return transform.transform(new Vector4d(x, y, z, 1));
    }
}
