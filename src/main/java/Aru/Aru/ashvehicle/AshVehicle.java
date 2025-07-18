package Aru.Aru.ashvehicle;

import Aru.Aru.ashvehicle.client.particle.AfterburnerFlameParticleProvider;
import Aru.Aru.ashvehicle.init.*;
import com.mojang.logging.LogUtils;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AshVehicle.MODID)
public class AshVehicle {
    public static final String MODID = "ashvehicle";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AshVehicle() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register entities for your extension mod
        ModEntities.REGISTRY.register(bus);

        // Register a separate tab
        // TODO directly register into SuperbWarfare's containers
        ModTabs.TABS.register(bus);
        ModSounds.REGISTRY.register(bus);
        ModParticleTypes.register(bus);
        bus.addListener(this::commonSetup);
        ModNetwork.register();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("common setup");
    }
}
