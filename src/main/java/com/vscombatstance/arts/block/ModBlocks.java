package com.vscombatstance.arts.block;

import com.vscombatstance.VSCombatStanceMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(VSCombatStanceMod.MODID);

    public static final DeferredBlock<Block> MARTIAL_STAND = BLOCKS.register("martial_stand",
            () -> new MartialStandBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0f, 3.0f)
                    .noOcclusion()));

    public static final DeferredBlock<Block> MARTIAL_STAND_PART = BLOCKS.register("martial_stand_part",
            () -> new MartialStandPartBlock());
}
