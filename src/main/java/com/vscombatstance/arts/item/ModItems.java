package com.vscombatstance.arts.item;

import com.vscombatstance.VSCombatStanceMod;
import com.vscombatstance.arts.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(VSCombatStanceMod.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VSCombatStanceMod.MODID);

    public static final DeferredItem<Item> SCROLL = ITEMS.register("scroll",
            () -> new ScrollItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> MARTIAL_STAND = ITEMS.register("martial_stand",
            () -> new BlockItem(ModBlocks.MARTIAL_STAND.get(), new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("customarts_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.customarts"))
                    .icon(() -> new ItemStack(SCROLL.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(MARTIAL_STAND.get());
                        // Generate an example of every technique for the creative tab
                        var rand = new java.util.Random(42);
                        com.vscombatstance.arts.technique.TechniqueRegistry.getAll().forEach(art ->
                                output.accept(ScrollItem.createScroll(art, rand)));
                        com.vscombatstance.arts.technique.LinkerRegistry.getAll().forEach(linker ->
                                output.accept(ScrollItem.createScroll(linker, rand)));
                    })
                    .build());
}
