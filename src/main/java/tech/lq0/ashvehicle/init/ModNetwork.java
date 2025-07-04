package tech.lq0.ashvehicle.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tech.lq0.ashvehicle.ExtensionTest;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ExtensionTest.MODID, "main"),
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

        // ここに他のパケットも追加できます
        // INSTANCE.registerMessage(packetId++, YourPacket.class, YourPacket::encode, YourPacket::decode, YourPacket::handle);
    }
}
