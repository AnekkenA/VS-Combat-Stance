package com.vscombatstance;

import com.vscombatstance.arts.block.ModBlockEntities;
import com.vscombatstance.arts.block.ModBlocks;
import com.vscombatstance.arts.block.ModMenuTypes;
import com.vscombatstance.arts.combat.CombatStyleCapability;
import com.vscombatstance.arts.combat.ComboEngine;
import com.vscombatstance.arts.item.ModItems;
import com.vscombatstance.arts.recipe.ModRecipes;
import com.vscombatstance.threats.entity.EliteGuardEntity;
import com.vscombatstance.threats.entity.ModEntities;
import com.vscombatstance.threats.world.NightPatrolSpawner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.function.Supplier;

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

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);

    public static final Supplier<AttachmentType<CombatStyleCapability>> COMBAT_STYLES =
            ATTACHMENT_TYPES.register("combat_styles", () -> AttachmentType.serializable(CombatStyleCapability::new).copyOnDeath().build());

    public VSCombatStanceMod(IEventBus modEventBus) {
        // Регистрация Attachment Types (Combat Styles)
        ATTACHMENT_TYPES.register(modEventBus);

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

        modEventBus.addListener(this::entityAttributeEvent);

        NeoForge.EVENT_BUS.register(new CombatEvents());
        NeoForge.EVENT_BUS.register(new com.vscombatstance.threats.combat.ThreatCombatEvents());

        LOGGER.info("VS: Combat Stance (Arts + Threats) initialized successfully for Voxel Stand!");
    }

    private void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ELITE_GUARD.get(), EliteGuardEntity.createAttributes().build());
    }

    public static class CombatEvents {
        @SubscribeEvent
        public void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();
            if (player != null && !player.level().isClientSide) {
                ComboEngine.tickPlayer(player);
            }
        }

        @SubscribeEvent
        public void onLevelTick(LevelTickEvent.Post event) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                NightPatrolSpawner.checkMidnightPatrol(serverLevel);
            }
        }
    }
}
