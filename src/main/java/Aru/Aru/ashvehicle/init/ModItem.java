package Aru.Aru.ashvehicle.init;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModItem {
    // ItemのDeferredRegister
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, "ashvehicle");

    // 効果なしのシンプルなアイテム
    public static final DeferredHolder<Item, Item> ASHVEHICLE_ITEM_ICON = ITEMS.register("ashvehicle-item",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ASHVEHICLE_AIR_ICON = ITEMS.register("ashvehicle-air-item",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ASHVEHICLE_TANK_ICON = ITEMS.register("ashvehicle-tank-item",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ASHVEHICLE_SHIP_ICON = ITEMS.register("ashvehicle-ship-item",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> JASSM = ITEMS.register("agm158item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final DeferredHolder<Item, Item> GBU57 = ITEMS.register("gbu57item",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CBU87 = ITEMS.register("cbu87item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final DeferredHolder<Item, Item> AIM9 = ITEMS.register("aim9item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final DeferredHolder<Item, Item> AIM120 = ITEMS.register("aim120item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final DeferredHolder<Item, Item> AIM54 = ITEMS.register("aim54item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final DeferredHolder<Item, Item> R60 = ITEMS.register("r60item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final DeferredHolder<Item, Item> AGM114 = ITEMS.register("agm114item",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final DeferredHolder<Item, Item> NUCLEARBOMB = ITEMS.register("nuclearbombitem",
            () -> new Item(new Item.Properties().stacksTo(4)));
    public static final DeferredHolder<Item, Item> JETENGINE = ITEMS.register("jetengineitem",
            () -> new Item(new Item.Properties().stacksTo(2)));
    public static final DeferredHolder<Item, Item> ENGINEFAN = ITEMS.register("enginefanitem",
            () -> new Item(new Item.Properties().stacksTo(8)));

}
