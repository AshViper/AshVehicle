package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.entity.projectile.*;
import Aru.Aru.ashvehicle.entity.vehicle.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AshVehicle.MODID);

    // ===== サイズ定数 =====
    private static final float AIRCRAFT_W = 7.0f, AIRCRAFT_H = 3.0f;
    private static final float SHIP_W = 3.0f, SHIP_H = 3.0f;
    private static final float TANK_W = 3.5f, TANK_H = 2.5f;
    private static final float TANK_H_LARGE = 4.0f;
    private static final float IFV_W = 3.0f, IFV_H = 2.0f;
    private static final float SMALL_W = 1.0f, SMALL_H = 1.0f;

    // ===== 共通ビルダー =====
    private static <T extends Entity> EntityType.Builder<T> vehicle(EntityType.EntityFactory<T> factory, float w, float h) {
        return EntityType.Builder.of(factory, MobCategory.MISC)
                .setTrackingRange(512)
                .setUpdateInterval(1)
                .fireImmune()
                .sized(w, h);
    }

    // ===== register ショート化 =====
    private static <T extends Entity> RegistryObject<EntityType<T>> reg(String name, EntityType.EntityFactory<T> factory, float w, float h) {
        return REGISTRY.register(name, () -> vehicle(factory, w, h).build(name));
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> weapon(String name, EntityType.EntityFactory<T> factory) {
        return reg(name, factory, SMALL_W, SMALL_H);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> aircraft(String name, EntityType.EntityFactory<T> factory) {
        return reg(name, factory, AIRCRAFT_W, AIRCRAFT_H);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> tank(String name, EntityType.EntityFactory<T> factory) {
        return reg(name, factory, TANK_W, TANK_H);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> largeTank(String name, EntityType.EntityFactory<T> factory) {
        return reg(name, factory, TANK_W, TANK_H_LARGE);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> ifv(String name, EntityType.EntityFactory<T> factory) {
        return reg(name, factory, IFV_W, IFV_H);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> ship(String name, EntityType.EntityFactory<T> factory) {
        return reg(name, factory, SHIP_W, SHIP_H);
    }

    // ===== 実際の登録 =====
    public static final RegistryObject<EntityType<Aim9Entity>> AIM9 = weapon("aim9", Aim9Entity::new);
    public static final RegistryObject<EntityType<Aim120Entity>> AIM120 = weapon("aim120", Aim120Entity::new);
    public static final RegistryObject<EntityType<R60Entity>> R60 = weapon("r60", R60Entity::new);
    public static final RegistryObject<EntityType<Agm114Entity>> AGM114 = weapon("agm114", Agm114Entity::new);
    public static final RegistryObject<EntityType<BallisticMissileEntity>> BALLISTIC_MISSILE = weapon("ballistic-missile", BallisticMissileEntity::new);
    public static final RegistryObject<EntityType<ToiletBombEntity>> TOILETBOMB = weapon("toiletbomb", ToiletBombEntity::new);
    public static final RegistryObject<EntityType<UH60Entity>> UH_60 = reg("uh_60", UH60Entity::new, 4.5f, 3.5f);
    public static final RegistryObject<EntityType<MH60MEntity>> MH_60M = aircraft("mh_60m", MH60MEntity::new);
    public static final RegistryObject<EntityType<F16Entity>> F_16 = aircraft("f_16", F16Entity::new);
    public static final RegistryObject<EntityType<F15Entity>> F_15 = aircraft("f_15", F15Entity::new);
    public static final RegistryObject<EntityType<F4Entity>> F_4 = aircraft("f_4", F4Entity::new);
    public static final RegistryObject<EntityType<Mig15Entity>> MIG_15 = aircraft("mig_15", Mig15Entity::new);
    public static final RegistryObject<EntityType<Mig29Entity>> MIG_29 = aircraft("mig_29", Mig29Entity::new);
    public static final RegistryObject<EntityType<T90Entity>> T_90 = tank("t_90", T90Entity::new);
    public static final RegistryObject<EntityType<M1A1AbramsEntity>> M1A1_ABRAMS = tank("m1a1abrams", M1A1AbramsEntity::new);
    public static final RegistryObject<EntityType<TosEntity>> TOS = tank("tos", TosEntity::new);
    public static final RegistryObject<EntityType<KV2Entity>> KV_2 = largeTank("kv-2", KV2Entity::new);
    public static final RegistryObject<EntityType<GepardEntity>> GEPARD_1A2 = largeTank("gepard-1a2", GepardEntity::new);
    public static final RegistryObject<EntityType<SU33Entity>> SU_33 = aircraft("su-33", SU33Entity::new);
    public static final RegistryObject<EntityType<SU34Entity>> SU_34 = aircraft("su-34", SU34Entity::new);
    public static final RegistryObject<EntityType<SU25Entity>> SU_25 = aircraft("su-25", SU25Entity::new);
    public static final RegistryObject<EntityType<M3A3BradleyEntity>> M3A3_BRADLEY = ifv("m3a3-bradley", M3A3BradleyEntity::new);
    public static final RegistryObject<EntityType<F39EEntity>> F_39E = aircraft("f-39e", F39EEntity::new);
    public static final RegistryObject<EntityType<F35Entity>> F_35 = aircraft("f-35", F35Entity::new);
    public static final RegistryObject<EntityType<B2Entity>> B_2 = aircraft("b-2", B2Entity::new);
    public static final RegistryObject<EntityType<F22Entity>> F_22 = aircraft("f-22", F22Entity::new);
    public static final RegistryObject<EntityType<SapsanEntity>> SAPSAN_GRIM2 = tank("sapsan-grim2", SapsanEntity::new);
    public static final RegistryObject<EntityType<F18Entity>> F_18 = aircraft("f-18", F18Entity::new);
    public static final RegistryObject<EntityType<F117Entity>> F_117 = aircraft("f-117", F117Entity::new);
    public static final RegistryObject<EntityType<ZumwaltEntity>> ZUMWALT = ship("zumwalt", ZumwaltEntity::new);
    public static final RegistryObject<EntityType<SU57Entity>> SU_57 = aircraft("su-57", SU57Entity::new);
    public static final RegistryObject<EntityType<V22Entity>> V_22 = aircraft("v-22", V22Entity::new);
    public static final RegistryObject<EntityType<SU27Entity>> SU_27 = aircraft("su-27", SU27Entity::new);
    public static final RegistryObject<EntityType<J20Entity>> J_20 = aircraft("j-20", J20Entity::new);
    public static final RegistryObject<EntityType<F2Entity>> F_2 = aircraft("f_2", F2Entity::new);
    public static final RegistryObject<EntityType<pantsirS1Entity>> PANTSIR_S1 = tank("pa_pantsir", pantsirS1Entity::new);
    public static final RegistryObject<EntityType<EuroFighterEntity>> EuroFighter = aircraft("eurofighter", EuroFighterEntity::new);
    public static final RegistryObject<EntityType<ReaperEntity>> REAPER = aircraft("reaper", ReaperEntity::new);
    public static final RegistryObject<EntityType<YF23Entity>> YF_23 = aircraft("yf-23", YF23Entity::new);
    public static final RegistryObject<EntityType<X47BEntity>> X_47B = aircraft("x-47b", X47BEntity::new);
    public static final RegistryObject<EntityType<RubberBoatEntity>> RUBBER_BOAT = ship("rubber_boat", RubberBoatEntity::new);
    public static final RegistryObject<EntityType<m777Entity>> M_777 = ship("m_777", m777Entity::new);
    public static final RegistryObject<EntityType<Rah66Entity>> RAH_66 = aircraft("rah_66", Rah66Entity::new);
    public static final RegistryObject<EntityType<AH64Entity>> AH_64 = aircraft("ah-64", AH64Entity::new);
}
