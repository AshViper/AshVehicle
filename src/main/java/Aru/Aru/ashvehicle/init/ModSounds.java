package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.AshVehicle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Класс для регистрации звуков, используемых в моде
 */
public class ModSounds {
    // Создаем регистр для звуков
    public static final DeferredRegister<SoundEvent> REGISTRY = 
        DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, AshVehicle.MODID);
    
    // Регистрируем звуки для M1A1 Abrams
    public static final DeferredHolder<SoundEvent, SoundEvent> M1A1_ABRAMS_FIRE = register("m1a1_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> M1A1_ABRAMS_ENGINE = register("m1a1_engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> M1A1_ABRAMS_RELOAD = register("m1a1_reload");
    public static final DeferredHolder<SoundEvent, SoundEvent> SU57_ENGINE = register("su57_engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> J20_ENGINE = register("j-20_engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> REAPER_ENGINE = register("reaper-engine");
    
    // Дополнительные звуковые события для совместимости с базовым модом
    public static final DeferredHolder<SoundEvent, SoundEvent> WHEEL_STEP = register("wheel_step");

    public static final DeferredHolder<SoundEvent, SoundEvent> YX_100_VERY_FAR = register("yx_100_veryfar");
    public static final DeferredHolder<SoundEvent, SoundEvent> YX_100_RELOAD = register("yx_100_reload");
    
    /**
     * Вспомогательный метод для регистрации звуков
     * @param name Название звука (без пространства имен)
     * @return DeferredHolder для звукового события
     */
    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(AshVehicle.MODID, name)));
    }
} 