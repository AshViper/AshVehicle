package Aru.Aru.ashvehicle.client.screen;

import Aru.Aru.ashvehicle.entity.vehicle.ZumwaltEntity;
import Aru.Aru.ashvehicle.init.CoordinateTargetVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import Aru.Aru.ashvehicle.entity.vehicle.SapsanEntity;
import Aru.Aru.ashvehicle.init.ModNetwork;
import Aru.Aru.ashvehicle.Packet.SetMissileTargetPacket;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class CoordinateInputScreen extends Screen {
    private final CoordinateTargetVehicle vehicle;
    private EditBox xInput, yInput, zInput;
    private Button submitButton;

    // Entity ID → 座標（Vec3）を保存するマップ
    private static final Map<Integer, Vec3> savedPositions = new HashMap<>();

    public CoordinateInputScreen(CoordinateTargetVehicle vehicle) {
        super(Component.literal("座標を入力"));
        this.vehicle = vehicle;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        Vec3 saved = savedPositions.getOrDefault(vehicle.getId(), new Vec3(0, 0, 0));

        xInput = new EditBox(font, centerX - 50, centerY - 30, 100, 20, Component.literal("X"));
        xInput.setValue(String.format("%.2f", saved.x));
        yInput = new EditBox(font, centerX - 50, centerY, 100, 20, Component.literal("Y"));
        yInput.setValue(String.format("%.2f", saved.y));
        zInput = new EditBox(font, centerX - 50, centerY + 30, 100, 20, Component.literal("Z"));
        zInput.setValue(String.format("%.2f", saved.z));

        this.addRenderableWidget(xInput);
        this.addRenderableWidget(yInput);
        this.addRenderableWidget(zInput);

        submitButton = Button.builder(Component.literal("fire"), btn -> {
            try {
                double x = Double.parseDouble(xInput.getValue());
                double y = Double.parseDouble(yInput.getValue());
                double z = Double.parseDouble(zInput.getValue());

                // Entity IDごとに保存
                savedPositions.put(vehicle.getId(), new Vec3(x, y, z));

                // パケット送信
                ModNetwork.INSTANCE.sendToServer(
                        new SetMissileTargetPacket(vehicle.getId(), x, y, z)
                );
                Minecraft.getInstance().setScreen(null);
            } catch (NumberFormatException ignored) {}
        }).bounds(centerX - 40, centerY + 60, 80, 20).build();

        this.addRenderableWidget(submitButton);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}


