package Aru.Aru.ashvehicle.Packet;

import Aru.Aru.ashvehicle.entity.vehicle.SapsanEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TogglePodPacket {
    private final int entityId;

    public TogglePodPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(TogglePodPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static TogglePodPacket decode(FriendlyByteBuf buf) {
        return new TogglePodPacket(buf.readInt());
    }

    public static void handle(TogglePodPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            
            Entity entity = player.level().getEntity(msg.entityId);
            if (entity instanceof SapsanEntity sapsan) {
                // Check if player is riding this vehicle
                if (player.getVehicle() == sapsan) {
                    sapsan.togglePod();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
