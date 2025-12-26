package Aru.Aru.ashvehicle.client.overlay;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.client.screen.TargetingCameraScreen;
import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import Aru.Aru.ashvehicle.init.ModKeyBindings;
import Aru.Aru.ashvehicle.tools.DroneFindUtil;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.Monitor;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.joml.Vector3f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class UCAVHudOverlay implements IGuiOverlay {

    public static final String ID = AshVehicle.MODID + "_ucav_hud";

    private static final int GREEN = 0xFF00FF00;
    private static final int RED = 0xFFFF0000;
    private static final int YELLOW = 0xFFFFFF00;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int CYAN = 0xFF00FFFF;
    private static final int DARK = 0xFF333333;

    @Override
    public void render(ForgeGui gui, GuiGraphics g, float pt, int w, int h) {
        Minecraft mc = gui.getMinecraft();
        Player player = mc.player;
        if (player == null) return;
        if (TargetingCameraScreen.isActive()) return;

        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ModItems.MONITOR.get())) return;
        if (!stack.getOrCreateTag().getBoolean("Using")) return;
        if (!stack.getOrCreateTag().getBoolean("Linked")) return;

        String droneUUID = stack.getOrCreateTag().getString(Monitor.LINKED_DRONE);
        RemoteDroneEntity drone = DroneFindUtil.findRemoteDrone(player.level(), droneUUID);
        if (drone == null) return;

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        PoseStack ps = g.pose();
        ps.pushPose();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, 
                                       GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        renderHUD(g, mc, player, drone, cameraPos, pt, w, h);

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        ps.popPose();
    }

    private void renderHUD(GuiGraphics g, Minecraft mc, Player player, RemoteDroneEntity drone, 
                           Vec3 cameraPos, float pt, int w, int h) {
        int cx = w / 2, cy = h / 2;
        var f = mc.font;

        // Dark overlay edges
        g.fill(0, 0, w, 20, 0xA0000000);
        g.fill(0, h - 25, w, h, 0xA0000000);

        // Top bar - drone name and status
        String name = drone.getName().getString();
        g.drawString(f, "§a" + name, 10, 6, GREEN, false);
        
        String status = drone.isLinked() ? "§aLINKED" : "§cNO LINK";
        g.drawString(f, status, w - f.width(status.substring(2)) - 10, 6, WHITE, false);

        // Compass at top center
        renderCompass(g, mc, drone, cx, 6);

        // Crosshair - military style
        renderCrosshair(g, cx, cy);

        // Left panel - flight data
        renderFlightData(g, mc, drone, player, 10, 35);

        // Right panel - target info
        renderTargetInfo(g, mc, drone, cameraPos, pt, w - 110, 35);

        // Bottom bar - coordinates and controls
        renderBottomBar(g, mc, drone, cx, h);

        // Pitch ladder
        renderPitchLadder(g, mc, drone, cx, cy);

        // Entity markers
        renderEntityMarkers(g, mc, drone, pt, w, h);

        // Artificial horizon indicator
        renderHorizon(g, drone, cx, cy);
    }

    private void renderCrosshair(GuiGraphics g, int cx, int cy) {
        int s = 25, gap = 8, t = 2;
        // Horizontal
        g.fill(cx - s, cy - t/2, cx - gap, cy + t/2 + 1, GREEN);
        g.fill(cx + gap, cy - t/2, cx + s, cy + t/2 + 1, GREEN);
        // Vertical
        g.fill(cx - t/2, cy - s, cx + t/2 + 1, cy - gap, GREEN);
        g.fill(cx - t/2, cy + gap, cx + t/2 + 1, cy + s, GREEN);
        // Center dot
        g.fill(cx - 2, cy - 2, cx + 3, cy + 3, GREEN);
        // Outer corners
        g.fill(cx - s - 5, cy - t/2, cx - s, cy + t/2 + 1, GREEN);
        g.fill(cx + s, cy - t/2, cx + s + 5, cy + t/2 + 1, GREEN);
    }

    private void renderFlightData(GuiGraphics g, Minecraft mc, RemoteDroneEntity drone, Player player, int x, int y) {
        var f = mc.font;
        int lh = 11;

        // Background panel
        g.fill(x - 5, y - 5, x + 95, y + 95, 0x80000000);

        // Throttle
        float power = drone.getEntityData().get(com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity.POWER);
        int pct = (int)(power * 100);
        int pc = power > 0.7f ? GREEN : (power > 0.3f ? YELLOW : RED);
        g.drawString(f, "THR", x, y, 0xAAAAAA, false);
        g.drawString(f, pct + "%", x + 30, y, pc, false);
        // Throttle bar
        g.fill(x + 60, y, x + 90, y + 8, DARK);
        g.fill(x + 60, y, x + 60 + (int)(30 * power), y + 8, pc);
        y += lh + 4;

        // Speed
        double speed = drone.getDeltaMovement().length() * 72; // km/h approx
        g.drawString(f, "SPD", x, y, 0xAAAAAA, false);
        g.drawString(f, String.format("%.0f", speed), x + 30, y, WHITE, false);
        y += lh;

        // Altitude
        g.drawString(f, "ALT", x, y, 0xAAAAAA, false);
        g.drawString(f, String.format("%.0f", drone.getY()), x + 30, y, WHITE, false);
        y += lh;

        // Distance to operator
        double dist = player.position().distanceTo(drone.position());
        int dc = dist < 200 ? GREEN : (dist < 400 ? YELLOW : RED);
        g.drawString(f, "RNG", x, y, 0xAAAAAA, false);
        g.drawString(f, String.format("%.0fm", dist), x + 30, y, dc, false);
        y += lh + 4;

        // Health bar
        float hp = drone.getHealth() / drone.getMaxHealth();
        int hc = hp > 0.5f ? GREEN : (hp > 0.25f ? YELLOW : RED);
        g.drawString(f, "HP", x, y, 0xAAAAAA, false);
        g.fill(x + 20, y, x + 90, y + 8, DARK);
        g.fill(x + 20, y, x + 20 + (int)(70 * hp), y + 8, hc);
        y += lh + 4;

        // Gear status
        boolean gear = drone.isGearDown();
        String gearStr = gear ? "§aGEAR DN" : "§eGEAR UP";
        g.drawString(f, gearStr, x, y, WHITE, false);
    }

    private void renderTargetInfo(GuiGraphics g, Minecraft mc, RemoteDroneEntity drone, Vec3 cameraPos, float pt, int x, int y) {
        var f = mc.font;

        g.fill(x - 5, y - 5, x + 105, y + 55, 0x80000000);

        g.drawString(f, "§eTARGET", x, y, YELLOW, false);
        y += 12;

        BlockHitResult result = drone.level().clip(new ClipContext(
            cameraPos, cameraPos.add(drone.getViewVector(1).scale(512)),
            ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, drone));
        
        double blockRange = cameraPos.distanceTo(result.getLocation());
        Entity target = findTargetEntity(drone, cameraPos, 512, pt);
        
        if (target != null) {
            g.drawString(f, target.getName().getString(), x, y, RED, false);
            y += 10;
            g.drawString(f, String.format("%.0fm", drone.distanceTo(target)), x, y, RED, false);
            if (target instanceof LivingEntity le) {
                y += 10;
                g.drawString(f, "HP: " + (int)le.getHealth(), x, y, RED, false);
            }
        } else {
            g.drawString(f, blockRange < 500 ? String.format("%.0fm", blockRange) : "---", x, y, GREEN, false);
        }
    }

    private void renderBottomBar(GuiGraphics g, Minecraft mc, RemoteDroneEntity drone, int cx, int h) {
        var f = mc.font;
        int y = h - 18;

        // Coordinates
        String coords = String.format("X:%.0f Y:%.0f Z:%.0f", drone.getX(), drone.getY(), drone.getZ());
        g.drawString(f, coords, cx - f.width(coords) / 2, y, GREEN, false);

        // Controls hint
        String tKey = ModKeyBindings.TARGETING_CAMERA.getKey().getDisplayName().getString();
        String gKey = ModKeyBindings.TOGGLE_GEAR.getKey().getDisplayName().getString();
        String rKey = ModKeyBindings.EXIT_DRONE.getKey().getDisplayName().getString();
        String hint = "[" + tKey + "] Camera  [" + gKey + "] Gear  [" + rKey + "] Exit";
        g.drawString(f, hint, cx - f.width(hint) / 2, h - 8, 0x666666, false);
    }

    private void renderCompass(GuiGraphics g, Minecraft mc, RemoteDroneEntity drone, int cx, int y) {
        var f = mc.font;
        float yaw = drone.getYRot();
        int width = 180;

        String[] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        float[] angles = {0, 45, 90, 135, 180, -135, -90, -45};

        for (int i = 0; i < dirs.length; i++) {
            float diff = Mth.wrapDegrees(angles[i] - yaw);
            if (Math.abs(diff) < 45) {
                int xPos = cx + (int)(diff * 2);
                int c = dirs[i].equals("N") ? RED : GREEN;
                g.drawString(f, dirs[i], xPos - f.width(dirs[i])/2, y, c, false);
            }
        }

        // Heading number
        int heading = (int) Mth.positiveModulo(yaw, 360);
        String hdg = String.format("%03d°", heading);
        g.drawString(f, hdg, cx - f.width(hdg)/2, y + 10, WHITE, false);

        // Center marker
        g.fill(cx - 1, y - 3, cx + 1, y, WHITE);
    }

    private void renderPitchLadder(GuiGraphics g, Minecraft mc, RemoteDroneEntity drone, int cx, int cy) {
        float pitch = drone.getXRot();
        int[] pitches = {-60, -45, -30, -15, 0, 15, 30, 45, 60};
        
        for (int p : pitches) {
            float diff = p - pitch;
            if (Math.abs(diff) < 30) {
                int yPos = cy + (int)(diff * 3);
                int len = p == 0 ? 60 : 30;
                int c = p == 0 ? GREEN : 0x88888888;
                
                g.fill(cx - len, yPos, cx - 10, yPos + 1, c);
                g.fill(cx + 10, yPos, cx + len, yPos + 1, c);
                
                if (p != 0) {
                    String label = String.valueOf(p);
                    g.drawString(mc.font, label, cx - len - mc.font.width(label) - 3, yPos - 4, c, false);
                }
            }
        }
    }

    private void renderHorizon(GuiGraphics g, RemoteDroneEntity drone, int cx, int cy) {
        float roll = drone.getRoll();
        int r = 50;
        
        // Roll indicator arc
        for (int i = -45; i <= 45; i += 15) {
            float angle = (float) Math.toRadians(i - 90 + roll);
            int x = cx + (int)(Math.cos(angle) * r);
            int y = cy + (int)(Math.sin(angle) * r);
            int c = i == 0 ? WHITE : 0x88888888;
            g.fill(x - 1, y - 1, x + 2, y + 2, c);
        }
        
        // Current roll indicator
        float angle = (float) Math.toRadians(-90);
        int x = cx + (int)(Math.cos(angle) * (r - 10));
        int y = cy + (int)(Math.sin(angle) * (r - 10));
        g.fill(x - 3, y, x + 4, y + 6, GREEN);
    }

    private void renderEntityMarkers(GuiGraphics g, Minecraft mc, RemoteDroneEntity drone, float pt, int w, int h) {
        List<LivingEntity> entities = drone.level().getEntitiesOfClass(LivingEntity.class, 
            drone.getBoundingBox().inflate(256), 
            e -> e.isAlive() && !(e instanceof Player p && p.isSpectator()));

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        for (LivingEntity e : entities) {
            if (e.getUUID().equals(drone.getUUID())) continue;
            
            Vec3 toEntity = e.position().subtract(drone.position()).normalize();
            Vec3 lookVec = drone.getViewVector(pt);
            if (toEntity.dot(lookVec) < 0.5) continue;

            Vec3 entityPos = new Vec3(
                Mth.lerp(pt, e.xo, e.getX()),
                Mth.lerp(pt, e.yo, e.getY()) + e.getBbHeight() / 2,
                Mth.lerp(pt, e.zo, e.getZ())
            );

            Vec3 screenPos = worldToScreen(entityPos, mc, w, h);
            if (screenPos == null) continue;

            float x = (float) screenPos.x;
            float y = (float) screenPos.y;
            if (x < 0 || x > w || y < 0 || y > h) continue;

            int ms = 10;
            int c = (e instanceof Player) ? CYAN : WHITE;
            
            // Diamond marker
            g.fill((int)x - ms, (int)y, (int)x, (int)y - ms, c);
            g.fill((int)x, (int)y - ms, (int)x + ms, (int)y, c);
            g.fill((int)x + ms, (int)y, (int)x, (int)y + ms, c);
            g.fill((int)x, (int)y + ms, (int)x - ms, (int)y, c);

            String distStr = (int)drone.distanceTo(e) + "m";
            g.drawString(mc.font, distStr, (int)x - mc.font.width(distStr)/2, (int)y + ms + 2, c, false);
        }
    }

    private Entity findTargetEntity(RemoteDroneEntity drone, Vec3 cameraPos, double range, float pt) {
        Vec3 lookVec = drone.getViewVector(pt);
        Vec3 endPos = cameraPos.add(lookVec.scale(range));
        AABB searchBox = new AABB(cameraPos, endPos).inflate(1.0);
        
        Entity closest = null;
        double closestDist = range;

        for (LivingEntity e : drone.level().getEntitiesOfClass(LivingEntity.class, searchBox, Entity::isAlive)) {
            AABB entityBox = e.getBoundingBox().inflate(0.3);
            var hitResult = entityBox.clip(cameraPos, endPos);
            if (hitResult.isPresent()) {
                double dist = cameraPos.distanceTo(hitResult.get());
                if (dist < closestDist) { closestDist = dist; closest = e; }
            }
        }
        return closest;
    }

    private Vec3 worldToScreen(Vec3 worldPos, Minecraft mc, int w, int h) {
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Vec3 delta = worldPos.subtract(cameraPos);
        
        float pitch = camera.getXRot();
        float yaw = camera.getYRot();
        
        Vector3f rotated = new Vector3f((float)delta.x, (float)delta.y, (float)delta.z);
        rotated.rotateY((float)Math.toRadians(yaw + 180));
        rotated.rotateX((float)Math.toRadians(-pitch));
        
        if (rotated.z <= 0) return null;
        
        float fov = (float)Math.toRadians(mc.options.fov().get());
        float aspect = (float)w / h;
        
        float screenX = w / 2f + (rotated.x / rotated.z) * (w / 2f) / (float)Math.tan(fov / 2);
        float screenY = h / 2f - (rotated.y / rotated.z) * (h / 2f) / (float)Math.tan(fov / 2) * aspect;
        
        return new Vec3(screenX, screenY, rotated.z);
    }
}
