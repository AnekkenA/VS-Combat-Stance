package com.vscombatstance.arts.recipe;

import com.vscombatstance.arts.item.ModItems;
import com.vscombatstance.arts.item.ScrollItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Random;

public class ScrollRerollRecipe extends CustomRecipe {

    public ScrollRerollRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean hasScroll = false;
        boolean hasBookAndQuill = false;
        int totalItems = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                totalItems++;
                if (stack.is(ModItems.SCROLL.get()) && ScrollItem.getTechniqueId(stack) != null) {
                    if (hasScroll) return false;
                    hasScroll = true;
                } else if (stack.is(Items.WRITABLE_BOOK)) {
                    if (hasBookAndQuill) return false;
                    hasBookAndQuill = true;
                } else {
                    return false;
                }
            }
        }

        return totalItems == 2 && hasScroll && hasBookAndQuill;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(ModItems.SCROLL.get()) && ScrollItem.getTechniqueId(stack) != null) {
                ItemStack result = stack.copy();
                result.setCount(1);
                ScrollItem.rerollStats(result, new Random());
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SCROLL_REROLL.get();
    }
}
