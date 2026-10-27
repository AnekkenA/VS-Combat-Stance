package com.vscombatstance.arts.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MartialStandBlockEntity extends BlockEntity {
    public MartialStandBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MARTIAL_STAND_BE.get(), pos, state);
    }
}
