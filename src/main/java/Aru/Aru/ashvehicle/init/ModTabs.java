
package Aru.Aru.ashvehicle.init;

import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.common.container.ContainerBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import Aru.Aru.ashvehicle.AshVehicle;

@SuppressWarnings("unused")
public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AshVehicle.MODID);

    public static final RegistryObject<CreativeModeTab> BLOCKTANK_TAB = TABS.register("ash-tank",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.ashvehicle.ash-tank"))
                    .icon(() -> new ItemStack(ModItems.CONTAINER.get()))
                    .displayItems((param, output) -> {
                        output.accept(ContainerBlockItem.createInstance(ModEntities.T_90.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.M1A1_ABRAMS.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.KV_2.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.GEPARD_1A2.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.M3A3_BRADLEY.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SAPSAN_GRIM2.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.ZYNWALT.get()));
                    })
                    .build()
    );
    public static final RegistryObject<CreativeModeTab> BLOCKAIR_TAB = TABS.register("ash-air",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.ashvehicle.ash-air"))
                    .icon(() -> new ItemStack(ModItems.CONTAINER.get()))
                    .displayItems((param, output) -> {
                        output.accept(ContainerBlockItem.createInstance(ModEntities.UH_60.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.MH_60M.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.MIG_29.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_4.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_16.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SU_33.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SU_25.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_39E.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SU_34.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_35.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.B_2.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_22.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_18.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.F_117.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SU_57.get()));
                    })
                    .build()
    );
}
