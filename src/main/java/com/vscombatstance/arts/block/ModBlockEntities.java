package com.vscombatstance.arts.block;

import com.vscombatstance.arts.CustomArtsMod;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VSCombatStanceMod.MODID);

    public static final RegistryObject<BlockEntityType<MartialStandBlockEntity>> MARTIAL_STAND_BE =
            BLOCK_ENTITIES.register("martial_stand",
                    () -> BlockEntityType.Builder.of(MartialStandBlockEntity::new, ModBlocks.MARTIAL_STAND.get()).build(null));
}
