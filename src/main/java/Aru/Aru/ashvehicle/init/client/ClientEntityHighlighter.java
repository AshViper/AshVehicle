package Aru.Aru.ashvehicle.init.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.vehicle.GepardEntity;
import com.atsuishio.superbwarfare.init.ModEntities;
import Aru.Aru.ashvehicle.entity.vehicle.ZumwaltEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = AshVehicle.MODID, value = Dist.CLIENT)
public class ClientEntityHighlighter {

    // 定数を事前計算してキャッシュ
    private static final float BULLET_SPEED = 35f;
    private static final double DETECTION_RANGE = 1028.0;
    private static final double MIN_Z_THRESHOLD = 0.01;
    private static final double EPSILON = 1e-6;
    private static final int STEALTH_SIZE = 6;
    private static final int NORMAL_SIZE = 10;
    private static final int LOCKED_COLOR = 0xFFFFFF00; // 黄色
    private static final int UNLOCKED_COLOR = 0xFF00FF00; // 緑
    private static final int INTERCEPT_COLOR = 0xFFFF0000; // 赤
    private static final float INTERCEPT_ROTATION = 35f;
    private static final int MAX_TARGETS = 4;

    // 遅延初期化用のフラグ
    private static Set<EntityType<?>> excludedTypes = null;
    private static Set<EntityType<?>> stealthTypes = null;

    private static final Set<String> ALLOWED_NAMESPACES = Set.of("superbwarfare", "ashvehicle", "vvp");

    // キャッシュ用変数（フレーム間で再利用）
    private static Vec3 cachedShooterPos = Vec3.ZERO;
    private static double cachedFovScale = 0;
    private static int cachedScreenWidth = 0;
    private static int cachedScreenHeight = 0;

    // ターゲット情報のキャッシュ（ガベージコレクション削減）
    private static final List<TargetCache> targetCaches = new ArrayList<>(MAX_TARGETS);
    static {
        for (int i = 0; i < MAX_TARGETS; i++) {
            targetCaches.add(new TargetCache());
        }
    }

    // ターゲット情報をキャッシュするクラス（オブジェクト生成を削減）
    private static class TargetCache {
        Entity entity;
        double distanceSq;
        Vec3 screenPos;
        Vec3 interceptScreenPos;
        boolean isStealth;
        boolean isLocked;
        int size;

        void reset() {
            entity = null;
            screenPos = null;
            interceptScreenPos = null;
        }
    }

    // 遅延初期化メソッド（初回アクセス時のみ実行）
    private static void ensureInitialized() {
        if (excludedTypes == null) {
            excludedTypes = Set.of(
                    ModEntities.SMALL_CANNON_SHELL.get(),
                    ModEntities.SMALL_ROCKET.get(),
                    ModEntities.CANNON_SHELL.get(),
                    ModEntities.GUN_GRENADE.get(),
                    ModEntities.PROJECTILE.get(),
                    ModEntities.AGM_65.get(),
                    ModEntities.JAVELIN_MISSILE.get(),
                    ModEntities.HAND_GRENADE.get(),
                    ModEntities.RGO_GRENADE.get(),
                    ModEntities.MELON_BOMB.get(),
                    ModEntities.MORTAR_SHELL.get(),
                    ModEntities.MORTAR.get(),
                    ModEntities.LASER.get(),
                    ModEntities.FLARE_DECOY.get(),
                    ModEntities.SMOKE_DECOY.get(),
                    ModEntities.CLAYMORE.get(),
                    ModEntities.BLU_43.get(),
                    ModEntities.TM_62.get(),
                    ModEntities.TASER_BULLET.get(),
                    ModEntities.MK_82.get(),
                    ModEntities.MK_42.get()
            );
        }

        if (stealthTypes == null) {
            stealthTypes = Set.of(
                    Aru.Aru.ashvehicle.init.ModEntities.F_35.get(),
                    Aru.Aru.ashvehicle.init.ModEntities.B_2.get(),
                    Aru.Aru.ashvehicle.init.ModEntities.F_22.get(),
                    Aru.Aru.ashvehicle.init.ModEntities.F_117.get(),
                    Aru.Aru.ashvehicle.init.ModEntities.SU_57.get()
            );
        }
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        // 初回のみ初期化
        ensureInitialized();

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        Entity vehicle = player.getVehicle();
        if (vehicle == null) return;

        // 早期リターンの最適化
        if (!isAllowedVehicle(vehicle)) return;

        List<Entity> lockedTargets = ClientTargetingData.getLockedTargets();

        // 画面サイズとFOVをキャッシュ
        cachedScreenWidth = mc.getWindow().getGuiScaledWidth();
        cachedScreenHeight = mc.getWindow().getGuiScaledHeight();
        float fov = mc.options.fov().get();
        double fovRad = Math.toRadians(fov);
        cachedFovScale = cachedScreenHeight / (2.0 * Math.tan(fovRad / 2.0));

        // 射撃位置をキャッシュ
        cachedShooterPos = new Vec3(player.getX(), player.getEyeY(), player.getZ());

        // カメラ変換を一度だけ計算
        Camera camera = mc.gameRenderer.getMainCamera();
        CameraTransform camTransform = new CameraTransform(camera);

        // ターゲット検索範囲を最適化（AABBを直接作成）
        AABB searchBox = player.getBoundingBox().inflate(DETECTION_RANGE);
        EntityType<?> playerType = player.getType();

        // ターゲット収集と距離計算を同時に実行（ソート不要の最適化）
        int validCount = 0;
        for (TargetCache cache : targetCaches) {
            cache.reset();
        }

        // 最も近い4つのターゲットを効率的に探す（ソート不要）
        for (Entity e : vehicle.level().getEntities(player, searchBox,
                entity -> isValidTarget(entity, player, vehicle, playerType))) {

            double distSq = e.distanceToSqr(player);

            // 最大4つまで、距離が近い順に挿入
            int insertPos = -1;
            for (int i = 0; i < MAX_TARGETS; i++) {
                if (targetCaches.get(i).entity == null) {
                    insertPos = i;
                    break;
                } else if (distSq < targetCaches.get(i).distanceSq) {
                    insertPos = i;
                    break;
                }
            }

            if (insertPos != -1) {
                // 挿入位置以降をシフト
                for (int i = MAX_TARGETS - 1; i > insertPos; i--) {
                    TargetCache current = targetCaches.get(i);
                    TargetCache prev = targetCaches.get(i - 1);
                    current.entity = prev.entity;
                    current.distanceSq = prev.distanceSq;
                }

                // 新しいターゲットを挿入
                TargetCache cache = targetCaches.get(insertPos);
                cache.entity = e;
                cache.distanceSq = distSq;

                if (insertPos >= validCount) {
                    validCount = insertPos + 1;
                }
            }
        }

        if (validCount == 0) return;

        // 描画準備（事前計算を一括実行）
        for (int i = 0; i < validCount; i++) {
            TargetCache cache = targetCaches.get(i);
            Entity target = cache.entity;

            // ターゲット情報を事前計算
            cache.isStealth = stealthTypes.contains(target.getType());
            cache.isLocked = lockedTargets.contains(target);
            cache.size = cache.isStealth ? STEALTH_SIZE : NORMAL_SIZE;

            // スクリーン座標を計算
            Vec3 targetPos = target.getBoundingBox().getCenter();
            cache.screenPos = worldToScreenOptimized(targetPos, camTransform);

            if (cache.screenPos != null) {
                // 迎撃点の計算
                Vec3 targetVelocity = target.getDeltaMovement();
                Vec3 interceptPoint = calculateInterceptPointOptimized(cachedShooterPos, targetPos, targetVelocity);
                cache.interceptScreenPos = worldToScreenOptimized(interceptPoint, camTransform);
            }
        }

        // 描画処理（事前計算済みのデータを使用）
        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        for (int i = 0; i < validCount; i++) {
            TargetCache cache = targetCaches.get(i);
            if (cache.screenPos == null) continue;

            int color = cache.isLocked ? LOCKED_COLOR : UNLOCKED_COLOR;
            drawRectFrame(guiGraphics, (int) cache.screenPos.x, (int) cache.screenPos.y, cache.size, color);

            if (cache.interceptScreenPos != null) {
                int interceptSize = cache.size - 2;
                drawRectFrameRotated(guiGraphics, poseStack,
                        (int) cache.interceptScreenPos.x, (int) cache.interceptScreenPos.y,
                        interceptSize, INTERCEPT_ROTATION, INTERCEPT_COLOR);
            }
        }

        poseStack.popPose();
        RenderSystem.disableBlend();
    }

    private static boolean isAllowedVehicle(Entity vehicle) {
        return vehicle instanceof GepardEntity
                || vehicle instanceof ZumwaltEntity;
    }

    private static boolean isValidTarget(Entity e, Player player, Entity vehicle, EntityType<?> playerType) {
        if (e == player || e == vehicle || !e.isAlive()) return false;

        EntityType<?> type = e.getType();
        if (type == playerType || excludedTypes.contains(type)) return false;

        ResourceLocation rl = ForgeRegistries.ENTITY_TYPES.getKey(type);
        return rl != null && ALLOWED_NAMESPACES.contains(rl.getNamespace());
    }

    private static void drawRectFrameRotated(GuiGraphics guiGraphics, PoseStack poseStack,
                                             int centerX, int centerY, int size,
                                             float angleDegrees, int color) {
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0);
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(angleDegrees));
        poseStack.translate(-centerX, -centerY, 0);

        int half = size >> 1;
        int left = centerX - half;
        int top = centerY - half;
        int right = centerX + half;
        int bottom = centerY + half;

        guiGraphics.fill(left, top, right, top + 1, color);
        guiGraphics.fill(left, bottom - 1, right, bottom, color);
        guiGraphics.fill(left, top, left + 1, bottom, color);
        guiGraphics.fill(right - 1, top, right, bottom, color);

        poseStack.popPose();
    }

    private static void drawRectFrame(GuiGraphics guiGraphics, int centerX, int centerY, int size, int color) {
        int half = size >> 1;
        int left = centerX - half;
        int top = centerY - half;
        int right = centerX + half;
        int bottom = centerY + half;

        guiGraphics.fill(left, top, right, top + 1, color);
        guiGraphics.fill(left, bottom - 1, right, bottom, color);
        guiGraphics.fill(left, top, left + 1, bottom, color);
        guiGraphics.fill(right - 1, top, right, bottom, color);
    }

    // カメラ変換をクラスにまとめて再利用
    private static class CameraTransform {
        final Vec3 camPos;
        final double cosYaw, sinYaw, cosPitch, sinPitch;

        CameraTransform(Camera camera) {
            this.camPos = camera.getPosition();
            double yaw = Math.toRadians(-camera.getYRot());
            double pitch = Math.toRadians(-camera.getXRot());
            this.cosYaw = Math.cos(yaw);
            this.sinYaw = Math.sin(yaw);
            this.cosPitch = Math.cos(pitch);
            this.sinPitch = Math.sin(pitch);
        }
    }

    private static Vec3 worldToScreenOptimized(Vec3 worldPos, CameraTransform cam) {
        // 相対座標計算
        double dx = worldPos.x - cam.camPos.x;
        double dy = worldPos.y - cam.camPos.y;
        double dz = worldPos.z - cam.camPos.z;

        // Yaw回転
        double x2 = cam.cosYaw * dx - cam.sinYaw * dz;
        double z2 = cam.sinYaw * dx + cam.cosYaw * dz;

        // Pitch回転
        double y2 = cam.cosPitch * dy - cam.sinPitch * z2;
        double z3 = cam.sinPitch * dy + cam.cosPitch * z2;

        // 背後判定
        if (z3 <= MIN_Z_THRESHOLD) return null;

        // スクリーン座標変換（キャッシュされた値を使用）
        double screenX = cachedScreenWidth * 0.5 - (x2 * cachedFovScale / z3);
        double screenY = cachedScreenHeight * 0.5 - (y2 * cachedFovScale / z3);

        // 画面外判定
        if (screenX < 0 || screenX >= cachedScreenWidth ||
                screenY < 0 || screenY >= cachedScreenHeight) {
            return null;
        }

        return new Vec3(screenX, screenY, 0);
    }

    public static Vec3 calculateInterceptPointOptimized(Vec3 shooterPos, Vec3 targetPos, Vec3 targetVel) {
        // 相対位置ベクトル
        double dx = targetPos.x - shooterPos.x;
        double dy = targetPos.y - shooterPos.y;
        double dz = targetPos.z - shooterPos.z;

        // 二次方程式の係数計算（最適化版）
        double vx = targetVel.x;
        double vy = targetVel.y;
        double vz = targetVel.z;

        double vDotV = vx * vx + vy * vy + vz * vz;
        double bulletSpeedSq = BULLET_SPEED * BULLET_SPEED;
        double a = vDotV - bulletSpeedSq;

        // 早期リターン
        if (Math.abs(a) < EPSILON) return targetPos;

        double dDotV = dx * vx + dy * vy + dz * vz;
        double b = 2.0 * dDotV;
        double c = dx * dx + dy * dy + dz * dz;

        double discriminant = b * b - 4.0 * a * c;
        if (discriminant < 0) return targetPos;

        double sqrtDisc = Math.sqrt(discriminant);
        double invA2 = 0.5 / a;
        double t1 = (-b - sqrtDisc) * invA2;
        double t2 = (-b + sqrtDisc) * invA2;

        double t = (t1 >= 0) ? t1 : t2;
        if (t < 0) return targetPos;

        // 迎撃点を直接計算
        return new Vec3(
                targetPos.x + vx * t,
                targetPos.y + vy * t,
                targetPos.z + vz * t
        );
    }
}