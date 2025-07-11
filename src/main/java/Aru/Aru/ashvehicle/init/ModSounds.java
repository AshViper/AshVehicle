package Aru.Aru.ashvehicle.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import Aru.Aru.ashvehicle.ExtensionTest;

/**
 * Класс для регистрации звуков, используемых в моде
 */
public class ModSounds {
    // Создаем регистр для звуков
    public static final DeferredRegister<SoundEvent> REGISTRY = 
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ExtensionTest.MODID);
    
    // Регистрируем звуки для M1A1 Abrams
    public static final RegistryObject<SoundEvent> M1A1_ABRAMS_FIRE = register("m1a1_fire");
    public static final RegistryObject<SoundEvent> M1A1_ABRAMS_ENGINE = register("m1a1_engine");
    public static final RegistryObject<SoundEvent> M1A1_ABRAMS_RELOAD = register("m1a1_reload");
    public static final RegistryObject<SoundEvent> SU57_ENGINE = register("su57_engine");
    
    // Дополнительные звуковые события для совместимости с базовым модом
    public static final RegistryObject<SoundEvent> WHEEL_STEP = register("wheel_step");

    public static final RegistryObject<SoundEvent> YX_100_VERY_FAR = register("yx_100_veryfar");
    public static final RegistryObject<SoundEvent> YX_100_RELOAD = register("yx_100_reload");
    
    /**
     * Вспомогательный метод для регистрации звуков
     * @param name Название звука (без пространства имен)
     * @return RegistryObject для звукового события
     */
    private static RegistryObject<SoundEvent> register(String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ExtensionTest.MODID, name)));
    }
} 