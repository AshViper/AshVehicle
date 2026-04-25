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

    /**
     * Optimized check to ignore self-collisions and passenger-vehicle collisions.
     */
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
     * Step 1: Follow Platform Movement
     */
    @ModifyVariable(method = "collide", at = @At("HEAD"), argsOnly = true)
    private Vec3 ashvehicle$followPlatformMovement(Vec3 movement) {
        Entity entity = (Entity) (Object) this;
        
        // CRITICAL FIX: If the player is already riding (isPassenger), Minecraft handles the sync.
        // Adding platform movement here would cause a double-move desync, closing inventories.
        if (this.isPassenger() || !(entity instanceof Player || entity instanceof VehicleEntity)) {
            return movement;
        }
        
        // Search for platforms in a large radius
        List<Entity> nearby = entity.level().getEntities(entity, entity.getBoundingBox().inflate(200.0));
        
        ashvehicle$currentPlatform = null;
        for (Entity e : nearby) {
            if (!(e instanceof VehicleEntity vehicle)) continue;
            if (ashvehicle$shouldIgnore(entity, vehicle)) continue;
            
            List<OBB> obbs = vehicle.getOBBs();
            if (obbs == null) continue;
            for (OBB obb : obbs) {
                // Check if standing on it
                if (OBB.isColliding(obb, entity.getBoundingBox().inflate(0.1, 0.2, 0.1).move(0, -0.1, 0))) {
                    ashvehicle$currentPlatform = vehicle;
                    
                    Vec3 platformMove = vehicle.getDeltaMovement();
                    float deltaYaw = vehicle.getYRot() - vehicle.yRotO;
                    if (Math.abs(deltaYaw) > 0.001f) {
                        Vec3 relativePos = entity.position().subtract(vehicle.position());
                        Vec3 rotatedPos = relativePos.yRot((float)Math.toRadians(-deltaYaw));
                        Vec3 rotMovement = rotatedPos.subtract(relativePos);
                        platformMove = platformMove.add(rotMovement);
                    }
                    return movement.add(platformMove);
                }
            }
        }
        return movement;
    }

    /**
     * Step 2: Orient with Platform
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickHead(CallbackInfo ci) {
        if (ashvehicle$currentPlatform != null && ashvehicle$currentPlatform.isAlive()) {
            // Only apply if NOT riding (redundant check but safe)
            if (!this.isPassenger()) {
                float deltaYaw = ashvehicle$currentPlatform.getYRot() - ashvehicle$currentPlatform.yRotO;
                if (Math.abs(deltaYaw) > 0.001f) {
                    this.setYRot(this.getYRot() + deltaYaw);
                }
            }
        }
    }

    /**
     * Step 3: Vertical Stabilization (Push Up)
     */
    @Inject(method = "collide", at = @At("RETURN"), cancellable = true)
    private void onCollideReturn(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        Entity entity = (Entity) (Object) this;
        
        // Skip if riding
        if (this.isPassenger() || !(entity instanceof Player || entity instanceof VehicleEntity)) {
            return;
        }

        Vec3 adjusted = cir.getReturnValue();
        List<Entity> nearby = entity.level().getEntities(entity, entity.getBoundingBox().inflate(200.0));

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
                    }
                }
            }
        }

        if (yPush > 0) {
            cir.setReturnValue(adjusted.add(0, yPush, 0));
        }
    }
}
