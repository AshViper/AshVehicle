package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.vehicle.*;
import com.atsuishio.superbwarfare.entity.vehicle.A10Entity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AshVehicle.MODID);

    // ===== サイズ定数 =====
    private static final float AIRCRAFT_WIDTH = 7.0f;
    private static final float AIRCRAFT_HEIGHT = 3.0f;

    private static final float SHIP_WIDTH = 3.0f;
    private static final float SHIP_HEIGHT = 3.0f;

    private static final float TANK_WIDTH = 3.5f;
    private static final float TANK_HEIGHT = 2.5f;

    private static final float LARGE_TANK_HEIGHT = 4.0f;

    private static final float IFV_WIDTH = 3.0f;
    private static final float IFV_HEIGHT = 2.0f;

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(name, () -> entityTypeBuilder.build(name));
    }

    // ===== エンティティ登録 =====
    // UH-60（元から正しい形式）
    public static final RegistryObject<EntityType<UH60Entity>> UH_60 =
            register("uh_60",
                    EntityType.Builder.of(UH60Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(4.5F, 3.5F));

    // MH-60M
    public static final RegistryObject<EntityType<MH60MEntity>> MH_60M =
            register("mh_60m",
                    EntityType.Builder.of(MH60MEntity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // F-16
    public static final RegistryObject<EntityType<F16Entity>> F_16 =
            register("f_16",
                    EntityType.Builder.of(F16Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // F-15
    public static final RegistryObject<EntityType<F15Entity>> F_15 =
            register("f_15",
                    EntityType.Builder.of(F15Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // F-4
    public static final RegistryObject<EntityType<F4Entity>> F_4 =
            register("f_4",
                    EntityType.Builder.of(F4Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // MIG-15
    public static final RegistryObject<EntityType<Mig15Entity>> MIG_15 =
            register("mig_15",
                    EntityType.Builder.of(Mig15Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // MIG-29
    public static final RegistryObject<EntityType<Mig29Entity>> MIG_29 =
            register("mig_29",
                    EntityType.Builder.of(Mig29Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // T-90
    public static final RegistryObject<EntityType<T90Entity>> T_90 =
            register("t_90",
                    EntityType.Builder.of(T90Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(TANK_WIDTH, TANK_HEIGHT));

    // M1A1 Abrams
    public static final RegistryObject<EntityType<M1A1AbramsEntity>> M1A1_ABRAMS =
            register("m1a1abrams",
                    EntityType.Builder.of(M1A1AbramsEntity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(TANK_WIDTH, TANK_HEIGHT));

    // KV-2
    public static final RegistryObject<EntityType<KV2Entity>> KV_2 =
            register("kv-2",
                    EntityType.Builder.of(KV2Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(TANK_WIDTH, LARGE_TANK_HEIGHT));

    // Gepard
    public static final RegistryObject<EntityType<GepardEntity>> GEPARD_1A2 =
            register("gepard-1a2",
                    EntityType.Builder.of(GepardEntity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(TANK_WIDTH, LARGE_TANK_HEIGHT));

    // SU-33
    public static final RegistryObject<EntityType<SU33Entity>> SU_33 =
            register("su-33",
                    EntityType.Builder.of(SU33Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // SU-34
    public static final RegistryObject<EntityType<SU34Entity>> SU_34 =
            register("su-34",
                    EntityType.Builder.of(SU34Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // SU-25
    public static final RegistryObject<EntityType<SU25Entity>> SU_25 =
            register("su-25",
                    EntityType.Builder.of(SU25Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // M3A3 Bradley
    public static final RegistryObject<EntityType<M3A3BradleyEntity>> M3A3_BRADLEY =
            register("m3a3-bradley",
                    EntityType.Builder.of(M3A3BradleyEntity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(IFV_WIDTH, IFV_HEIGHT));

    // F-39E
    public static final RegistryObject<EntityType<F39EEntity>> F_39E =
            register("f-39e",
                    EntityType.Builder.of(F39EEntity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // F-35
    public static final RegistryObject<EntityType<F35Entity>> F_35 =
            register("f-35",
                    EntityType.Builder.of(F35Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // B-2
    public static final RegistryObject<EntityType<B2Entity>> B_2 =
            register("b-2",
                    EntityType.Builder.of(B2Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // F-22
    public static final RegistryObject<EntityType<F22Entity>> F_22 =
            register("f-22",
                    EntityType.Builder.of(F22Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // Sapsan GRIM-2
    public static final RegistryObject<EntityType<SapsanEntity>> SAPSAN_GRIM2 =
            register("sapsan-grim2",
                    EntityType.Builder.of(SapsanEntity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(TANK_WIDTH, TANK_HEIGHT));

    // F-18
    public static final RegistryObject<EntityType<F18Entity>> F_18 =
            register("f-18",
                    EntityType.Builder.of(F18Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // F-117
    public static final RegistryObject<EntityType<F117Entity>> F_117 =
            register("f-117",
                    EntityType.Builder.of(F117Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // Zumwalt
    public static final RegistryObject<EntityType<ZumwaltEntity>> ZYNWALT =
            register("zumwalt",
                    EntityType.Builder.of(ZumwaltEntity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(SHIP_WIDTH, SHIP_HEIGHT));

    // SU-57
    public static final RegistryObject<EntityType<SU57Entity>> SU_57 =
            register("su-57",
                    EntityType.Builder.of(SU57Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // V-22
    public static final RegistryObject<EntityType<V22Entity>> V_22 =
            register("v-22",
                    EntityType.Builder.of(V22Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // SU-27
    public static final RegistryObject<EntityType<SU27Entity>> SU_27 =
            register("su-27",
                    EntityType.Builder.of(SU27Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // J-20
    public static final RegistryObject<EntityType<J20Entity>> J_20 =
            register("j-20",
                    EntityType.Builder.of(J20Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // F-2
    public static final RegistryObject<EntityType<F2Entity>> F_2 =
            register("f_2",
                    EntityType.Builder.of(F2Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // Pantsir-S1
    public static final RegistryObject<EntityType<pantsirS1Entity>> pantsir_S1 =
            register("pantsir-s1",
                    EntityType.Builder.of(pantsirS1Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(TANK_WIDTH, TANK_HEIGHT));

    // Eurofighter
    public static final RegistryObject<EntityType<EuroFighterEntity>> EuroFighter =
            register("eurofighter",
                    EntityType.Builder.of(EuroFighterEntity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // Reaper UAV
    public static final RegistryObject<EntityType<ReaperEntity>> REAPER =
            register("reaper",
                    EntityType.Builder.of(ReaperEntity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // YF-23
    public static final RegistryObject<EntityType<YF23Entity>> YF_23 =
            register("yf-23",
                    EntityType.Builder.of(YF23Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // X-47B
    public static final RegistryObject<EntityType<X47BEntity>> X_47B =
            register("x-47b",
                    EntityType.Builder.of(X47BEntity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

    // Rubber Boat
    public static final RegistryObject<EntityType<RubberBoatEntity>> RUBBER_BOAT =
            register("rubber_boat",
                    EntityType.Builder.of(RubberBoatEntity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(SHIP_WIDTH, SHIP_HEIGHT));

    // M777
    public static final RegistryObject<EntityType<m777Entity>> M_777 =
            register("m_777",
                    EntityType.Builder.of(m777Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(SHIP_WIDTH, SHIP_HEIGHT));

    // RAH-66
    public static final RegistryObject<EntityType<Rah66Entity>> RAH_66 =
            register("rah_66",
                    EntityType.Builder.of(Rah66Entity::new, MobCategory.MISC)
                            .setTrackingRange(512)
                            .setUpdateInterval(1)
                            .fireImmune()
                            .sized(AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT));

}
