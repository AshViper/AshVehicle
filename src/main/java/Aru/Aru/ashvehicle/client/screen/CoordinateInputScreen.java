package Aru.Aru.ashvehicle.client.screen;

import Aru.Aru.ashvehicle.entity.projectile.BallisticMissileEntity;
import Aru.Aru.ashvehicle.init.CoordinateTargetVehicle;
import Aru.Aru.ashvehicle.init.ModNetwork;
import Aru.Aru.ashvehicle.Packet.SetMissileTargetPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collections;
import java.util.List;

public class CoordinateInputScreen extends Screen {

    private final CoordinateTargetVehicle vehicle;

    // Fixed sizes
    private static final int MAP_SIZE = 280;
    private static final int PANEL_WIDTH = 150;
    private int mapX, mapY;

    private double radarRange = 500;
    private static final double MIN_RANGE = 50;
    private static final double MAX_RANGE = 12000;
    private static final double RANGE_STEP = 100;

    private double lastTerrainRange = -1;

    // Target
    private Vec3 manualTarget = null;
    private Entity targetEntity = null;

    // Terrain
    private static final int TERRAIN_GRID = 56;
    private int[][] terrain = new int[TERRAIN_GRID][TERRAIN_GRID];
    private boolean terrainReady = false;

    // Animation
    private float scanAngle = 0;
    private long lastTime = System.currentTimeMillis();

    // Fire button bounds
    private int fireBtnX, fireBtnY, fireBtnW, fireBtnH;

    // Colors
    private static final int COL_BG = 0xF0080C10;
    private static final int COL_PANEL = 0xD0101418;
    private static final int COL_ACCENT = 0xFF00CCAA;
    private static final int COL_ACCENT_DIM = 0xFF006655;
    private static final int COL_ACCENT_GLOW = 0x5000FFCC;
    private static final int COL_GRID = 0x30008866;
    private static final int COL_TEXT = 0xFF00FFCC;
    private static final int COL_TEXT_WARN = 0xFFFFAA00;
    private static final int COL_TARGET = 0xFFFF2222;
    private static final int COL_TARGET_ENTITY = 0xFFFF00FF;
    private static final int COL_ENEMY = 0xFFFF6600;
    private static final int COL_MISSILE = 0xFF00DDFF;
    private static final int COL_SELF = 0xFF00FF44;

    public CoordinateInputScreen(CoordinateTargetVehicle vehicle) {
        super(Component.literal("Missile Control"));
        this.vehicle = vehicle;
    }

    private Vec3 getVehiclePos() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return Vec3.ZERO;
        Entity e = mc.level.getEntity(vehicle.getId());
        return e != null ? e.position() : Vec3.ZERO;
    }

    @Override
    protected void init() {
        // Center the whole UI
        int totalWidth = MAP_SIZE + 15 + PANEL_WIDTH;
        mapX = (width - totalWidth) / 2;
        mapY = (height - MAP_SIZE) / 2;

        loadTerrain();
        lastTerrainRange = radarRange;
    }

    private void loadTerrain() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Vec3 center = getVehiclePos();
        double cellSize = (radarRange * 2) / TERRAIN_GRID;

        for (int x = 0; x < TERRAIN_GRID; x++) {
            for (int z = 0; z < TERRAIN_GRID; z++) {
                int wx = (int) (center.x - radarRange + x * cellSize);
                int wz = (int) (center.z - radarRange + z * cellSize);
                terrain[x][z] = mc.level.getHeightmapPos(
                        Heightmap.Types.WORLD_SURFACE,
                        new BlockPos(wx, 0, wz)
                ).getY();
            }
        }
        terrainReady = true;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        radarRange -= delta * RANGE_STEP;
        radarRange = Math.max(MIN_RANGE, Math.min(MAX_RANGE, radarRange));

        if (Math.abs(radarRange - lastTerrainRange) > 50) {
            loadTerrain();
            lastTerrainRange = radarRange;
        }
        return true;
    }

    private List<Entity> getRadarEntities() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return Collections.emptyList();

        Vec3 center = getVehiclePos();
        double r2 = radarRange * radarRange;

        AABB box = new AABB(
                center.x - radarRange, center.y - 500, center.z - radarRange,
                center.x + radarRange, center.y + 500, center.z + radarRange
        );

        return mc.level.getEntities((Entity) null, box, e -> e.isAlive() && e.distanceToSqr(center) < r2);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partial) {
        // Update tracked entity
        if (targetEntity != null) {
            if (targetEntity.isAlive()) {
                manualTarget = targetEntity.position();
            } else {
                targetEntity = null;
            }
        }

        // Animation
        long now = System.currentTimeMillis();
        float dt = (now - lastTime) / 1000f;
        lastTime = now;
        scanAngle += dt * 45;
        if (scanAngle >= 360) scanAngle -= 360;

        // Background
        renderBackground(gui);
        gui.fill(0, 0, width, height, COL_BG);

        drawTitle(gui);
        drawRadarPanel(gui);
        drawInfoPanel(gui);

        super.render(gui, mx, my, partial);
    }

    private void drawTitle(GuiGraphics gui) {
        String title = "◆ TACTICAL MISSILE CONTROL ◆";
        int tw = font.width(title);
        int tx = width / 2 - tw / 2;
        int ty = mapY - 25;

        gui.fill(tx - 12, ty - 3, tx + tw + 12, ty + 12, COL_PANEL);
        drawBorder(gui, tx - 12, ty - 3, tx + tw + 12, ty + 12, COL_ACCENT);
        gui.drawString(font, title, tx, ty, COL_TEXT, false);
    }

    private void drawRadarPanel(GuiGraphics gui) {
        int cx = mapX + MAP_SIZE / 2;
        int cy = mapY + MAP_SIZE / 2;

        // Background
        gui.fill(mapX - 6, mapY - 6, mapX + MAP_SIZE + 6, mapY + MAP_SIZE + 6, COL_PANEL);

        // Terrain
        if (terrainReady) {
            int cell = MAP_SIZE / TERRAIN_GRID;
            for (int x = 0; x < TERRAIN_GRID; x++) {
                for (int z = 0; z < TERRAIN_GRID; z++) {
                    int h = terrain[x][z];
                    int b = Math.min(50, Math.max(8, (h - 50)));
                    int col = 0xFF000000 | (b / 4) | ((b / 2 + 10) << 8) | (b / 3 << 16);
                    gui.fill(mapX + x * cell, mapY + z * cell,
                            mapX + x * cell + cell, mapY + z * cell + cell, col);
                }
            }
        }

        drawGrid(gui, mapX, mapY, MAP_SIZE, 6);
        drawScanSweep(gui, cx, cy, MAP_SIZE / 2);
        drawRangeCircles(gui, cx, cy);
        drawCompass(gui, cx, cy);

        // Entities
        Vec3 center = getVehiclePos();
        double scale = MAP_SIZE / (radarRange * 2);

        for (Entity e : getRadarEntities()) {
            if (e.getId() == vehicle.getId()) continue;
            Vec3 d = e.position().subtract(center);
            int ex = (int) (cx + d.x * scale);
            int ey = (int) (cy + d.z * scale);

            if (ex < mapX || ex > mapX + MAP_SIZE || ey < mapY || ey > mapY + MAP_SIZE) continue;

            boolean isTracked = (targetEntity != null && e.getId() == targetEntity.getId());

            if (e instanceof BallisticMissileEntity) {
                drawMissileBlip(gui, ex, ey);
            } else if (e instanceof LivingEntity) {
                drawEnemyBlip(gui, ex, ey, isTracked);
            } else {
                gui.fill(ex - 2, ey - 2, ex + 2, ey + 2, isTracked ? COL_TARGET_ENTITY : 0xFF888888);
            }
        }

        drawSelfMarker(gui, cx, cy);

        // Target marker
        if (manualTarget != null) {
            Vec3 d = manualTarget.subtract(center);
            int tx = (int) (cx + d.x * scale);
            int ty = (int) (cy + d.z * scale);
            drawTargetMarker(gui, tx, ty, targetEntity != null);
        }

        drawGlowBorder(gui, mapX - 6, mapY - 6, mapX + MAP_SIZE + 6, mapY + MAP_SIZE + 6);
        drawCorners(gui, mapX - 6, mapY - 6, mapX + MAP_SIZE + 6, mapY + MAP_SIZE + 6);
    }

    private void drawGrid(GuiGraphics gui, int x, int y, int size, int divisions) {
        int step = size / divisions;
        for (int i = 1; i < divisions; i++) {
            gui.fill(x + i * step, y, x + i * step + 1, y + size, COL_GRID);
            gui.fill(x, y + i * step, x + size, y + i * step + 1, COL_GRID);
        }
    }

    private void drawScanSweep(GuiGraphics gui, int cx, int cy, int radius) {
        int segments = 30;
        float sweepAngle = 45;

        for (int i = 0; i < segments; i++) {
            float a = scanAngle - i * (sweepAngle / segments);
            double r = Math.toRadians(a);
            int alpha = (int) (80 * (1 - i / (float) segments));
            int col = (alpha << 24) | 0x00FFAA;

            int x1 = cx + (int) (Math.cos(r) * radius * 0.1);
            int y1 = cy + (int) (Math.sin(r) * radius * 0.1);
            int x2 = cx + (int) (Math.cos(r) * radius);
            int y2 = cy + (int) (Math.sin(r) * radius);

            drawLine(gui, x1, y1, x2, y2, col);
        }
    }

    private void drawLine(GuiGraphics gui, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int steps = Math.max(dx, dy);
        if (steps == 0) return;

        for (int i = 0; i <= steps; i++) {
            int x = x1 + (x2 - x1) * i / steps;
            int y = y1 + (y2 - y1) * i / steps;
            gui.fill(x, y, x + 1, y + 1, color);
        }
    }

    private void drawRangeCircles(GuiGraphics gui, int cx, int cy) {
        int[] radii = {MAP_SIZE / 6, MAP_SIZE / 3, MAP_SIZE / 2};
        for (int r : radii) {
            drawCircle(gui, cx, cy, r, COL_GRID);
        }
    }

    private void drawCircle(GuiGraphics gui, int cx, int cy, int radius, int color) {
        int segments = 64;
        for (int i = 0; i < segments; i++) {
            double a1 = Math.PI * 2 * i / segments;
            double a2 = Math.PI * 2 * (i + 1) / segments;
            int x1 = cx + (int) (Math.cos(a1) * radius);
            int y1 = cy + (int) (Math.sin(a1) * radius);
            int x2 = cx + (int) (Math.cos(a2) * radius);
            int y2 = cy + (int) (Math.sin(a2) * radius);
            drawLine(gui, x1, y1, x2, y2, color);
        }
    }

    private void drawCompass(GuiGraphics gui, int cx, int cy) {
        gui.drawString(font, "N", cx - 3, mapY + 4, COL_ACCENT, false);
        gui.drawString(font, "S", cx - 3, mapY + MAP_SIZE - 12, COL_ACCENT_DIM, false);
        gui.drawString(font, "E", mapX + MAP_SIZE - 10, cy - 4, COL_ACCENT_DIM, false);
        gui.drawString(font, "W", mapX + 4, cy - 4, COL_ACCENT_DIM, false);
    }

    private void drawSelfMarker(GuiGraphics gui, int cx, int cy) {
        gui.fill(cx - 1, cy - 6, cx + 2, cy + 7, COL_SELF);
        gui.fill(cx - 6, cy - 1, cx + 7, cy + 2, COL_SELF);
        gui.fill(cx - 3, cy - 3, cx + 4, cy + 4, 0xFF003311);
        gui.fill(cx - 2, cy - 2, cx + 3, cy + 3, COL_SELF);
    }

    private void drawTargetMarker(GuiGraphics gui, int x, int y, boolean isEntity) {
        long pulse = System.currentTimeMillis() % 1000;
        int size = 6 + (int) (pulse / 200);
        int alpha = (int) (255 * (1 - pulse / 1000.0));
        int baseCol = isEntity ? 0xFF00FF : 0xFF2222;
        int pulseCol = (alpha << 24) | baseCol;
        int markerCol = isEntity ? COL_TARGET_ENTITY : COL_TARGET;

        drawCircle(gui, x, y, size, pulseCol);

        gui.fill(x - 8, y - 1, x - 3, y + 2, markerCol);
        gui.fill(x + 4, y - 1, x + 9, y + 2, markerCol);
        gui.fill(x - 1, y - 8, x + 2, y - 3, markerCol);
        gui.fill(x - 1, y + 4, x + 2, y + 9, markerCol);
        gui.fill(x - 2, y - 2, x + 3, y + 3, markerCol);
    }

    private void drawEnemyBlip(GuiGraphics gui, int x, int y, boolean tracked) {
        int col = tracked ? COL_TARGET_ENTITY : COL_ENEMY;
        int bg = tracked ? 0xFF330033 : 0xFF331100;
        gui.fill(x - 3, y - 3, x + 4, y + 4, bg);
        gui.fill(x - 2, y - 2, x + 3, y + 3, col);
    }

    private void drawMissileBlip(GuiGraphics gui, int x, int y) {
        gui.fill(x - 1, y - 4, x + 2, y + 3, COL_MISSILE);
        gui.fill(x - 3, y + 1, x + 4, y + 3, COL_MISSILE);
    }

    private void drawGlowBorder(GuiGraphics gui, int x1, int y1, int x2, int y2) {
        gui.fill(x1 - 2, y1 - 2, x2 + 2, y1, COL_ACCENT_GLOW);
        gui.fill(x1 - 2, y2, x2 + 2, y2 + 2, COL_ACCENT_GLOW);
        gui.fill(x1 - 2, y1, x1, y2, COL_ACCENT_GLOW);
        gui.fill(x2, y1, x2 + 2, y2, COL_ACCENT_GLOW);
        drawBorder(gui, x1, y1, x2, y2, COL_ACCENT);
    }

    private void drawBorder(GuiGraphics gui, int x1, int y1, int x2, int y2, int color) {
        gui.fill(x1, y1, x2, y1 + 2, color);
        gui.fill(x1, y2 - 2, x2, y2, color);
        gui.fill(x1, y1, x1 + 2, y2, color);
        gui.fill(x2 - 2, y1, x2, y2, color);
    }

    private void drawCorners(GuiGraphics gui, int x1, int y1, int x2, int y2) {
        int len = 12;
        int t = 3;
        gui.fill(x1, y1, x1 + len, y1 + t, COL_ACCENT);
        gui.fill(x1, y1, x1 + t, y1 + len, COL_ACCENT);
        gui.fill(x2 - len, y1, x2, y1 + t, COL_ACCENT);
        gui.fill(x2 - t, y1, x2, y1 + len, COL_ACCENT);
        gui.fill(x1, y2 - t, x1 + len, y2, COL_ACCENT);
        gui.fill(x1, y2 - len, x1 + t, y2, COL_ACCENT);
        gui.fill(x2 - len, y2 - t, x2, y2, COL_ACCENT);
        gui.fill(x2 - t, y2 - len, x2, y2, COL_ACCENT);
    }

    private void drawInfoPanel(GuiGraphics gui) {
        int px = mapX + MAP_SIZE + 15;
        int py = mapY;
        int pw = PANEL_WIDTH;
        int ph = MAP_SIZE;

        gui.fill(px, py, px + pw, py + ph, COL_PANEL);
        drawGlowBorder(gui, px, py, px + pw, py + ph);
        drawCorners(gui, px, py, px + pw, py + ph);

        int textY = py + 12;
        int lineH = 12;

        // STATUS section
        gui.drawString(font, "▸ STATUS", px + 8, textY, COL_ACCENT, false);
        textY += lineH + 4;
        gui.fill(px + 6, textY, px + pw - 6, textY + 1, COL_ACCENT_DIM);
        textY += 8;

        Vec3 v = getVehiclePos();
        gui.drawString(font, "POSITION", px + 8, textY, COL_ACCENT_DIM, false);
        textY += lineH;
        gui.drawString(font, "X: " + (int) v.x, px + 12, textY, COL_TEXT, false);
        textY += lineH;
        gui.drawString(font, "Z: " + (int) v.z, px + 12, textY, COL_TEXT, false);
        textY += lineH + 4;

        gui.drawString(font, "RANGE: " + (int) radarRange + "m", px + 8, textY, COL_TEXT, false);
        textY += lineH + 6;

        // TARGET section
        gui.fill(px + 6, textY, px + pw - 6, textY + 1, COL_ACCENT_DIM);
        textY += 8;
        gui.drawString(font, "▸ TARGET", px + 8, textY, COL_ACCENT, false);
        textY += lineH + 4;

        if (manualTarget != null) {
            int targetCol = targetEntity != null ? COL_TARGET_ENTITY : COL_TARGET;

            if (targetEntity != null) {
                String name = targetEntity.getName().getString();
                if (name.length() > 12) name = name.substring(0, 10) + "..";
                gui.drawString(font, "◎ " + name, px + 8, textY, COL_TARGET_ENTITY, false);
                textY += lineH;
            }

            gui.drawString(font, "X: " + (int) manualTarget.x, px + 12, textY, targetCol, false);
            textY += lineH;
            gui.drawString(font, "Z: " + (int) manualTarget.z, px + 12, textY, targetCol, false);
            textY += lineH;

            double dist = getVehiclePos().distanceTo(manualTarget);
            gui.drawString(font, "DIST: " + (int) dist + "m", px + 12, textY, COL_TEXT_WARN, false);
            textY += lineH + 8;

            // FIRE BUTTON
            fireBtnX = px + 8;
            fireBtnY = textY;
            fireBtnW = pw - 16;
            fireBtnH = 24;
            drawFireButton(gui, fireBtnX, fireBtnY, fireBtnW, fireBtnH);
        } else {
            gui.drawString(font, "NO TARGET", px + 12, textY, COL_ACCENT_DIM, false);
            textY += lineH + 2;
            gui.drawString(font, "Click radar", px + 12, textY, 0xFF555555, false);
            textY += lineH;
            gui.drawString(font, "to set target", px + 12, textY, 0xFF555555, false);
            fireBtnX = fireBtnY = fireBtnW = fireBtnH = 0;
        }

        // Hints at bottom
        int hintY = py + ph - 28;
        gui.fill(px + 6, hintY - 4, px + pw - 6, hintY - 3, COL_ACCENT_DIM);
        gui.drawString(font, "SCROLL: Zoom", px + 8, hintY, 0xFF444444, false);
        gui.drawString(font, "CLICK: Target", px + 8, hintY + 10, 0xFF444444, false);
    }

    private void drawFireButton(GuiGraphics gui, int x, int y, int w, int h) {
        long pulse = System.currentTimeMillis() % 1200;
        float glow = (float) (Math.sin(pulse / 1200.0 * Math.PI * 2) * 0.5 + 0.5);

        // Glow
        int glowAlpha = (int) (60 * glow);
        gui.fill(x - 3, y - 3, x + w + 3, y + h + 3, (glowAlpha << 24) | 0xFF0000);

        // Button bg
        gui.fill(x, y, x + w, y + h, 0xFF441111);

        // Border
        gui.fill(x, y, x + w, y + 2, COL_TARGET);
        gui.fill(x, y + h - 2, x + w, y + h, COL_TARGET);
        gui.fill(x, y, x + 2, y + h, COL_TARGET);
        gui.fill(x + w - 2, y, x + w, y + h, COL_TARGET);

        // Text
        String text = "★ FIRE ★";
        int tw = font.width(text);
        gui.drawString(font, text, x + w / 2 - tw / 2, y + h / 2 - 4, COL_TARGET, false);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        // Fire button
        if (manualTarget != null && btn == 0 && fireBtnW > 0) {
            if (mx >= fireBtnX && mx <= fireBtnX + fireBtnW && my >= fireBtnY && my <= fireBtnY + fireBtnH) {
                fire();
                return true;
            }
        }

        // Radar click
        if (btn == 0 && mx >= mapX && mx <= mapX + MAP_SIZE && my >= mapY && my <= mapY + MAP_SIZE) {
            int cx = mapX + MAP_SIZE / 2;
            int cy = mapY + MAP_SIZE / 2;
            double scale = MAP_SIZE / (radarRange * 2);

            Vec3 center = getVehiclePos();
            Entity clickedEntity = null;
            double closestDist = 10;

            for (Entity e : getRadarEntities()) {
                if (e.getId() == vehicle.getId()) continue;
                Vec3 d = e.position().subtract(center);
                int ex = (int) (cx + d.x * scale);
                int ey = (int) (cy + d.z * scale);

                double dist = Math.sqrt((mx - ex) * (mx - ex) + (my - ey) * (my - ey));
                if (dist < closestDist) {
                    closestDist = dist;
                    clickedEntity = e;
                }
            }

            if (clickedEntity != null) {
                targetEntity = clickedEntity;
                manualTarget = clickedEntity.position();
            } else {
                targetEntity = null;
                double dx = (mx - cx) / scale;
                double dz = (my - cy) / scale;
                manualTarget = new Vec3(center.x + dx, center.y, center.z + dz);
            }
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    private void fire() {
        if (manualTarget == null) return;

        ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(),
                new SetMissileTargetPacket(vehicle.getId(),
                        manualTarget.x, manualTarget.y, manualTarget.z));
        manualTarget = null;
        targetEntity = null;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
