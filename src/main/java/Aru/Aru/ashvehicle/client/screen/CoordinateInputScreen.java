package Aru.Aru.ashvehicle.client.screen;

import Aru.Aru.ashvehicle.entity.projectile.BallisticMissileEntity;
import Aru.Aru.ashvehicle.init.CoordinateTargetVehicle;
import Aru.Aru.ashvehicle.init.ModNetwork;
import Aru.Aru.ashvehicle.Packet.SetMissileTargetPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collections;
import java.util.List;

public class CoordinateInputScreen extends Screen {

    private final CoordinateTargetVehicle vehicle;
    private Button fireButton;

    // レーダー
    private static final int MAP_SIZE = 360;
    private int mapX, mapY;

    private double radarRange = 500;
    private static final double MIN_RANGE = 50;
    private static final double MAX_RANGE = 12000;
    private static final double RANGE_STEP = 100;

    private double lastTerrainRange = -1;

    // 射撃地点
    private Vec3 manualTarget = null;

    // 地形
    private static final int TERRAIN_GRID = 64;
    private static final int TERRAIN_STEP = 8;
    private int[][] terrain = new int[TERRAIN_GRID][TERRAIN_GRID];
    private boolean terrainReady = false;

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
        int cx = width / 2;
        int cy = height / 2;

        mapX = cx - MAP_SIZE / 2;
        mapY = cy - MAP_SIZE / 2;

        fireButton = Button.builder(Component.literal("SHOOT"), b -> fire())
                .bounds(cx - 40, mapY + MAP_SIZE + 10, 80, 20)
                .build();
        addRenderableWidget(fireButton);

        loadTerrain();
        lastTerrainRange = radarRange;
    }

    private void loadTerrain() {
        Minecraft mc = Minecraft.getInstance();
        Vec3 center = getVehiclePos();

        int startX = (int)center.x - (TERRAIN_GRID * TERRAIN_STEP) / 2;
        int startZ = (int)center.z - (TERRAIN_GRID * TERRAIN_STEP) / 2;

        for (int x = 0; x < TERRAIN_GRID; x++) {
            for (int z = 0; z < TERRAIN_GRID; z++) {
                int wx = startX + x * TERRAIN_STEP;
                int wz = startZ + z * TERRAIN_STEP;
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
        radarRange += delta * RANGE_STEP;
        radarRange = Math.max(MIN_RANGE, Math.min(MAX_RANGE, radarRange));

        if (Math.abs(radarRange - lastTerrainRange) > TERRAIN_STEP) {
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
                center.x - radarRange, center.y - radarRange, center.z - radarRange,
                center.x + radarRange, center.y + radarRange, center.z + radarRange
        );

        return mc.level.getEntities(
                (Entity) null,
                box,
                (Entity e) -> e.isAlive() && e.distanceToSqr(center) < r2
        );
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partial) {
        renderBackground(gui);
        super.render(gui, mx, my, partial);
        drawRadar(gui);

        Vec3 v = getVehiclePos();
        gui.drawString(font, "SELF X:" + (int)v.x + " Z:" + (int)v.z, mapX + 8, mapY + MAP_SIZE - 20, 0x55FF55);

        if (manualTarget != null) {
            gui.drawString(font, "TARGET X:" + (int)manualTarget.x + " Z:" + (int)manualTarget.z, mapX + 8, mapY + 8, 0xFF5555);
        }

        gui.drawString(font, "RANGE " + (int)radarRange + "m", mapX + MAP_SIZE - 100, mapY + 8, 0xFFFFFF);
    }

    private void drawRadar(GuiGraphics gui) {
        int cx = mapX + MAP_SIZE / 2;
        int cy = mapY + MAP_SIZE / 2;

        if (terrainReady) {
            int cell = MAP_SIZE / TERRAIN_GRID;
            for (int x = 0; x < TERRAIN_GRID; x++) {
                for (int z = 0; z < TERRAIN_GRID; z++) {
                    int h = terrain[x][z];
                    int c = Math.min(255, Math.max(0, (h - 40) * 3));
                    int col = 0xFF000000 | (c << 16) | (c << 8) | c;
                    gui.fill(mapX + x * cell, mapY + z * cell,
                            mapX + x * cell + cell, mapY + z * cell + cell, col);
                }
            }
        }

        gui.fill(mapX - 2, mapY - 2, mapX + MAP_SIZE + 2, mapY, 0xFF0066FF);
        gui.fill(mapX - 2, mapY + MAP_SIZE, mapX + MAP_SIZE + 2, mapY + MAP_SIZE + 2, 0xFF0066FF);
        gui.fill(mapX - 2, mapY, mapX, mapY + MAP_SIZE, 0xFF0066FF);
        gui.fill(mapX + MAP_SIZE, mapY, mapX + MAP_SIZE + 2, mapY + MAP_SIZE, 0xFF0066FF);

        gui.fill(cx - 4, cy - 4, cx + 4, cy + 4, 0xFF00FF00);

        Vec3 center = getVehiclePos();
        double scale = MAP_SIZE / (radarRange * 2);

        for (Entity e : getRadarEntities()) {
            if (e.getId() == vehicle.getId()) continue;
            Vec3 d = e.position().subtract(center);
            int x = (int)(cx + d.x * scale);
            int y = (int)(cy + d.z * scale);

            int color = (e instanceof BallisticMissileEntity) ? 0xFF00FFFF : 0xFFFF8800;
            gui.fill(x - 2, y - 2, x + 2, y + 2, color);
        }

        if (manualTarget != null) {
            Vec3 d = manualTarget.subtract(center);
            int x = (int)(cx + d.x * scale);
            int y = (int)(cy + d.z * scale);
            gui.fill(x - 4, y - 4, x + 4, y + 4, 0xFFFF0000);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (btn == 0 &&
                mx >= mapX && mx <= mapX + MAP_SIZE &&
                my >= mapY && my <= mapY + MAP_SIZE) {

            int cx = mapX + MAP_SIZE / 2;
            int cy = mapY + MAP_SIZE / 2;
            double scale = MAP_SIZE / (radarRange * 2);

            double dx = (mx - cx) / scale;
            double dz = (my - cy) / scale;

            Vec3 v = getVehiclePos();
            manualTarget = new Vec3(v.x + dx, v.y, v.z + dz);
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    private void fire() {
        if (manualTarget == null) return;

        ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(),
                new SetMissileTargetPacket(vehicle.getId(),
                        manualTarget.x, manualTarget.y, manualTarget.z));
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
