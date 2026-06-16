package Aru.Aru.ashvehicle.Packet;
import Aru.Aru.ashvehicle.entity.vehicle.F35Entity;
import Aru.Aru.ashvehicle.entity.vehicle.SapsanEntity;
import Aru.Aru.ashvehicle.entity.vehicle.V22Entity;
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
            if (entity instanceof SapsanEntity sapsan && player.getVehicle() == sapsan) {
                sapsan.togglePod();
            } else if (entity instanceof F35Entity f35 && player.getVehicle() == f35) {
                f35.toggleVtolMode();
            } else if (entity instanceof V22Entity v22 && player.getVehicle() == v22) {
                v22.toggleVtolMode();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
