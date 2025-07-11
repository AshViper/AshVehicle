package Aru.Aru.ashvehicle.init;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientTargetingData {
    private static final List<Entity> lockedTargets = new ArrayList<>();

    /** 複数ターゲットをロック */
    public static void setLockedTargets(List<Entity> entities) {
        lockedTargets.clear();
        if (entities != null) {
            lockedTargets.addAll(entities);
        }
    }

    /** 単一ターゲットをロック（複数対応と統一） */
    public static void setLockedTarget(@Nullable Entity entity) {
        if (entity != null) {
            setLockedTargets(List.of(entity));
        } else {
            clearLockedTargets();
        }
    }

    /** ロック解除 */
    public static void clearLockedTargets() {
        lockedTargets.clear();
    }

    /** 単一ロック取得：リストの最初を返す */
    @Nullable
    public static Entity getLockedTarget() {
        return lockedTargets.isEmpty() ? null : lockedTargets.get(0);
    }

    /** 複数ロック取得（読み取り専用） */
    public static List<Entity> getLockedTargets() {
        return Collections.unmodifiableList(lockedTargets);
    }

    /** 単一ロック解除（メソッド互換） */
    public static void clearLockedTarget() {
        clearLockedTargets();
    }
}