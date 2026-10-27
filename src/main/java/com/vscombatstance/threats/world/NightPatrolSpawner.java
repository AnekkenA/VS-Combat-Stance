package com.vscombatstance.threats.world;

import com.vscombatstance.threats.ai.DynamicShieldGoal;
import com.vscombatstance.threats.ai.HarpoonHookGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

public class NightPatrolSpawner {
    public static final String TAG_IS_ELITE = "customthreats:is_elite";
    public static final String TAG_LAST_PATROL_DAY = "customthreats:last_patrol_day";

    private static final Random RANDOM = new Random();

    public static boolean spawnWarHornWave(ServerLevel level, Player player) {
        BlockPos spawnCenter = findSafeSurfacePos(level, player.blockPosition(), 25, 35);
        if (spawnCenter == null) return false;

        spawnSquad(level, spawnCenter, 3);
        spawnVisualMarker(level, spawnCenter);
        return true;
    }

    public static void checkMidnightPatrol(ServerLevel level) {
        long dayTime = level.getDayTime();
        long timeOfDay = dayTime % 24000;
        long currentDay = dayTime / 24000;

        // Trigger around midnight (18000 ticks)
        if (timeOfDay >= 18000 && timeOfDay < 18020) {
            for (ServerPlayer player : level.players()) {
                if (player.isSpectator() || player.isSleeping()) continue;
                if (!level.canSeeSky(player.blockPosition())) continue;

                long lastDay = player.getPersistentData().getLong(TAG_LAST_PATROL_DAY);
                if (lastDay >= currentDay) continue; // Already spawned tonight

                // 60% chance for a patrol tonight
                if (RANDOM.nextFloat() <= 0.60f) {
                    BlockPos pos = findSafeSurfacePos(level, player.blockPosition(), 45, 60);
                    if (pos != null) {
                        spawnSquad(level, pos, 3);
                        spawnVisualMarker(level, pos);
                        player.getPersistentData().putLong(TAG_LAST_PATROL_DAY, currentDay);

                        // Ominous distant horn audio
                        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                                SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(4).get(), SoundSource.HOSTILE, 1.8f, 0.55f);
                    }
                }
            }
        }
    }

    public static void spawnSquad(ServerLevel level, BlockPos center, int count) {
        // 0. Спавн анимированного элитного стража GeckoLib в качестве командира отряда
        com.vscombatstance.threats.entity.EliteGuardEntity guard = com.vscombatstance.threats.entity.ModEntities.ELITE_GUARD.get().create(level);
        if (guard != null) {
            guard.moveTo(center.getX(), center.getY(), center.getZ(), 0, 0);
            guard.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            guard.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
            guard.addTag(TAG_IS_ELITE);
            level.addFreshEntity(guard);
        }

        // 1. Spawn Shieldbearer Elites
        for (int i = 0; i < count; i++) {
            Zombie zombie = EntityType.ZOMBIE.create(level);
            if (zombie != null) {
                zombie.moveTo(center.getX() + (i * 1.5) - 1.5, center.getY(), center.getZ() + (i % 2), 0, 0);
                zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                zombie.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
                zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
                zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
                zombie.setDropChance(EquipmentSlot.OFFHAND, 0.0f);

                zombie.goalSelector.addGoal(2, new DynamicShieldGoal(zombie));
                zombie.addTag(TAG_IS_ELITE);

                level.addFreshEntity(zombie);
            }
        }

        // 2. Spawn Stray Archer with Harpoon Hook
        Stray stray = EntityType.STRAY.create(level);
        if (stray != null) {
            stray.moveTo(center.getX(), center.getY(), center.getZ() - 2, 0, 0);
            stray.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            stray.goalSelector.addGoal(1, new HarpoonHookGoal(stray));
            stray.addTag(TAG_IS_ELITE);
            level.addFreshEntity(stray);
        }

        // 3. 25% Chance of Cavalry on open plains
        if (RANDOM.nextFloat() <= 0.25f) {
            SkeletonHorse horse = EntityType.SKELETON_HORSE.create(level);
            if (horse != null) {
                horse.moveTo(center.getX() + 2, center.getY(), center.getZ() + 2, 0, 0);
                horse.setTamed(true);
                level.addFreshEntity(horse);
            }
        }
    }

    public static void spawnVisualMarker(ServerLevel level, BlockPos pos) {
        // Column of dark smoke and soul flames
        for (int y = 0; y < 8; y++) {
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + y, pos.getZ() + 0.5,
                    5, 0.3, 0.5, 0.3, 0.02);
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + y * 0.5, pos.getZ() + 0.5,
                    3, 0.2, 0.2, 0.2, 0.01);
        }
    }

    public static BlockPos findSafeSurfacePos(ServerLevel level, BlockPos origin, int minDistance, int maxDistance) {
        double angle = RANDOM.nextDouble() * Math.PI * 2.0;
        int dist = minDistance + RANDOM.nextInt(Math.max(1, maxDistance - minDistance));
        int targetX = origin.getX() + (int) (Math.cos(angle) * dist);
        int targetZ = origin.getZ() + (int) (Math.sin(angle) * dist);

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, targetX, targetZ);
        BlockPos candidate = new BlockPos(targetX, surfaceY, targetZ);

        if (level.canSeeSky(candidate)) {
            return candidate;
        }
        return null;
    }
}
