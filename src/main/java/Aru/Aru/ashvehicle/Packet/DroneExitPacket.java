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
 * Packet to exit drone control
 */
public class DroneExitPacket {

    public DroneExitPacket() {
    }

    public DroneExitPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack stack = player.getMainHandItem();
            if (!stack.is(ModItems.MONITOR.get())) return;
            if (!stack.getOrCreateTag().getBoolean("Using")) return;
            if (!stack.getOrCreateTag().getBoolean("Linked")) return;

            String droneUUID = stack.getOrCreateTag().getString(Monitor.LINKED_DRONE);
            RemoteDroneEntity drone = DroneFindUtil.findRemoteDrone(player.level(), droneUUID);
            
            // Stop using monitor
            stack.getOrCreateTag().putBoolean("Using", false);
            
            // Eject player from drone
            if (drone != null && drone.hasPassenger(player)) {
                drone.ejectController(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
