package com.vscombatstance.threats.item;

import com.vscombatstance.VSCombatStanceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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

    public static final DeferredItem<Item> WAR_HORN = ITEMS.register("war_horn",
            () -> new WarHornItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("customthreats_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.customthreats"))
                    .icon(() -> new ItemStack(WAR_HORN.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(WAR_HORN.get());
                    })
                    .build());
}
