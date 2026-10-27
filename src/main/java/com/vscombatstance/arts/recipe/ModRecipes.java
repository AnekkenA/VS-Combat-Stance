package com.vscombatstance.arts.recipe;

import com.vscombatstance.arts.CustomArtsMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, VSCombatStanceMod.MODID);

    public static final RegistryObject<RecipeSerializer<ScrollRerollRecipe>> SCROLL_REROLL =
            RECIPES.register("scroll_reroll", () -> new SimpleCraftingRecipeSerializer<>(ScrollRerollRecipe::new));
}
