package Aru.Aru.ashvehicle.Packet;

import Aru.Aru.ashvehicle.entity.vehicle.base.RemoteDroneEntity;
import Aru.Aru.ashvehicle.tools.DroneFindUtil;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.Monitor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Пакет для стрельбы дрона через монитор
 */
public class DroneFirePacket {

    private final @Nullable UUID targetEntityUUID;
    private final @Nullable Vector3f targetPos;

    public DroneFirePacket(@Nullable UUID targetEntityUUID) {
        this.targetEntityUUID = targetEntityUUID;
        this.targetPos = null;
    }

    public DroneFirePacket(@Nullable UUID targetEntityUUID, @Nullable Vector3f targetPos) {
        this.targetEntityUUID = targetEntityUUID;
        this.targetPos = targetPos;
    }

    public static void encode(DroneFirePacket message, FriendlyByteBuf buf) {
        buf.writeOptional(Optional.ofNullable(message.targetEntityUUID), FriendlyByteBuf::writeUUID);
        
        boolean hasTargetPos = message.targetPos != null;
        buf.writeBoolean(hasTargetPos);
        if (hasTargetPos) {
            buf.writeVector3f(message.targetPos);
        }
    }

    public static DroneFirePacket decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readOptional(FriendlyByteBuf::readUUID).orElse(null);
        boolean hasTargetPos = buf.readBoolean();
        if (hasTargetPos) {
            return new DroneFirePacket(uuid, buf.readVector3f());
        }
        return new DroneFirePacket(uuid);
    }

    public static void handle(DroneFirePacket message, Supplier<NetworkEvent.Context> ctx) {
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
                    // Вызываем стрельбу вехикла
                    Vec3 targetVec = message.targetPos != null ? new Vec3(message.targetPos) : null;
                    drone.vehicleShoot(player, message.targetEntityUUID, targetVec);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
