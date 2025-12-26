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
 * Пакет для передачи движения мыши на сервер для управления дроном
 */
public class DroneMouseMovePacket {

    private final double speedX;
    private final double speedY;

    public DroneMouseMovePacket(double speedX, double speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public static void encode(DroneMouseMovePacket message, FriendlyByteBuf buf) {
        buf.writeDouble(message.speedX);
        buf.writeDouble(message.speedY);
    }

    public static DroneMouseMovePacket decode(FriendlyByteBuf buf) {
        return new DroneMouseMovePacket(buf.readDouble(), buf.readDouble());
    }

    public static void handle(DroneMouseMovePacket message, Supplier<NetworkEvent.Context> ctx) {
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
                    drone.mouseInput(message.speedX, message.speedY);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
