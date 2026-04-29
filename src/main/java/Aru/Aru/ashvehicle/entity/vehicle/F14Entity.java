package Aru.Aru.ashvehicle.entity.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.base.BaseAircraftEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class F14Entity extends BaseAircraftEntity {

    public F14Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    /*
     * =========================
     *   定数（調整用）
     * =========================
     */

    // 翼の最大後退角
    private static final float MAX_SWEEP = 45.0f;

    // 速度レンジ（ここは機体ごとに調整必須）
    private static final double MIN_SPEED = 2.0;
    private static final double MAX_SPEED = 4.5;

    // 補間速度
    private static final float SWEEP_LERP = 0.1f;

    /*
     * =========================
     *   データ同期
     * =========================
     */

    private static final EntityDataAccessor<Float> WING_SWEEP =
            SynchedEntityData.defineId(F14Entity.class, EntityDataSerializers.FLOAT);

    public float wingSweepO = 0f;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WING_SWEEP, 0.0F);
    }

    /*
     * =========================
     *   Getter / Setter
     * =========================
     */

    public void setWingSweep(float value) {
        // 安全にクランプ
        float clamped = Math.max(0f, Math.min(MAX_SWEEP, value));
        this.entityData.set(WING_SWEEP, clamped);
    }

    public float getWingSweep() {
        return this.entityData.get(WING_SWEEP);
    }

    /*
     * =========================
     *   コアロジック
     * =========================
     */

    private float getTargetWingSweep() {

        double speed = this.getDeltaMovement().length();

        // 正規化（0～1）
        double t = (speed - MIN_SPEED) / (MAX_SPEED - MIN_SPEED);
        t = Math.max(0.0, Math.min(1.0, t));

        // 0 → MAX_SWEEP に変換
        return (float)(t * MAX_SWEEP);
    }

    /*
     * =========================
     *   Tick処理
     * =========================
     */

    @Override
    public void baseTick() {
        super.baseTick();

        wingSweepO = getWingSweep();

        float target = getTargetWingSweep();
        float current = getWingSweep();

        // 補間（滑らかに）
        float next = current + (target - current) * SWEEP_LERP;

        setWingSweep(next);

        // アフターバーナー
        if (this.sprintInputDown() && this.level().isClientSide) {
            this.spawnAfterburnerEffect();
        }
    }

    /*
     * =========================
     *   パーティクル
     * =========================
     */

    @Override
    public List<Vec3> getAfterburnerParticlePositions() {
        List<Vec3> positions = new ArrayList<>();
        positions.add(new Vec3(-10, 2.0, -1));
        positions.add(new Vec3(-10, 2.0, 1));
        return positions;
    }
}