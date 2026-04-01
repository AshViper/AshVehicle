package Aru.Aru.ashvehicle;

import Aru.Aru.ashvehicle.init.*;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent; // This will likely be red; see notes below!
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(AshVehicle.MODID)
public class AshVehicle {
    public static final String MODID = "ashvehicle";
    private static final Logger LOGGER = LogUtils.getLogger();

    // The magical IEventBus injection!
    public AshVehicle(IEventBus modEventBus) {
        
        // Register your registries directly to the bus passed in the constructor
        ModEntities.REGISTRY.register(modEventBus);
        ModTabs.TABS.register(modEventBus);
        ModSounds.REGISTRY.register(modEventBus);
        ModItem.ITEMS.register(modEventBus);
        
        // Ensure this method in ModParticleTypes is updated to accept the bus!
        ModParticleTypes.register(modEventBus);
        
        // Register ourselves for standard game events
        // NeoForge.EVENT_BUS.register(this);
        
        // Add listeners for specific lifecycle events
        modEventBus.addListener(this::commonSetup);
    }

    // NOTE: This event was technically deprecated in late 1.20.1 and replaced in 1.21.
    // However, if your IDE still recognizes it via your specific NeoForge mappings, 
    // it will run. If it stays red, you will need to replace it with FMLConstructModEvent.
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("AshVehicle common setup initialized.");
        
        // In 1.21, networking is registered via RegisterPayloadHandlersEvent.
        // If ModNetwork.register() relies on the old SimpleChannel setup, it will crash here.
        ModNetwork.register(); 
    }
}