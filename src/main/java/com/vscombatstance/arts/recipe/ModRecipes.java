package com.vscombatstance.arts.recipe;

import com.vscombatstance.VSCombatStanceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, VSCombatStanceMod.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ScrollRerollRecipe>> SCROLL_REROLL =
            RECIPES.register("scroll_reroll", () -> new SimpleCraftingRecipeSerializer<>(ScrollRerollRecipe::new));
}
