package com.vscombatstance;

import com.vscombatstance.arts.block.ModBlockEntities;
import com.vscombatstance.arts.block.ModBlocks;
import com.vscombatstance.arts.block.ModMenuTypes;
import com.vscombatstance.arts.combat.CombatStyleCapability;
import com.vscombatstance.arts.combat.ComboEngine;
import com.vscombatstance.arts.item.ModItems;
import com.vscombatstance.arts.network.ModNetwork;
import com.vscombatstance.arts.recipe.ModRecipes;
import com.vscombatstance.threats.entity.EliteGuardEntity;
import com.vscombatstance.threats.entity.ModEntities;
import com.vscombatstance.threats.world.NightPatrolSpawner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

/**
 * VS: Combat Stance
 * Официальное боевое дополнение для мода Voxel Stand:
 * 1. Боевые искусства, комбо-цепочки, свитки техник и стойки с анимациями (playerAnimator).
 * 2. Ночные карательные патрули разбойников, гарпуны, тактические щиты и боссы (GeckoLib 4).
 *
 * ТРЕБУЕТ установленный базовый мод Voxel Stand (voxelstand).
 */
@Mod(VSCombatStanceMod.MODID)
public class VSCombatStanceMod {
    public static final String MODID = "vs_combatstance";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public VSCombatStanceMod(IEventBus modEventBus) {
        // Инициализация GeckoLib для скелетных анимаций мобов
        GeckoLib.initialize();

        // 1. Регистрация контента боевых искусств (Arts)
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModItems.CREATIVE_MODE_TABS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModRecipes.RECIPES.register(modEventBus);

        // 2. Регистрация угроз и мобов (Threats)
        ModEntities.ENTITY_TYPES.register(modEventBus);
        com.vscombatstance.threats.item.ModItems.ITEMS.register(modEventBus);
        com.vscombatstance.threats.item.ModItems.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::entityAttributeEvent);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new CombatEvents());
        NeoForge.EVENT_BUS.register(new com.vscombatstance.threats.combat.ThreatCombatEvents());

        LOGGER.info("VS: Combat Stance (Arts + Threats) initialized successfully for Voxel Stand!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }

    private void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ELITE_GUARD.get(), EliteGuardEntity.createAttributes().build());
    }

    public static class CombatEvents {
        @SubscribeEvent
        public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                event.addCapability(new ResourceLocation(MODID, "combat_styles"), new CombatStyleCapability());
            }
        }

        @SubscribeEvent
        public void onPlayerClone(PlayerEvent.Clone event) {
            event.getOriginal().getCapability(CombatStyleCapability.INSTANCE).ifPresent(oldCap -> {
                event.getEntity().getCapability(CombatStyleCapability.INSTANCE).ifPresent(newCap -> {
                    newCap.deserializeNBT(oldCap.serializeNBT());
                });
            });
        }

        @SubscribeEvent
        public void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
                ComboEngine.serverTick(event.player);
            }
        }

        @SubscribeEvent
        public void onLevelTick(TickEvent.LevelTickEvent event) {
            if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel serverLevel) {
                NightPatrolSpawner.checkMidnightPatrol(serverLevel);
            }
        }
    }
}
