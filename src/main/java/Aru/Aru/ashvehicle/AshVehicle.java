package Aru.Aru.ashvehicle;

import Aru.Aru.ashvehicle.init.*;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
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
        ModTabs.TABS.register(bus);
        ModSounds.REGISTRY.register(bus);
        ModItem.ITEMS.register(bus);
        ModParticleTypes.register(bus);
        
        // Key mappings are registered via ClientKeyRegister @Mod.EventBusSubscriber
        
        bus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("common setup");
        // Register network packets
        ModNetwork.register();
    }
}
