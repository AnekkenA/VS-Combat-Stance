package com.vscombatstance.arts.block;

import com.vscombatstance.VSCombatStanceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VSCombatStanceMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MartialStandBlockEntity>> MARTIAL_STAND_BE =
            BLOCK_ENTITIES.register("martial_stand",
                    () -> BlockEntityType.Builder.of(MartialStandBlockEntity::new, ModBlocks.MARTIAL_STAND.get()).build(null));
}
