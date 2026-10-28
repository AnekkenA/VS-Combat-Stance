package com.vscombatstance.threats.entity;

import com.vscombatstance.VSCombatStanceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, VSCombatStanceMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<EliteGuardEntity>> ELITE_GUARD =
            ENTITY_TYPES.register("elite_guard", () ->
                    EntityType.Builder.of(EliteGuardEntity::new, MobCategory.MONSTER)
                            .sized(0.65F, 1.95F)
                            .clientTrackingRange(8)
                            .build("elite_guard"));

    private ModEntities() {}
}
