package com.vscombatstance.arts.block;

import com.vscombatstance.arts.CustomArtsMod;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, VSCombatStanceMod.MODID);

    public static final RegistryObject<MenuType<MartialStandMenu>> MARTIAL_STAND_MENU =
            MENUS.register("martial_stand", () -> IMenuTypeExtension.create(MartialStandMenu::new));
}
