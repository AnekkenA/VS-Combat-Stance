package com.vscombatstance.arts.item;

import com.vscombatstance.arts.CustomArtsMod;
import com.vscombatstance.arts.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, VSCombatStanceMod.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VSCombatStanceMod.MODID);

    public static final RegistryObject<Item> SCROLL = ITEMS.register("scroll",
            () -> new ScrollItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> MARTIAL_STAND = ITEMS.register("martial_stand",
            () -> new BlockItem(ModBlocks.MARTIAL_STAND.get(), new Item.Properties().stacksTo(1)));

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("customarts_tab",
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
