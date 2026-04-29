package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Класс для регистрации звуков, используемых в моде
 */
public class ModSounds {
    // Создаем регистр для звуков
    public static final DeferredRegister<SoundEvent> REGISTRY = 
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AshVehicle.MODID);

    public static final RegistryObject<SoundEvent> MISSILE_SHOT = register("missile_shot");
    public static final RegistryObject<SoundEvent> M61_FIRE = register("m61_fire");
    public static final RegistryObject<SoundEvent> GSH30_FIRE = register("gsh30_fire");

    public static final RegistryObject<SoundEvent> SU57_ENGINE = register("su57_engine");
    public static final RegistryObject<SoundEvent> J20_ENGINE = register("j-20_engine");
    public static final RegistryObject<SoundEvent> REAPER_ENGINE = register("reaper-engine");
    public static final RegistryObject<SoundEvent> ENGINE1 = register("engine1_medium");
    public static final RegistryObject<SoundEvent> ENGINE1_START = register("engine1_start");
    public static final RegistryObject<SoundEvent> ENGINE2 = register("engine2_medium");
    public static final RegistryObject<SoundEvent> ENGINE2_START = register("engine2_start");

    private static RegistryObject<SoundEvent> register(String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(AshVehicle.MODID, name)));
    }
} 