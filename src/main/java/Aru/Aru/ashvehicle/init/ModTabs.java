package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.AshVehicle;
import com.atsuishio.superbwarfare.item.common.container.ContainerBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AshVehicle.MODID);

    public static final RegistryObject<CreativeModeTab> AIR_TAB = TABS.register("ash-air",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.ashvehicle.ash-air"))
                    .icon(() -> new ItemStack(ModItem.ASHVEHICLE_AIR_ICON.get()))
                    .displayItems((params, output) -> {
                        output.accept(ContainerBlockItem.createInstance(ModEntities.UH_60.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.MH_60M.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_16.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_15.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_4.get()));
                        //output.accept(ContainerBlockItem.createInstance(ModEntities.MIG_15.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.MIG_29.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SU_33.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SU_34.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SU_25.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_39E.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_35B.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_35A.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.B_2.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_22.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_18.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_117.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SU_57.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.V_22.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SU_27.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.J_20.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_2.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.EuroFighter.get()));
                        //output.accept(ContainerBlockItem.createInstance(ModEntities.REAPER.get()));
                        //output.accept(ContainerBlockItem.createInstance(ModEntities.YF_23.get()));
                        //output.accept(ContainerBlockItem.createInstance(ModEntities.X_47B.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.RAH_66.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.AH_64.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.ZELENSKY.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_14.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.KA_52.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.AC_130U.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.B_52.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.C_130.get()));
                    })
                    .build()
    );

    public static final RegistryObject<CreativeModeTab> GROUND_TAB = TABS.register("ash-ground",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.ashvehicle.ash-ground"))
                    .icon(() -> new ItemStack(ModItem.ASHVEHICLE_TANK_ICON.get()))
                    .displayItems((params, output) -> {
                        output.accept(ContainerBlockItem.createInstance(ModEntities.T_90.get()));
                        //output.accept(ContainerBlockItem.createInstance(ModEntities.M1A1_ABRAMS.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.TOS.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.KV_2.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.GEPARD_1A2.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.M3A3_BRADLEY.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SAPSAN_GRIM2.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.PANTSIR_S1.get()));
                    })
                    .build()
    );

    public static final RegistryObject<CreativeModeTab> NAVAL_TAB = TABS.register("ash-naval",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.ashvehicle.ash-naval"))
                    .icon(() -> new ItemStack(ModItem.ASHVEHICLE_SHIP_ICON.get()))
                    .displayItems((params, output) -> {
                        //output.accept(ContainerBlockItem.createInstance(ModEntities.ZUMWALT.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.RUBBER_BOAT.get()));
                        //output.accept(ContainerBlockItem.createInstance(ModEntities.M_777.get()));
                    })
                    .build()
    );

    public static final RegistryObject<CreativeModeTab> ITEMS_TAB = TABS.register("ash-item",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.ashvehicle.ash-item"))
                    .icon(() -> new ItemStack(ModItem.ASHVEHICLE_ITEM_ICON.get()))
                    .displayItems((params, output) -> {
                        ModItem.ITEMS.getEntries().stream()
                                .filter(e -> !e.getId().getPath().contains("ashvehicle"))
                                .forEach(e -> output.accept(e.get()));
                    })
                    .build()
    );
}
