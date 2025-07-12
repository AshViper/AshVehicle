package Aru.Aru.ashvehicle.Packet;

import Aru.Aru.ashvehicle.init.ClientTargetingData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class LockTargetPacket {
    private final UUID targetUUID;

    public LockTargetPacket(UUID targetUUID) {
        this.targetUUID = targetUUID;
    }

    public static void encode(LockTargetPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.targetUUID);
    }

    public static LockTargetPacket decode(FriendlyByteBuf buf) {
        return new LockTargetPacket(buf.readUUID());
    }

    public static void handle(LockTargetPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                // UUID(0,0) → ロック解除とみなす
                if (msg.targetUUID.getLeastSignificantBits() == 0 && msg.targetUUID.getMostSignificantBits() == 0) {
                    ClientTargetingData.clearLockedTarget();
                    return;
                }

                // エンティティ検索
                Entity target = null;
                for (Entity entity : mc.level.entitiesForRendering()) {
                    if (entity != null && entity.getUUID().equals(msg.targetUUID)) {
                        target = entity;
                        break;
                    }
                }

                if (target != null) {
                    ClientTargetingData.setLockedTarget(target);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
