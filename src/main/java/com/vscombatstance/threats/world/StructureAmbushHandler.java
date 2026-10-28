package com.vscombatstance.threats.world;

import com.vscombatstance.threats.ai.DynamicShieldGoal;
import com.vscombatstance.threats.ai.LeashTerritoryGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.vscombatstance.VSCombatStanceMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = VSCombatStanceMod.MODID)
public class StructureAmbushHandler {
    private static final Set<BlockPos> TRIGGERED_CHESTS = new HashSet<>();

    @SubscribeEvent
    public static void onChestOpen(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        BlockPos pos = event.getPos();
        BlockState state = serverLevel.getBlockState(pos);

        if (state.is(Blocks.CHEST) || state.is(Blocks.TRAPPED_CHEST)) {
            if (TRIGGERED_CHESTS.contains(pos)) return;

            // Check if player is near a structure or underground vault
            // For safety and immersion, 35% chance to trigger an ambush on unopened chests
            if (serverLevel.random.nextFloat() <= 0.35f) {
                TRIGGERED_CHESTS.add(pos);
                spawnAmbushGuards(serverLevel, pos, event.getEntity());
            }
        }
    }

    private static void spawnAmbushGuards(ServerLevel level, BlockPos origin, Player player) {
        int count = 2 + level.random.nextInt(2); // 2 to 3 guards

        for (int i = 0; i < count; i++) {
            Zombie guard = EntityType.ZOMBIE.create(level);
            if (guard != null) {
                double ox = origin.getX() + (i == 0 ? 2 : i == 1 ? -2 : 0);
                double oz = origin.getZ() + (i == 2 ? 2 : 0);
                guard.moveTo(ox + 0.5, origin.getY(), oz + 0.5, 0, 0);

                guard.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
                guard.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
                guard.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
                guard.setDropChance(EquipmentSlot.OFFHAND, 0.0f);

                // Add goals: Dynamic shield & territorial leash
                guard.goalSelector.addGoal(2, new DynamicShieldGoal(guard));
                guard.goalSelector.addGoal(1, new LeashTerritoryGoal(guard, origin));
                guard.addTag(NightPatrolSpawner.TAG_IS_ELITE);

                guard.setTarget(player);
                level.addFreshEntity(guard);
            }
        }

        // Sinister alert chime
        level.playSound(null, origin.getX(), origin.getY(), origin.getZ(),
                SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.2f, 0.6f);
    }
}
