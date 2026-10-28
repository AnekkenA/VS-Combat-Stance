package com.vscombatstance.threats.client;

import com.vscombatstance.VSCombatStanceMod;
import com.vscombatstance.threats.client.renderer.EliteGuardRenderer;
import com.vscombatstance.threats.entity.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = VSCombatStanceMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ELITE_GUARD.get(), EliteGuardRenderer::new);
    }
}
