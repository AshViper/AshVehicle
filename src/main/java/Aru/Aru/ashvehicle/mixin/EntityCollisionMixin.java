package Aru.Aru.ashvehicle.mixin;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityCollisionMixin {

    @Shadow public abstract AABB getBoundingBox();
    @Shadow public abstract boolean onGround();
    @Shadow public abstract void setYRot(float yRot);
    @Shadow public abstract float getYRot();
    @Shadow public abstract Entity getRootVehicle();
    @Shadow public abstract boolean isPassenger();

    @Unique
    private VehicleEntity ashvehicle$currentPlatform = null;

    @Unique
    private boolean ashvehicle$shouldIgnore(Entity entity, VehicleEntity vehicle) {
        if (entity == vehicle) return true;
        Entity entityRoot = entity.getRootVehicle();
        Entity vehicleRoot = vehicle.getRootVehicle();
        if (entityRoot == vehicleRoot) return true;
        if (entity.getVehicle() == vehicle || vehicle.getVehicle() == entity) return true;
        return false;
    }

    /**
     * Step 1: Follow Platform Movement（改善版）
     */
    @ModifyVariable(method = "collide", at = @At("HEAD"), argsOnly = true)
    private Vec3 ashvehicle$followPlatformMovement(Vec3 movement) {
        Entity entity = (Entity) (Object) this;

        if (this.isPassenger() || !(entity instanceof Player || entity instanceof VehicleEntity)) {
            return movement;
        }

        List<Entity> nearby = entity.level().getEntities(entity, entity.getBoundingBox().inflate(20.0));

        ashvehicle$currentPlatform = null;

        for (Entity e : nearby) {
            if (!(e instanceof VehicleEntity vehicle)) continue;
            if (ashvehicle$shouldIgnore(entity, vehicle)) continue;

            List<OBB> obbs = vehicle.getOBBs();
            if (obbs == null) continue;

            for (OBB obb : obbs) {

                // 接地判定を少し安定させる
                if (OBB.isColliding(obb, entity.getBoundingBox().inflate(0.2, 0.6, 0.2).move(0, -0.2, 0))) {

                    ashvehicle$currentPlatform = vehicle;

                    // ===== 修正①：実移動差分を使用 =====
                    Vec3 platformMove = vehicle.position().subtract(vehicle.xo, vehicle.yo, vehicle.zo);

                    // ===== 修正②：回転補正（前tick基準） =====
                    float deltaYaw = vehicle.getYRot() - vehicle.yRotO;
                    if (Math.abs(deltaYaw) > 0.001f) {

                        Vec3 relativePrev = entity.position().subtract(new Vec3(vehicle.xo, vehicle.yo, vehicle.zo));
                        Vec3 relativeNow = relativePrev.yRot((float) Math.toRadians(-deltaYaw));
                        Vec3 rotMovement = relativeNow.subtract(relativePrev);

                        platformMove = platformMove.add(rotMovement);
                    }

                    // ===== 既存仕様維持（上方向は除外） =====
                    double dy = platformMove.y < 0 ? platformMove.y : 0;

                    return movement.add(platformMove.x, dy, platformMove.z);
                }
            }
        }

        return movement;
    }

    /**
     * Step 2: Orient with Platform（変更なし）
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickHead(CallbackInfo ci) {
        if (ashvehicle$currentPlatform != null && ashvehicle$currentPlatform.isAlive()) {
            if (!this.isPassenger()) {
                float deltaYaw = ashvehicle$currentPlatform.getYRot() - ashvehicle$currentPlatform.yRotO;
                if (Math.abs(deltaYaw) > 0.001f) {
                    this.setYRot(this.getYRot() + deltaYaw);
                }
            }
        }
    }

    /**
     * Step 3: Vertical Stabilization（変更なし）
     */
    @Inject(method = "collide", at = @At("RETURN"), cancellable = true)
    private void onCollideReturn(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        Entity entity = (Entity) (Object) this;

        if (this.isPassenger() || !(entity instanceof Player || entity instanceof VehicleEntity)) {
            return;
        }

        Vec3 adjusted = cir.getReturnValue();
        List<Entity> nearby = entity.level().getEntities(entity, entity.getBoundingBox().inflate(20.0));

        double yPush = 0;
        AABB predictedBox = entity.getBoundingBox().move(adjusted);

        for (Entity e : nearby) {
            if (!(e instanceof VehicleEntity vehicle)) continue;
            if (ashvehicle$shouldIgnore(entity, vehicle)) continue;

            List<OBB> obbs = vehicle.getOBBs();
            if (obbs == null) continue;

            for (OBB obb : obbs) {
                if (OBB.isColliding(obb, predictedBox.move(0, yPush, 0))) {
                    double low = 0;
                    double high = 1.0;

                    if (!OBB.isColliding(obb, predictedBox.move(0, high, 0))) {
                        for (int i = 0; i < 12; i++) {
                            double mid = (low + high) / 2.0;
                            if (OBB.isColliding(obb, predictedBox.move(0, mid, 0))) low = mid;
                            else high = mid;
                        }
                        yPush += high + 0.001;
                        break;
                    }
                }
            }
        }

        if (yPush > 0) {
            cir.setReturnValue(adjusted.add(0, yPush, 0));
        }
    }
}