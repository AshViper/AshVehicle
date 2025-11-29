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
    public static final RegistryObject<Item> AAM4 = ITEMS.register("aam_4_item",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> JASSM = ITEMS.register("jassm_item",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GBU57 = ITEMS.register("gbu-57-item",
            () -> new Item(new Item.Properties()));
}
