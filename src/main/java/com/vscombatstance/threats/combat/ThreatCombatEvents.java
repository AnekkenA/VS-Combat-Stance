package com.vscombatstance.threats.combat;

import com.vscombatstance.threats.ai.DynamicShieldGoal;
import com.vscombatstance.threats.world.NightPatrolSpawner;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ThreatCombatEvents {
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(victim instanceof Monster mob)) return;
        if (!mob.getTags().contains(NightPatrolSpawner.TAG_IS_ELITE)) return;

        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();

            // Check if mob is using dynamic shield
            for (WrappedGoal wrapped : mob.goalSelector.getAvailableGoals()) {
                if (wrapped.getGoal() instanceof DynamicShieldGoal shieldGoal && shieldGoal.isCurrentlyBlocking()) {
                    boolean shouldBreakShield = false;

                    // 1. Vanilla Axe break
                    if (weapon.getItem() instanceof AxeItem) {
                        shouldBreakShield = true;
                    }

                    // 2. CustomSmith Heavy Weapon break (Weight >= 350)
                    double weight = com.vscombatstance.arts.adapter.WeightAdapter.getEffectiveWeight(weapon);
                    if (weight >= 350.0) {
                        shouldBreakShield = true;
                    }

                    if (shouldBreakShield) {
                        shieldGoal.disableShield(100); // Disable for 5.0 seconds
                        if (mob.level() instanceof ServerLevel sl) {
                            sl.sendParticles(ParticleTypes.CRIT, mob.getX(), mob.getY() + 1.2, mob.getZ(),
                                    15, 0.4, 0.4, 0.4, 0.1);
                        }
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.getTags().contains(NightPatrolSpawner.TAG_IS_ELITE)) return;

        int looting = Math.min(3, event.getLootingLevel());
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        // 1. Netherite Scrap: 4% base + (looting * 1%) -> 4% to 7%
        float scrapChance = 0.04f + (looting * 0.01f);
        if (RANDOM.nextFloat() <= scrapChance) {
            event.getDrops().add(new ItemEntity(entity.level(), x, y, z, new ItemStack(Items.NETHERITE_SCRAP)));
        }

        // 2. Diamonds: 8% base + (looting * 2%) -> 8% to 14%
        float diamondChance = 0.08f + (looting * 0.02f);
        if (RANDOM.nextFloat() <= diamondChance) {
            event.getDrops().add(new ItemEntity(entity.level(), x, y, z, new ItemStack(Items.DIAMOND, 1 + (looting > 1 ? RANDOM.nextInt(2) : 0))));
        }

        // 3. Smithing Templates: 3% base + (looting * 0.5%) -> 3% to 4.5%
        float templateChance = 0.03f + (looting * 0.005f);
        if (RANDOM.nextFloat() <= templateChance) {
            ItemStack template = getRandomSmithingTemplate();
            event.getDrops().add(new ItemEntity(entity.level(), x, y, z, template));
        }

        // 4. Guaranteed combat supplies: Arrows of Slowness, Iron Ingots
        event.getDrops().add(new ItemEntity(entity.level(), x, y, z, new ItemStack(Items.TIPPED_ARROW, 2 + RANDOM.nextInt(3))));
        if (RANDOM.nextFloat() <= 0.35f) {
            event.getDrops().add(new ItemEntity(entity.level(), x, y, z, new ItemStack(Items.IRON_INGOT, 1 + RANDOM.nextInt(2))));
        }
    }

    private static ItemStack getRandomSmithingTemplate() {
        ItemStack[] templates = new ItemStack[] {
                new ItemStack(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE),
                new ItemStack(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE),
                new ItemStack(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE),
                new ItemStack(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE),
                new ItemStack(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE),
                new ItemStack(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE),
                new ItemStack(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE)
        };
        return templates[RANDOM.nextInt(templates.length)];
    }
}
