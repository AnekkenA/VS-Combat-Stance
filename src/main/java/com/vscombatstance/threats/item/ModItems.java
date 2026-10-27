package com.vscombatstance.threats.item;

import com.vscombatstance.threats.CustomThreatsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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

    public static final RegistryObject<Item> WAR_HORN = ITEMS.register("war_horn",
            () -> new WarHornItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("customthreats_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.customthreats"))
                    .icon(() -> new ItemStack(WAR_HORN.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(WAR_HORN.get());
                    })
                    .build());
}
