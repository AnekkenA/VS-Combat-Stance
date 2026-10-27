package com.vscombatstance.arts.client;

import com.vscombatstance.arts.CustomArtsMod;
import com.vscombatstance.arts.block.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;

@Mod.EventBusSubscriber(modid = VSCombatStanceMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.MARTIAL_STAND_MENU.get(), MartialStandScreen::new);
            // Инициализация слоя анимаций playerAnimator для приемов боевых искусств
            com.vscombatstance.arts.client.animator.PlayerAnimationManager.init();
        });
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "combat_hud", new CombatHudOverlay());
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KeyInputHandler.registerKeys(event);
    }
}
