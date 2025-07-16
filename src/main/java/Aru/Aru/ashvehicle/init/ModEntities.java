package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.vehicle.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import Aru.Aru.ashvehicle.entity.weapon.*;

import java.util.function.BiFunction;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AshVehicle.MODID);

    // ===== サイズ定数 =====
    private static final float AIRCRAFT_WIDTH = 7.0f;
    private static final float AIRCRAFT_HEIGHT = 3.0f;

    private static final float TANK_WIDTH = 3.5f;
    private static final float TANK_HEIGHT = 2.5f;

    private static final float LARGE_TANK_HEIGHT = 4.0f;

    private static final float SMALL_WIDTH = 1.0f;
    private static final float SMALL_HEIGHT = 1.0f;

    private static final float IFV_WIDTH = 3.0f;
    private static final float IFV_HEIGHT = 2.0f;

    // ===== エンティティ登録 =====
    public static final RegistryObject<EntityType<Tom7Entity>> TOM_7 = registerSimple("tom_7", Tom7Entity::new, Tom7Entity::new, 1.05f, 1.0f);
    public static final RegistryObject<EntityType<UH60Entity>> UH_60 = registerSimple("uh_60", UH60Entity::new, UH60Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<MH60MEntity>> MH_60M = registerSimple("mh_60m", MH60MEntity::new, MH60MEntity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<F16Entity>> F_16 = registerSimple("f_16", F16Entity::new, F16Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<F4Entity>> F_4 = registerSimple("f_4", F4Entity::new, F4Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<Mig15Entity>> MIG_15 = registerSimple("mig_15", Mig15Entity::new, Mig15Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<Mig29Entity>> MIG_29 = registerSimple("mig_29", Mig29Entity::new, Mig29Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<T90Entity>> T_90 = registerSimple("t_90", T90Entity::new, T90Entity::new, TANK_WIDTH, TANK_HEIGHT);
    public static final RegistryObject<EntityType<M1A1AbramsEntity>> M1A1_ABRAMS = registerSimple("m1a1abrams", M1A1AbramsEntity::new, M1A1AbramsEntity::new, TANK_WIDTH, TANK_HEIGHT);
    public static final RegistryObject<EntityType<KV2Entity>> KV_2 = registerSimple("kv-2", KV2Entity::new, KV2Entity::new, TANK_WIDTH, LARGE_TANK_HEIGHT);
    public static final RegistryObject<EntityType<GepardEntity>> GEPARD_1A2 = registerSimple("gepard-1a2", GepardEntity::new, GepardEntity::new, TANK_WIDTH, LARGE_TANK_HEIGHT);
    public static final RegistryObject<EntityType<SU33Entity>> SU_33 = registerSimple("su-33", SU33Entity::new, SU33Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<SU34Entity>> SU_34 = registerSimple("su-34", SU34Entity::new, SU34Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<SU25Entity>> SU_25 = registerSimple("su-25", SU25Entity::new, SU25Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<M3A3BradleyEntity>> M3A3_BRADLEY = registerSimple("m3a3-bradley", M3A3BradleyEntity::new, M3A3BradleyEntity::new, IFV_WIDTH, IFV_HEIGHT);
    public static final RegistryObject<EntityType<F39EEntity>> F_39E = registerSimple("f-39e", F39EEntity::new, F39EEntity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<F35Entity>> F_35 = registerSimple("f-35", F35Entity::new, F35Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<B2Entity>> B_2 = registerSimple("b-2", B2Entity::new, B2Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<F22Entity>> F_22 = registerSimple("f-22", F22Entity::new, F22Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<SapsanEntity>> SAPSAN_GRIM2 = registerSimple("sapsan-grim2", SapsanEntity::new, SapsanEntity::new, TANK_WIDTH, TANK_HEIGHT);
    public static final RegistryObject<EntityType<F18Entity>> F_18 = registerSimple("f-18", F18Entity::new, F18Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<F117Entity>> F_117 = registerSimple("f-117", F117Entity::new, F117Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<ZumwaltEntity>> ZYNWALT = registerSimple("zumwalt", ZumwaltEntity::new, ZumwaltEntity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
    public static final RegistryObject<EntityType<SU57Entity>> SU_57 = registerSimple("su-57", SU57Entity::new, SU57Entity::new, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);

    public static final RegistryObject<EntityType<Aam4Entity>> AAM_4 = registerSimple("aam-4", Aam4Entity::new, Aam4Entity::new, SMALL_WIDTH, SMALL_HEIGHT);
    public static final RegistryObject<EntityType<GBU57Entity>> GBU_57 = registerSimple("gbu-57", GBU57Entity::new, GBU57Entity::new, SMALL_WIDTH, SMALL_HEIGHT);
    public static final RegistryObject<EntityType<NuclearBombEntity>> NUCLEAR_BOMB = registerSimple("nuclear-bomb", NuclearBombEntity::new, NuclearBombEntity::new, SMALL_WIDTH, SMALL_HEIGHT);
    public static final RegistryObject<EntityType<JassmXREntity>> JASSM_XR = registerSimple("jassm-xr", JassmXREntity::new, JassmXREntity::new, SMALL_WIDTH, SMALL_HEIGHT);
    public static final RegistryObject<EntityType<BallisticMissileEntity>> BALLISTIC_MISSILE = registerSimple("ballistic-missile", BallisticMissileEntity::new, BallisticMissileEntity::new, SMALL_WIDTH, SMALL_HEIGHT);
    public static final RegistryObject<EntityType<NapalmBombEntity>> NAPALM_BOMB = registerSimple("napalm-bomb", NapalmBombEntity::new, NapalmBombEntity::new, SMALL_WIDTH, SMALL_HEIGHT);
    public static final RegistryObject<EntityType<TomahawkEntity>> TOMAHAWK = registerSimple("tomahawk", TomahawkEntity::new, TomahawkEntity::new, SMALL_WIDTH, SMALL_HEIGHT);

    // ===== ヘルパー =====
    private static <T extends Entity> RegistryObject<EntityType<T>> registerSimple(
            String name,
            BiFunction<PlayMessages.SpawnEntity, Level, T> clientFactory,
            EntityType.EntityFactory<T> serverFactory,
            float width, float height) {

        return register(name, EntityType.Builder.<T>of(serverFactory, MobCategory.MISC)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .setCustomClientFactory(clientFactory)
                .setTrackingRange(512)
                .clientTrackingRange(1028)
                .fireImmune()
                .sized(width, height)
        );
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return REGISTRY.register(name, () -> builder.build(name));
    }
}
