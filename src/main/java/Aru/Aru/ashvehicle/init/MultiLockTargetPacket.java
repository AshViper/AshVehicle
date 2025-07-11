package Aru.Aru.ashvehicle.init;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class MultiLockTargetPacket {
    private final List<UUID> targetUUIDs;

    public MultiLockTargetPacket(List<UUID> targetUUIDs) {
        this.targetUUIDs = targetUUIDs;
    }

    public static void encode(MultiLockTargetPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.targetUUIDs.size());
        for (UUID uuid : msg.targetUUIDs) {
            buf.writeUUID(uuid);
        }
    }

    public static MultiLockTargetPacket decode(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<UUID> targets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            targets.add(buf.readUUID());
        }
        return new MultiLockTargetPacket(targets);
    }

    public static void handle(MultiLockTargetPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            // UUIDが空 → ロック解除として扱う
            if (msg.targetUUIDs.isEmpty()) {
                ClientTargetingData.clearLockedTargets();
                return;
            }

            List<Entity> locked = new ArrayList<>();
            for (UUID uuid : msg.targetUUIDs) {
                for (Entity entity : mc.level.entitiesForRendering()) {
                    if (entity != null && entity.getUUID().equals(uuid)) {
                        locked.add(entity);
                        break;
                    }
                }
            }

            ClientTargetingData.setLockedTargets(locked);
        });
        ctx.get().setPacketHandled(true);
    }
}