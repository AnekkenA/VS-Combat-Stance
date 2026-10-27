package com.vscombatstance.threats.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;

import java.util.EnumSet;

public class DynamicShieldGoal extends Goal {
    public static final int BLOCK_DURATION = 50; // 2.5 seconds block
    public static final int VULNERABLE_DURATION = 24; // 1.2 seconds opening
    public static final int DISABLE_DURATION = 100; // 5.0 seconds cooldown when broken

    private final Monster mob;
    private int blockTimer = 0;
    private int vulnerableTimer = 0;
    private int disabledTimer = 0;
    private boolean isBlocking = false;

    public DynamicShieldGoal(Monster mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;
        ItemStack offhand = mob.getOffhandItem();
        return (offhand.getItem() instanceof ShieldItem || offhand.is(Items.SHIELD));
    }

    @Override
    public void start() {
        this.blockTimer = BLOCK_DURATION;
        this.vulnerableTimer = 0;
        this.isBlocking = false;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            stopBlocking();
            return;
        }

        // Handle disabled timer (e.g. from axe or heavy hammer blow)
        if (disabledTimer > 0) {
            disabledTimer--;
            stopBlocking();
            return;
        }

        double distanceSq = mob.distanceToSqr(target);

        // Only manage shield when within combat engagement distance (2.5m to 12m)
        if (distanceSq >= 6.0 && distanceSq <= 144.0) {
            if (blockTimer > 0) {
                blockTimer--;
                startBlocking();
            } else if (vulnerableTimer > 0) {
                vulnerableTimer--;
                stopBlocking();
            } else {
                // Cycle back to blocking phase
                blockTimer = BLOCK_DURATION;
                vulnerableTimer = VULNERABLE_DURATION;
            }
        } else {
            // Drop shield when very close to execute attacks
            stopBlocking();
        }
    }

    private void startBlocking() {
        if (!isBlocking && disabledTimer <= 0) {
            mob.startUsingItem(InteractionHand.OFF_HAND);
            isBlocking = true;
            if (mob instanceof com.vscombatstance.threats.entity.EliteGuardEntity guard) {
                guard.triggerShieldBlockAnimation();
            }
        }
    }

    private void stopBlocking() {
        if (isBlocking) {
            mob.stopUsingItem();
            isBlocking = false;
        }
    }

    public void disableShield(int ticks) {
        this.disabledTimer = ticks;
        stopBlocking();
        mob.level().playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                SoundEvents.SHIELD_BREAK, SoundSource.HOSTILE, 1.2f, 0.8f);
    }

    public boolean isShieldDisabled() {
        return disabledTimer > 0;
    }

    public boolean isCurrentlyBlocking() {
        return isBlocking;
    }
}
