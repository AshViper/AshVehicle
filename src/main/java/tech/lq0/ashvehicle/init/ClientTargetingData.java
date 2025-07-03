package tech.lq0.ashvehicle.init;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class ClientTargetingData {
    private static Entity lockedTarget;

    @Nullable
    public static Entity getLockedTarget() {
        return lockedTarget;
    }

    public static void setLockedTarget(@Nullable Entity entity) {
        lockedTarget = entity;
    }

    public static void clearLockedTarget() {
        lockedTarget = null;
    }
}
