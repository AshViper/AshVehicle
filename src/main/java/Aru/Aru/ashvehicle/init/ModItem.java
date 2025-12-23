package Aru.Aru.ashvehicle.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItem {
    // ItemのDeferredRegister
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "ashvehicle");

    // 効果なしのシンプルなアイテム
    public static final RegistryObject<Item> ASHVEHICLE_ITEM_ICON = ITEMS.register("ashvehicle-item",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ASHVEHICLE_AIR_ICON = ITEMS.register("ashvehicle-air-item",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ASHVEHICLE_TANK_ICON = ITEMS.register("ashvehicle-tank-item",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ASHVEHICLE_SHIP_ICON = ITEMS.register("ashvehicle-ship-item",
            () -> new Item(new Item.Properties()));
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
    public static final RegistryObject<Item> NUCLEARBOMB = ITEMS.register("nuclearbombitem",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final RegistryObject<Item> JETENGINE = ITEMS.register("jetengineitem",
            () -> new Item(new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> ENGINEFAN = ITEMS.register("enginefanitem",
            () -> new Item(new Item.Properties().stacksTo(8)));

}
