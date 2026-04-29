package Aru.Aru.ashvehicle.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import Aru.Aru.ashvehicle.AshVehicle;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AshVehicle.MODID);

    // Icons
    public static final RegistryObject<Item> ASHVEHICLE_ITEM_ICON = ITEMS.register("ashvehicle-item",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ASHVEHICLE_AIR_ICON = ITEMS.register("ashvehicle-air-item",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ASHVEHICLE_TANK_ICON = ITEMS.register("ashvehicle-tank-item",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ASHVEHICLE_SHIP_ICON = ITEMS.register("ashvehicle-ship-item",
            () -> new Item(new Item.Properties()));

    // Projectile Items
    public static final RegistryObject<Item> JASSM = ITEMS.register("agm158item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final RegistryObject<Item> GBU57 = ITEMS.register("gbu57item",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CBU87 = ITEMS.register("cbu87item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final RegistryObject<Item> AIM9 = ITEMS.register("aim9item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final RegistryObject<Item> AIM120 = ITEMS.register("aim120item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final RegistryObject<Item> AIM54 = ITEMS.register("aim54item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final RegistryObject<Item> R60 = ITEMS.register("r60item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final RegistryObject<Item> AGM114 = ITEMS.register("agm114item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final RegistryObject<Item> NUCLEARBOMB = ITEMS.register("nuclearbombitem",
            () -> new Item(new Item.Properties().stacksTo(1)));

    // Parts
    public static final RegistryObject<Item> JETENGINE = ITEMS.register("jetengineitem",
            () -> new Item(new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> ENGINEFAN = ITEMS.register("enginefanitem",
            () -> new Item(new Item.Properties().stacksTo(8)));

    // Ammo
    public static final RegistryObject<Item> AMMO_40MM = ITEMS.register("40mmitem",
            () -> new Item(new Item.Properties().stacksTo(32)));
    public static final RegistryObject<Item> AMMO_105MM = ITEMS.register("105mmitem",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> AMMO_20MM = ITEMS.register("20mmitem",
            () -> new Item(new Item.Properties().stacksTo(64)));

    // Custom Items
    public static final RegistryObject<Item> JERRY_CAN = ITEMS.register("jerry_can",
            () -> new Aru.Aru.ashvehicle.item.JerryCanItem(new Item.Properties().stacksTo(1)));
}
