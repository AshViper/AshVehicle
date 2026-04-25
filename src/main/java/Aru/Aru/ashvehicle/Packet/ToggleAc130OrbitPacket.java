package Aru.Aru.ashvehicle.Packet;

import Aru.Aru.ashvehicle.entity.vehicle.Ac130uEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleAc130OrbitPacket {
    private final int entityId;

    public ToggleAc130OrbitPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(ToggleAc130OrbitPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static ToggleAc130OrbitPacket decode(FriendlyByteBuf buf) {
        return new ToggleAc130OrbitPacket(buf.readInt());
    }

    public static void handle(ToggleAc130OrbitPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Entity entity = player.level().getEntity(msg.entityId);
            if (entity instanceof Ac130uEntity ac130) {

                if (ac130.isOrbiting()) {
                    ac130.stopOrbit();
                } else {
                    ac130.startOrbit(player.position(), 150);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
