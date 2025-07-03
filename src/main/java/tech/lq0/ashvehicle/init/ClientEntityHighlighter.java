package tech.lq0.ashvehicle.init;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import tech.lq0.ashvehicle.ExtensionTest;
import tech.lq0.ashvehicle.entity.Class.BaseAircraftEntity;
import tech.lq0.ashvehicle.entity.GepardEntity;
import com.atsuishio.superbwarfare.init.ModEntities;

import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = ExtensionTest.MODID, value = Dist.CLIENT)
public class ClientEntityHighlighter {

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Entity vehicle = player.getVehicle();
        if (player == null || vehicle == null) return;

        boolean isAllowedVehicle = vehicle instanceof GepardEntity
                || vehicle instanceof BaseAircraftEntity;
        if (!isAllowedVehicle) return;

        float bulletSpeed = 35f;
        Vec3 shooterPos = new Vec3(player.getX(), player.getEyeY(), player.getZ());

        Set<EntityType<?>> excludedTypes = Set.of(
                ModEntities.SMALL_CANNON_SHELL.get(),
                ModEntities.HELI_ROCKET.get(),
                ModEntities.CANNON_SHELL.get(),
                ModEntities.GUN_GRENADE.get(),
                ModEntities.PROJECTILE.get(),
                ModEntities.AGM_65.get(),
                ModEntities.RPG_ROCKET.get(),
                ModEntities.WG_MISSILE.get(),
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
                ModEntities.C_4.get(),
                ModEntities.WATER_MASK.get(),
                ModEntities.TASER_BULLET.get(),
                ModEntities.MK_82.get(),
                ModEntities.MK_42.get(),
                tech.lq0.ashvehicle.init.ModEntities.AAM_4.get(),
                tech.lq0.ashvehicle.init.ModEntities.GBU_57.get(),
                player.getType()
        );

        Set<EntityType<?>> stealthTypes = Set.of(
                tech.lq0.ashvehicle.init.ModEntities.F_35.get(),
                tech.lq0.ashvehicle.init.ModEntities.B_2.get(),
                tech.lq0.ashvehicle.init.ModEntities.F_22.get(),
                tech.lq0.ashvehicle.init.ModEntities.F_117.get(),
                player.getType()
        );

        // 兵器タイプだけを許可するホワイトリスト
        Set<String> allowedNamespaces = Set.of("superbwarfare", "ashvehicle", "vvp");

        List<Entity> targets = vehicle.level().getEntities(player, player.getBoundingBox().inflate(1028), e -> {
            if (e == player || e == vehicle || !e.isAlive()) return false;

            // EntityType 取得
            ResourceLocation rl = ForgeRegistries.ENTITY_TYPES.getKey(e.getType());
            if (rl == null) return false;

            // 指定した MOD ID 以外は除外
            if (!allowedNamespaces.contains(rl.getNamespace())) return false;

            // 弾などの除外
            if (excludedTypes.contains(e.getType())) return false;

            // 必要に応じてさらに制限（例：BaseVehicleEntityのみに絞る）
            // return e instanceof BaseVehicleEntity;

            return true;
        });
        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        for (Entity target : targets) {
            float fov = mc.options.fov().get();
            Vec3 targetPos = target.getBoundingBox().getCenter();
            Vec3 screenPos = worldToScreen(targetPos, mc, event.getPartialTick(), fov);

            if (screenPos == null) continue;

            // 緑色の枠線（現在位置）
            int size = stealthTypes.contains(target.getType()) ? 6 : 10;

            if (target == ClientTargetingData.getLockedTarget()) {
                drawRectFrame(guiGraphics, (int)screenPos.x, (int)screenPos.y, size, 0xFFFFFF00); // 黄色
            } else {
                drawRectFrame(guiGraphics, (int)screenPos.x, (int)screenPos.y, size, 0xFF00FF00); // 緑
            }

            Vec3 targetVelocity = target.getDeltaMovement();
            Vec3 interceptPoint = calculateInterceptPoint(shooterPos, targetPos, targetVelocity, bulletSpeed);
            Vec3 interceptScreenPos = worldToScreen(interceptPoint, mc, event.getPartialTick(), fov);
            if (interceptScreenPos == null) continue;

            // 予測枠のサイズも同様に調整
            int interceptSize = size - 2;
            drawRectFrameRotated(guiGraphics, poseStack, (int)interceptScreenPos.x, (int)interceptScreenPos.y, interceptSize, 35f, 0xFFFF0000);
        }

        poseStack.popPose();
        RenderSystem.disableBlend();
    }

    private static void drawRectFrameRotated(GuiGraphics guiGraphics, PoseStack poseStack, int centerX, int centerY, int size, float angleDegrees, int color) {
        poseStack.pushPose();

        // 中心に移動 → 回転 → 描画位置に戻す
        poseStack.translate(centerX, centerY, 0);
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(angleDegrees));
        poseStack.translate(-centerX, -centerY, 0);

        // draw using current transformed pose
        int half = size / 2;
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
        int half = size / 2;
        int left = centerX - half;
        int top = centerY - half;
        int right = centerX + half;
        int bottom = centerY + half;

        guiGraphics.fill(left, top, right, top + 1, color);
        guiGraphics.fill(left, bottom - 1, right, bottom, color);
        guiGraphics.fill(left, top, left + 1, bottom, color);
        guiGraphics.fill(right - 1, top, right, bottom, color);
    }

    private static Vec3 worldToScreen(Vec3 worldPos, Minecraft mc, float partialTicks, float fov) {
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();
        Vec3 rel = worldPos.subtract(camPos);

        // カメラの回転角（ラジアン）
        float yaw = (float)Math.toRadians(-camera.getYRot());   // 🔁 Yaw の符号を反転（ここが重要！）
        float pitch = (float)Math.toRadians(-camera.getXRot()); // 🔁 Pitch もマイナスで後ろ向き補正

        // 回転（Yaw → Pitch の順）
        double x1 = rel.x;
        double y1 = rel.y;
        double z1 = rel.z;

        // Yaw回転（水平）
        double x2 = Math.cos(yaw) * x1 - Math.sin(yaw) * z1;
        double z2 = Math.sin(yaw) * x1 + Math.cos(yaw) * z1;

        // Pitch回転（垂直）
        double y2 = Math.cos(pitch) * y1 - Math.sin(pitch) * z2;
        double z3 = Math.sin(pitch) * y1 + Math.cos(pitch) * z2;

        // 画面変換
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        double fovRad = Math.toRadians(fov);
        double scale = screenHeight / (2.0 * Math.tan(fovRad / 2.0));

        if (z3 <= 0.01) return null;

        double screenX = screenWidth / 2.0 - (x2 * scale / z3);
        double screenY = screenHeight / 2.0 - (y2 * scale / z3);

        if (screenX < 0 || screenX >= screenWidth || screenY < 0 || screenY >= screenHeight)
            return null;

        return new Vec3(screenX, screenY, 0);
    }

    public static Vec3 calculateInterceptPoint(Vec3 shooterPos, Vec3 targetPos, Vec3 targetVel, float bulletSpeed) {
        Vec3 displacement = targetPos.subtract(shooterPos);
        double a = targetVel.dot(targetVel) - bulletSpeed * bulletSpeed;
        double b = 2 * displacement.dot(targetVel);
        double c = displacement.dot(displacement);

        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0 || Math.abs(a) < 1e-6) {
            return targetPos;
        }

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = (-b - sqrtDisc) / (2 * a);
        double t2 = (-b + sqrtDisc) / (2 * a);

        double t = Math.min(t1, t2);
        if (t < 0) t = Math.max(t1, t2);
        if (t < 0) {
            return targetPos;
        }

        return targetPos.add(targetVel.scale(t));
    }
}
