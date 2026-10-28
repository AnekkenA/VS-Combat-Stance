package com.vscombatstance.threats.ai;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class HarpoonHookGoal extends Goal {
    public static final int STATIONARY_THRESHOLD_TICKS = 200; // 10.0 seconds
    public static final double MAX_STATIONARY_DIST_SQ = 2.25; // 1.5 blocks radius
    public static final double MIN_ELEVATION = 2.0; // 2+ blocks above mob

    private final Monster mob;
    private Vec3 lastTrackedPos = null;
    private int stationaryTicks = 0;
    private int cooldown = 0;

    public HarpoonHookGoal(Monster mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target instanceof Player && target.isAlive();
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        LivingEntity target = mob.getTarget();
        if (!(target instanceof Player player)) {
            resetTracking();
            return;
        }

        double deltaY = player.getY() - mob.getY();

        if (deltaY >= MIN_ELEVATION) {
            Vec3 currentPos = player.position();
            if (lastTrackedPos == null) {
                lastTrackedPos = currentPos;
                stationaryTicks = 0;
            } else {
                double movedDistSq = currentPos.distanceToSqr(lastTrackedPos);
                if (movedDistSq <= MAX_STATIONARY_DIST_SQ) {
                    stationaryTicks++;
                    if (stationaryTicks >= STATIONARY_THRESHOLD_TICKS) {
                        executeHarpoonHook(player);
                        cooldown = 100; // 5 seconds cooldown
                        resetTracking();
                    }
                } else {
                    // Player moved or jumped beyond 1.5 blocks -> reset timer
                    lastTrackedPos = currentPos;
                    stationaryTicks = 0;
                }
            }
        } else {
            resetTracking();
        }
    }

    private void executeHarpoonHook(Player player) {
        if (mob instanceof com.vscombatstance.threats.entity.EliteGuardEntity guard) {
            guard.triggerHarpoonAnimation();
        }

        Vec3 mobPos = mob.position();
        Vec3 playerPos = player.position();

        // Calculate pull velocity towards the mob
        Vec3 diff = mobPos.subtract(playerPos);
        Vec3 pull = diff.normalize().scale(1.25).add(0, 0.2, 0);
        player.setDeltaMovement(pull);
        player.hasImpulse = true;

        // Visual chain line particles
        if (mob.level() instanceof ServerLevel serverLevel) {
            int steps = 15;
            for (int i = 0; i <= steps; i++) {
                double progress = (double) i / steps;
                Vec3 p = playerPos.add(diff.scale(progress)).add(0, 0.8, 0);
                serverLevel.sendParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 2, 0.05, 0.05, 0.05, 0.0);
            }
        }

        // Sound effects
        mob.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CHAIN_PLACE, SoundSource.HOSTILE, 1.5f, 0.8f);
        mob.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.HOSTILE, 1.2f, 1.2f);

        player.displayClientMessage(Component.translatable("message.customthreats.harpoon_hooked"), true);
    }

    private void resetTracking() {
        lastTrackedPos = null;
        stationaryTicks = 0;
    }

    public int getStationaryTicks() {
        return stationaryTicks;
    }
}
