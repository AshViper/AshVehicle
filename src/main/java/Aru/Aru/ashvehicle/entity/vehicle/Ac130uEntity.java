package Aru.Aru.ashvehicle.entity.vehicle;

import Aru.Aru.ashvehicle.entity.vehicle.base.BaseAircraftEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Ac130uEntity extends BaseAircraftEntity {
    private Vec3 orbitCenter = null;
    private double orbitRadius = 60.0;
    private boolean orbitMode = false;

    // 旋回速度・飛行パラメータ
    private static final float  ORBIT_YAW_SPEED          = -1.0f;   // 度/tick、旋回の鋭さ

    public Ac130uEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public boolean isOrbiting() { return orbitMode; }

    public void startOrbit(Vec3 center, double radius) {
        this.orbitCenter = center;
        this.orbitRadius = radius;
        this.orbitMode   = true;
    }

    public void stopOrbit() {
        this.orbitMode = false;
        // 入力リセット
        this.setForwardInputDown(false);
        this.setBackInputDown(false);
        this.setLeftInputDown(false);
        this.setRightInputDown(false);
        this.setUpInputDown(false);
        this.setDownInputDown(false);
        this.setSprintInputDown(false);
        // 姿勢リセット
        this.setZRot(0.0f);
    }

    // =========================================================
    // travel() をオーバーライド
    //   → 軌道モード中は入力を書き換えてから親のエンジン処理に渡す
    // =========================================================
    @Override
    public void travel() {
        if (!level().isClientSide && orbitMode && orbitCenter != null) {
            applyOrbitInput();
        }
        super.travel(); // 親のエンジン処理（AIRCRAFT エンジンが入力を読む）
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && orbitMode) {
            if (this.getPassengers().isEmpty()) {
                stopOrbit();
            }
        }
    }

    private void applyOrbitInput() {
        Vec3 pos = this.position();

        // ===== 水平距離・方向 =====
        double dx = orbitCenter.x - pos.x;
        double dz = orbitCenter.z - pos.z;
        double hDist = Math.sqrt(dx * dx + dz * dz);
        if (hDist < 0.001) return;

        // ===== Yaw を毎tick直接左旋回 =====
        // エンジンがYawを上書きする前にここで進める
        float currentYaw = this.getYRot();
        float newYaw     = currentYaw + ORBIT_YAW_SPEED;
        this.yRotO = currentYaw;   // 前フレーム値を保持（補間用）
        this.setYRot(newYaw);

        // ===== 常に前進（スプリントでフル出力）=====
        this.setForwardInputDown(true);
        this.setBackInputDown(false);

        this.setLeftInputDown(this.getRoll() >= computeBankAngle());

        // ===== 高度維持（直接制御）=====
        double targetY = orbitCenter.y;
        double dy = targetY - pos.y;

        // P制御 + 軽いダンピング
        double vy = Mth.clamp(dy * 0.05 - this.getDeltaMovement().y * 0.1, -0.3, 0.3);

        this.setDeltaMovement(
                this.getDeltaMovement().x,
                vy,
                this.getDeltaMovement().z
        );
    }

    private float computeBankAngle() {
        double h = getGroundDistance(); // 地面までの高さ
        double r = orbitRadius;

        if (r < 0.001) return 0.0f;

        double angleDeg = Math.toDegrees(Math.atan(h / r)) * 0.8;

        // 左旋回なのでマイナス
        return (float)-Mth.clamp(angleDeg, 0, 60);
    }

    private double getGroundDistance() {
        Vec3 start = this.position();
        Vec3 end = start.add(0, -1000, 0);

        HitResult result = this.level().clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));

        if (result.getType() == HitResult.Type.BLOCK) {
            double height = start.y - result.getLocation().y;
            //LOGGER.info("Ground distance: {}", height);
            return height;
        }

        return 0;
    }
}