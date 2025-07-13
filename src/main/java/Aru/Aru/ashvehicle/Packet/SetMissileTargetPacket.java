package Aru.Aru.ashvehicle.Packet;

import Aru.Aru.ashvehicle.entity.vehicle.ZumwaltEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import Aru.Aru.ashvehicle.entity.vehicle.SapsanEntity;

import java.util.function.Supplier;

public class SetMissileTargetPacket {
    private final int entityId;
    private final double x, y, z;

    public SetMissileTargetPacket(int entityId, double x, double y, double z) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(SetMissileTargetPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
    }

    public static SetMissileTargetPacket decode(FriendlyByteBuf buf) {
        return new SetMissileTargetPacket(buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void handle(SetMissileTargetPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Level level = player.level();
            Entity entity = level.getEntity(msg.entityId);
            if (entity instanceof SapsanEntity sapsan) {
                sapsan.shootMissileTo(player, new Vec3(msg.x, msg.y, msg.z));
            }
            if (entity instanceof ZumwaltEntity zumwalt) {
                zumwalt.shootMissileTo(player, new Vec3(msg.x, msg.y, msg.z));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
