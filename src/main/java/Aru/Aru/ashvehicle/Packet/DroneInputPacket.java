package Aru.Aru.ashvehicle.Packet;

import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import Aru.Aru.ashvehicle.tools.DroneFindUtil;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.Monitor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Пакет для передачи клавиатурного ввода на сервер для управления дроном
 */
public class DroneInputPacket {

    private final boolean forward;
    private final boolean backward;
    private final boolean left;
    private final boolean right;
    private final boolean up;
    private final boolean down;

    public DroneInputPacket(boolean forward, boolean backward, boolean left, boolean right, boolean up, boolean down) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.up = up;
        this.down = down;
    }

    public static void encode(DroneInputPacket message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.forward);
        buf.writeBoolean(message.backward);
        buf.writeBoolean(message.left);
        buf.writeBoolean(message.right);
        buf.writeBoolean(message.up);
        buf.writeBoolean(message.down);
    }

    public static DroneInputPacket decode(FriendlyByteBuf buf) {
        return new DroneInputPacket(
            buf.readBoolean(),
            buf.readBoolean(),
            buf.readBoolean(),
            buf.readBoolean(),
            buf.readBoolean(),
            buf.readBoolean()
        );
    }

    public static void handle(DroneInputPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack stack = player.getMainHandItem();

            // Проверяем что игрок держит монитор и он активен
            if (stack.is(ModItems.MONITOR.get()) 
                && stack.getOrCreateTag().getBoolean("Using") 
                && stack.getOrCreateTag().getBoolean("Linked")) {
                
                String droneUUID = stack.getOrCreateTag().getString(Monitor.LINKED_DRONE);
                RemoteDroneEntity drone = DroneFindUtil.findRemoteDrone(player.level(), droneUUID);
                
                if (drone != null) {
                    drone.setForwardInputDown(message.forward);
                    drone.setBackInputDown(message.backward);
                    drone.setLeftInputDown(message.left);
                    drone.setRightInputDown(message.right);
                    drone.setUpInputDown(message.up);
                    drone.setDownInputDown(message.down);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
