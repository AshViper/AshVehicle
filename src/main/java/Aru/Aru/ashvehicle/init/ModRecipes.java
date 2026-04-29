package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.recipe.JerryCanRefillRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AshVehicle.MODID);

    public static final RegistryObject<RecipeSerializer<JerryCanRefillRecipe>> JERRY_CAN_REFILL =
            SERIALIZERS.register("jerry_can_refill", () -> new SimpleCraftingRecipeSerializer<>(JerryCanRefillRecipe::new));
}
