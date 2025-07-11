package Aru.Aru.ashvehicle.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import Aru.Aru.ashvehicle.entity.SapsanEntity;
import Aru.Aru.ashvehicle.init.ModNetwork;
import Aru.Aru.ashvehicle.init.SetMissileTargetPacket;

public class CoordinateInputScreen extends Screen {
    private final SapsanEntity vehicle;
    private EditBox xInput, yInput, zInput;
    private Button submitButton;

    public CoordinateInputScreen(SapsanEntity vehicle) {
        super(Component.literal("座標を入力"));
        this.vehicle = vehicle;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        xInput = new EditBox(font, centerX - 50, centerY - 30, 100, 20, Component.literal("X"));
        yInput = new EditBox(font, centerX - 50, centerY,      100, 20, Component.literal("Y"));
        zInput = new EditBox(font, centerX - 50, centerY + 30, 100, 20, Component.literal("Z"));

        this.addRenderableWidget(xInput);
        this.addRenderableWidget(yInput);
        this.addRenderableWidget(zInput);

        submitButton = Button.builder(Component.literal("fire"), btn -> {
            try {
                double x = Double.parseDouble(xInput.getValue());
                double y = Double.parseDouble(yInput.getValue());
                double z = Double.parseDouble(zInput.getValue());

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
