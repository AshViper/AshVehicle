package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.Packet.LockTargetPacket;
import Aru.Aru.ashvehicle.Packet.MultiLockTargetPacket;
import Aru.Aru.ashvehicle.Packet.SetMissileTargetPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AshVehicle.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        INSTANCE.registerMessage(
                packetId++,
                LockTargetPacket.class,
                LockTargetPacket::encode,
                LockTargetPacket::decode,
                LockTargetPacket::handle
        );

        INSTANCE.registerMessage(
                packetId++,
                MultiLockTargetPacket.class,
                MultiLockTargetPacket::encode,
                MultiLockTargetPacket::decode,
                MultiLockTargetPacket::handle
        );

        INSTANCE.registerMessage(
                packetId++,
                SetMissileTargetPacket.class,
                SetMissileTargetPacket::encode,
                SetMissileTargetPacket::decode,
                SetMissileTargetPacket::handle
        );

        // ここに他のパケットも追加できます
        // INSTANCE.registerMessage(packetId++, YourPacket.class, YourPacket::encode, YourPacket::decode, YourPacket::handle);
    }
}
