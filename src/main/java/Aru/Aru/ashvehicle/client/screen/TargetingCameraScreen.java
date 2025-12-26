package Aru.Aru.ashvehicle.client.screen;

import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import Aru.Aru.ashvehicle.init.ModKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TargetingCameraScreen {
    private static boolean active = false;
    private static RemoteDroneEntity drone = null;
    private static float cameraYaw = 0;
    private static float cameraPitch = 45.0f;
    private static float currentZoom = 1.0f;
    private static float targetZoom = 1.0f;
    private static int zoomLevel = 0;
    private static final float[] ZOOM_LEVELS = {1.0f, 2.0f, 4.0f, 8.0f};
    private static final float MAX_PITCH = 80.0f;
    private static final float MIN_PITCH = -20.0f;
    private static boolean thermalMode = false;
    private static boolean lockHeld = false;
    private static Entity lockedTarget = null;
    private static Entity nearestTarget = null;
    private static int lockingProgress = 0;
    private static final int LOCK_TIME = 1; // Мгновенный лок - 1 тик

    public static void open(RemoteDroneEntity d) {
        active = true; drone = d; cameraYaw = d.getYRot(); cameraPitch = 45.0f;
        currentZoom = 1.0f; targetZoom = 1.0f; zoomLevel = 0; thermalMode = false;
        lockHeld = false; lockedTarget = null; nearestTarget = null; lockingProgress = 0;
    }
    public static void close() {
        active = false; drone = null; thermalMode = false;
        lockHeld = false; lockedTarget = null; nearestTarget = null; lockingProgress = 0;
    }
    public static void toggle(RemoteDroneEntity d) { if (active) close(); else open(d); }
    public static boolean isActive() { return active; }
    public static RemoteDroneEntity getDrone() { return drone; }
    public static float getCurrentZoom() { return currentZoom; }
    public static float getCameraYaw() { return cameraYaw; }
    public static float getCameraPitch() { return cameraPitch; }
    public static boolean isThermalMode() { return active && thermalMode; }
    public static boolean isLocked() { return lockHeld && lockedTarget != null && lockingProgress >= LOCK_TIME; }
    public static Entity getLockedTarget() { return lockedTarget; }
    
    /** Получить UUID залоченной цели для передачи ракетам */
    public static java.util.UUID getLockedTargetUUID() {
        if (isLocked() && lockedTarget != null) {
            return lockedTarget.getUUID();
        }
        return null;
    }

    public static void tick() {
        if (!active) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || drone == null || !drone.isAlive()) { close(); return; }
        currentZoom = currentZoom + (targetZoom - currentZoom) * 0.2f;
        findNearestTarget(); updateLock();
    }

    private static void findNearestTarget() {
        if (drone == null) return;
        Minecraft mc = Minecraft.getInstance();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        Vec3 lookVec = getActiveSeekVec();
        if (lookVec == null) return;
        List<LivingEntity> entities = drone.level().getEntitiesOfClass(LivingEntity.class,
            drone.getBoundingBox().inflate(256),
            e -> e.isAlive() && !(e instanceof Player p && p.isSpectator()) && e.getId() != drone.getId());
        Entity nearest = null; double nearestAngle = 15.0;
        for (LivingEntity e : entities) {
            Vec3 toEntity = e.position().add(0, e.getBbHeight() / 2, 0).subtract(cameraPos).normalize();
            double angle = Math.toDegrees(Math.acos(Mth.clamp(lookVec.dot(toEntity), -1, 1)));
            if (angle < nearestAngle) { nearestAngle = angle; nearest = e; }
        }
        nearestTarget = nearest;
    }
    private static void updateLock() {
        if (!lockHeld) { lockingProgress = 0; return; }
        if (nearestTarget != null && nearestTarget.isAlive()) {
            lockingProgress++;
            if (lockingProgress >= LOCK_TIME) lockedTarget = nearestTarget;
        } else {
            lockingProgress = Math.max(0, lockingProgress - 2);
            if (lockedTarget != null && !lockedTarget.isAlive()) { lockedTarget = null; lockHeld = false; }
        }
    }
    public static void toggleLock() {
        if (!active) return;
        if (lockHeld) { lockHeld = false; lockedTarget = null; lockingProgress = 0; }
        else if (nearestTarget != null) { lockHeld = true; lockingProgress = 0; }
    }
    public static void toggleThermal() { if (active) thermalMode = !thermalMode; }
    public static void handleRawMouseInput(double deltaX, double deltaY) {
        if (!active) return;
        float sensitivity = 0.05f / currentZoom;
        cameraYaw += (float) deltaX * sensitivity;
        cameraPitch += (float) deltaY * sensitivity;
        cameraPitch = Mth.clamp(cameraPitch, MIN_PITCH, MAX_PITCH);
        while (cameraYaw > 180f) cameraYaw -= 360f;
        while (cameraYaw < -180f) cameraYaw += 360f;
    }
    public static void handleScroll(double delta) {
        if (!active) return;
        if (delta > 0 && zoomLevel < ZOOM_LEVELS.length - 1) zoomLevel++;
        else if (delta < 0 && zoomLevel > 0) zoomLevel--;
        targetZoom = ZOOM_LEVELS[zoomLevel];
    }
    public static void zoomIn() { if (active && zoomLevel < ZOOM_LEVELS.length - 1) { zoomLevel++; targetZoom = ZOOM_LEVELS[zoomLevel]; } }
    public static void zoomOut() { if (active && zoomLevel > 0) { zoomLevel--; targetZoom = ZOOM_LEVELS[zoomLevel]; } }

    public static void renderHUD(GuiGraphics g, int w, int h) {
        if (!active || drone == null) return;
        Minecraft mc = Minecraft.getInstance(); var font = mc.font;
        int cx = w / 2, cy = h / 2;
        if (!thermalMode) g.fill(0, 0, w, h, 0x60000000);
        int fc = thermalMode ? 0xCCFFFFFF : 0xCC00FF00; int m = 25;
        g.fill(m, m, w - m, m + 2, fc); g.fill(m, h - m - 2, w - m, h - m, fc);
        g.fill(m, m, m + 2, h - m, fc); g.fill(w - m - 2, m, w - m, h - m, fc);
        int cs = 20, ct = 3;
        g.fill(m - 5, m - 5, m + cs, m - 5 + ct, fc); g.fill(m - 5, m - 5, m - 5 + ct, m + cs, fc);
        g.fill(w - m - cs, m - 5, w - m + 5, m - 5 + ct, fc); g.fill(w - m + 5 - ct, m - 5, w - m + 5, m + cs, fc);
        g.fill(m - 5, h - m + 5 - ct, m + cs, h - m + 5, fc); g.fill(m - 5, h - m - cs, m - 5 + ct, h - m + 5, fc);
        g.fill(w - m - cs, h - m + 5 - ct, w - m + 5, h - m + 5, fc); g.fill(w - m + 5 - ct, h - m - cs, w - m + 5, h - m + 5, fc);
        int cc = 0xFFFFFFFF, crs = 20, crg = 6, crt = 2;
        g.fill(cx - crs, cy - crt / 2, cx - crg, cy + crt / 2, cc); g.fill(cx + crg, cy - crt / 2, cx + crs, cy + crt / 2, cc);
        g.fill(cx - crt / 2, cy - crs, cx + crt / 2, cy - crg, cc); g.fill(cx - crt / 2, cy + crg, cx + crt / 2, cy + crs, cc);
        int tx = m + 10, ty = m + 10;
        g.drawString(font, "§a■ TGT CAMERA", tx, ty, 0xFFFFFF, false); ty += 12;
        g.drawString(font, String.format("ZOOM §e%.1fx", currentZoom), tx, ty, 0xCCCCCC, false); ty += 10;
        g.drawString(font, String.format("YAW §7%.0f°", cameraYaw), tx, ty, 0x888888, false); ty += 10;
        g.drawString(font, String.format("PIT §7%.0f°", cameraPitch), tx, ty, 0x888888, false); ty += 10;
        g.drawString(font, thermalMode ? "§aTHRM ON" : "§8THRM OFF", tx, ty, 0x888888, false);
        String dn = drone.getName().getString();
        g.drawString(font, "§a" + dn, w - m - font.width(dn) - 10, m + 10, 0x00FF00, false);
        renderLockStatus(g, font, cx, cy);
        String lk = ModKeyBindings.LOCK_TARGET.getKey().getDisplayName().getString();
        String tk = ModKeyBindings.THERMAL_TOGGLE.getKey().getDisplayName().getString();
        String ck = ModKeyBindings.TARGETING_CAMERA.getKey().getDisplayName().getString();
        String la = lockHeld ? "Unlock" : "Lock";
        String ctrl = "[Scroll] Zoom  [" + lk + "] " + la + "  [" + tk + "] Thermal  [" + ck + "] Close";
        g.drawString(font, ctrl, cx - font.width(ctrl) / 2, h - m - 18, 0x666666, false);
        renderCameraIndicator(g, w, h, m);
    }

    private static void renderLockStatus(GuiGraphics g, net.minecraft.client.gui.Font f, int cx, int cy) {
        if (isLocked() && lockedTarget != null) {
            String lt = "■ LOCKED ■";
            g.drawString(f, "§a" + lt, cx - f.width(lt) / 2, cy + 40, 0x00FF00, false);
            String tn = lockedTarget.getName().getString();
            g.drawString(f, "§7" + tn, cx - f.width(tn) / 2, cy + 52, 0xAAAAAA, false);
            if (drone != null) {
                String dt = String.format("DIST: %.0fm", drone.distanceTo(lockedTarget));
                g.drawString(f, "§e" + dt, cx - f.width(dt) / 2, cy + 63, 0xFFFF00, false);
            }
            drawBox(g, cx, cy, 35, 0xFF00FF00, 2);
        } else if (lockHeld && nearestTarget != null && lockingProgress > 0) {
            int pr = Math.min((lockingProgress * 100) / LOCK_TIME, 100);
            String lt = String.format("LOCKING %d%%", pr);
            g.drawString(f, "§e" + lt, cx - f.width(lt) / 2, cy + 40, 0xFFFF00, false);
            int bw = 100, bh = 6, bx = cx - bw / 2, by = cy + 54;
            g.fill(bx - 1, by - 1, bx + bw + 1, by + bh + 1, 0xFF222222);
            g.fill(bx, by, bx + (bw * pr) / 100, by + bh, pr < 50 ? 0xFFFF6600 : 0xFFFFFF00);
            drawBox(g, cx, cy, 40 - (pr / 4), 0xFFFFFF00, 2);
        } else if (nearestTarget != null) {
            g.drawString(f, "§7[TARGET DETECTED]", cx - f.width("[TARGET DETECTED]") / 2, cy + 40, 0x888888, false);
            String lk = ModKeyBindings.LOCK_TARGET.getKey().getDisplayName().getString();
            String ht = "Press [" + lk + "] to lock";
            g.drawString(f, "§8" + ht, cx - f.width(ht) / 2, cy + 52, 0x555555, false);
            drawBox(g, cx, cy, 45, 0x88888888, 1);
        } else {
            g.drawString(f, "§8NO TARGET", cx - f.width("NO TARGET") / 2, cy + 40, 0x444444, false);
        }
    }
    private static void drawBox(GuiGraphics g, int cx, int cy, int s, int c, int t) {
        g.fill(cx - s, cy - s, cx - s + t, cy + s, c); g.fill(cx + s - t, cy - s, cx + s, cy + s, c);
        g.fill(cx - s, cy - s, cx + s, cy - s + t, c); g.fill(cx - s, cy + s - t, cx + s, cy + s, c);
    }
    private static void renderCameraIndicator(GuiGraphics g, int w, int h, int m) {
        if (drone == null) return;
        int r = 28, cx = w - m - r - 15, cy = h - m - r - 35;
        int c = thermalMode ? 0xFFFFFFFF : 0xFF00FF00;
        int dc = thermalMode ? 0x66FFFFFF : 0x6600FF00;
        drawFilledCircle(g, cx, cy, r + 2, 0x40000000); drawCircle(g, cx, cy, r, dc, 1);
        var f = Minecraft.getInstance().font; float vy = drone.getYRot();
        String[] cd = {"N", "E", "S", "W"};
        for (int i = 0; i < 4; i++) {
            float a = (float) Math.toRadians(-vy + i * 90 - 90);
            int mx = cx + (int)(Math.cos(a) * (r + 10)); int my = cy + (int)(Math.sin(a) * (r + 10));
            g.drawString(f, cd[i], mx - f.width(cd[i]) / 2, my - f.lineHeight / 2, dc, false);
        }
        float yd = Mth.wrapDegrees(cameraYaw - vy); float ca = (float) Math.toRadians(yd - 90);
        int lx = cx + (int)(Math.cos(ca) * (r - 3)); int ly = cy + (int)(Math.sin(ca) * (r - 3));
        drawLine(g, cx, cy, lx, ly, c, 2); drawFilledCircle(g, lx, ly, 4, c);
        int px = cx + r + 18, ph = r * 2, py = cy - r;
        g.fill(px - 1, py, px + 5, py + ph, 0x40000000);
        g.fill(px, py, px + 4, py + 1, dc); g.fill(px, py + ph - 1, px + 4, py + ph, dc);
        float pn = (cameraPitch - MIN_PITCH) / (MAX_PITCH - MIN_PITCH);
        int piy = py + (int)(pn * ph); g.fill(px - 2, piy - 2, px + 6, piy + 2, c);
        g.drawString(f, "CAM", cx - 8, cy + r + 12, dc, false);
    }

    private static void drawFilledCircle(GuiGraphics g, int cx, int cy, int r, int c) {
        for (int y = -r; y <= r; y++) {
            int hw = (int) Math.sqrt(r * r - y * y);
            g.fill(cx - hw, cy + y, cx + hw, cy + y + 1, c);
        }
    }
    private static void drawCircle(GuiGraphics g, int cx, int cy, int r, int c, int t) {
        for (int i = 0; i < 32; i++) {
            float a1 = (float) (2 * Math.PI * i / 32); float a2 = (float) (2 * Math.PI * (i + 1) / 32);
            drawLine(g, cx + (int)(Math.cos(a1) * r), cy + (int)(Math.sin(a1) * r),
                       cx + (int)(Math.cos(a2) * r), cy + (int)(Math.sin(a2) * r), c, t);
        }
    }
    private static void drawLine(GuiGraphics g, int x1, int y1, int x2, int y2, int c, int t) {
        float dx = x2 - x1, dy = y2 - y1; float l = (float) Math.sqrt(dx * dx + dy * dy);
        if (l < 1) return;
        for (int i = 0; i <= (int) l; i++) {
            float p = (float) i / l; int x = (int) (x1 + dx * p), y = (int) (y1 + dy * p);
            g.fill(x - t / 2, y - t / 2, x + t / 2 + 1, y + t / 2 + 1, c);
        }
    }
    public static Vec3 getActiveSeekVec() {
        if (!active) return null;
        float yr = -cameraYaw * Mth.DEG_TO_RAD, pr = -cameraPitch * Mth.DEG_TO_RAD;
        float cp = Mth.cos(pr), sp = Mth.sin(pr), cy = Mth.cos(yr), sy = Mth.sin(yr);
        return new Vec3(sy * cp, sp, cy * cp);
    }
    public static CameraData getCameraData() {
        if (!active) return null;
        return new CameraData(cameraYaw, cameraPitch, currentZoom);
    }
    public record CameraData(float yaw, float pitch, float zoom) {}
}
