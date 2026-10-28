package com.vscombatstance.threats.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;

import java.util.EnumSet;

public class LeashTerritoryGoal extends Goal {
    public static final double MAX_LEASH_DIST = 18.0;
    public static final double MAX_LEASH_DIST_SQ = MAX_LEASH_DIST * MAX_LEASH_DIST; // 324.0

    private final Monster mob;
    private final BlockPos origin;

    public LeashTerritoryGoal(Monster mob, BlockPos origin) {
        this.mob = mob;
        this.origin = origin;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public LeashTerritoryGoal(Monster mob, double maxDistance) {
        this(mob, mob.blockPosition());
    }

    @Override
    public boolean canUse() {
        return mob.blockPosition().distSqr(origin) > MAX_LEASH_DIST_SQ;
    }

    @Override
    public void start() {
        // Break aggro when pulled too far from home base
        mob.setTarget(null);
        mob.getNavigation().moveTo(origin.getX() + 0.5, origin.getY(), origin.getZ() + 0.5, 1.25);
    }

    @Override
    public boolean canContinueToUse() {
        return mob.blockPosition().distSqr(origin) > 9.0 && !mob.getNavigation().isDone();
    }

    public BlockPos getOrigin() {
        return origin;
    }
}
