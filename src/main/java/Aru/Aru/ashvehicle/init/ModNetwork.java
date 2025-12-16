package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.Packet.NukeSkyPacket;
import Aru.Aru.ashvehicle.Packet.SetMissileTargetPacket;
import Aru.Aru.ashvehicle.Packet.TogglePodPacket;
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
                SetMissileTargetPacket.class,
                SetMissileTargetPacket::encode,
                SetMissileTargetPacket::decode,
                SetMissileTargetPacket::handle
        );
        
        INSTANCE.registerMessage(
                packetId++,
                TogglePodPacket.class,
                TogglePodPacket::encode,
                TogglePodPacket::decode,
                TogglePodPacket::handle
        );
        
        INSTANCE.registerMessage(
                packetId++,
                NukeSkyPacket.class,
                NukeSkyPacket::encode,
                NukeSkyPacket::decode,
                NukeSkyPacket::handle
        );
    }
}
