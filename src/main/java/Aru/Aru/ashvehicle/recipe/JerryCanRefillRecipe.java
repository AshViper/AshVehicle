package Aru.Aru.ashvehicle.recipe;

import Aru.Aru.ashvehicle.init.ModItem;
import Aru.Aru.ashvehicle.init.ModRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class JerryCanRefillRecipe extends CustomRecipe {
    public JerryCanRefillRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        boolean hasJerryCan = false;
        boolean hasCoal = false;
        int count = 0;

        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack stack = pContainer.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(ModItem.JERRY_CAN.get())) {
                    if (hasJerryCan) return false;
                    hasJerryCan = true;
                } else if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) {
                    hasCoal = true;
                } else {
                    return false;
                }
                count++;
            }
        }

        return hasJerryCan && hasCoal;
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        ItemStack jerryCan = ItemStack.EMPTY;
        int coalCount = 0;

        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack stack = pContainer.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(ModItem.JERRY_CAN.get())) {
                    jerryCan = stack.copy();
                } else if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) {
                    coalCount++;
                }
            }
        }

        if (!jerryCan.isEmpty()) {
            // 石炭1つにつき1000000燃料（耐久値）を回復
            int currentDamage = jerryCan.getDamageValue();
            int refillAmount = coalCount * 100000;
            jerryCan.setDamageValue(Math.max(0, currentDamage - refillAmount));
        }

        return jerryCan;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.JERRY_CAN_REFILL.get();
    }
}
