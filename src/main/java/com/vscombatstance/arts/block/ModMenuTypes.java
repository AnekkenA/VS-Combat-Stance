package com.vscombatstance.arts.block;

import com.vscombatstance.VSCombatStanceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, VSCombatStanceMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<MartialStandMenu>> MARTIAL_STAND_MENU =
            MENUS.register("martial_stand", () -> IMenuTypeExtension.create(MartialStandMenu::new));
}
