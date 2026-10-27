package com.vscombatstance.threats.client;

import com.vscombatstance.threats.CustomThreatsMod;
import com.vscombatstance.threats.client.renderer.EliteGuardRenderer;
import com.vscombatstance.threats.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VSCombatStanceMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ELITE_GUARD.get(), EliteGuardRenderer::new);
    }
}
