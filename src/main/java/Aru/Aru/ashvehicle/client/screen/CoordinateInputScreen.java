package Aru.Aru.ashvehicle.client.screen;

import Aru.Aru.ashvehicle.init.CoordinateTargetVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import Aru.Aru.ashvehicle.init.ModNetwork;
import Aru.Aru.ashvehicle.Packet.SetMissileTargetPacket;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CoordinateInputScreen extends Screen {
    private final CoordinateTargetVehicle vehicle;
    private EditBox xInput, yInput, zInput;
    private Button submitButton;

    private static final Map<Integer, Vec3> savedPositions = new HashMap<>();

    public CoordinateInputScreen(CoordinateTargetVehicle vehicle) {
        super(Component.literal("Target Coordinates"));
        this.vehicle = vehicle;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        Vec3 saved = savedPositions.getOrDefault(vehicle.getId(), new Vec3(0, 0, 0));

        // Use Locale.US to always use dot as decimal separator
        xInput = new EditBox(font, centerX - 50, centerY - 30, 100, 20, Component.literal("X"));
        xInput.setValue(String.format(Locale.US, "%.0f", saved.x));
        yInput = new EditBox(font, centerX - 50, centerY, 100, 20, Component.literal("Y"));
        yInput.setValue(String.format(Locale.US, "%.0f", saved.y));
        zInput = new EditBox(font, centerX - 50, centerY + 30, 100, 20, Component.literal("Z"));
        zInput.setValue(String.format(Locale.US, "%.0f", saved.z));

        this.addRenderableWidget(xInput);
        this.addRenderableWidget(yInput);
        this.addRenderableWidget(zInput);

        submitButton = Button.builder(Component.literal("FIRE"), btn -> {
            try {
                // Replace comma with dot for parsing (handles Russian locale)
                String xStr = xInput.getValue().replace(',', '.');
                String yStr = yInput.getValue().replace(',', '.');
                String zStr = zInput.getValue().replace(',', '.');
                
                double x = Double.parseDouble(xStr);
                double y = Double.parseDouble(yStr);
                double z = Double.parseDouble(zStr);

                savedPositions.put(vehicle.getId(), new Vec3(x, y, z));

                ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(),
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
