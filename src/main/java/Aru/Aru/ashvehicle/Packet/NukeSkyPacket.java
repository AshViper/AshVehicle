package Aru.Aru.ashvehicle.Packet;

import Aru.Aru.ashvehicle.init.event.ClientRenderEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NukeSkyPacket {
    private final double x, y, z;
    private final float radius;
    private final int durationTicks;

    public NukeSkyPacket(double x, double y, double z, float radius, int durationTicks) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.durationTicks = durationTicks;
    }

    public static void encode(NukeSkyPacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeFloat(msg.radius);
        buf.writeInt(msg.durationTicks);
    }

    public static NukeSkyPacket decode(FriendlyByteBuf buf) {
        return new NukeSkyPacket(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readFloat(),
                buf.readInt()
        );
    }

    public static void handle(NukeSkyPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Run on client
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientRenderEvents.activateNukeSky(
                        new Vec3(msg.x, msg.y, msg.z),
                        msg.radius,
                        msg.durationTicks
                );
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
