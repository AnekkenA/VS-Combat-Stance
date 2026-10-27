package com.vscombatstance.threats.entity;

import com.vscombatstance.threats.CustomThreatsMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VSCombatStanceMod.MODID);

    public static final RegistryObject<EntityType<EliteGuardEntity>> ELITE_GUARD =
            ENTITY_TYPES.register("elite_guard", () ->
                    EntityType.Builder.of(EliteGuardEntity::new, MobCategory.MONSTER)
                            .sized(0.65F, 1.95F)
                            .clientTrackingRange(8)
                            .build("elite_guard"));

    private ModEntities() {}
}
