package com.vscombatstance.arts.technique;

import net.minecraft.resources.ResourceLocation;
import java.util.*;

public class LinkerRegistry {
    private static final Map<String, Technique> LINKERS = new LinkedHashMap<>();

    public static final Technique VAULT_LEAP = register(new Technique.Builder("vault_leap", TechniqueType.LINKER)
            .source(new ResourceLocation("minecraft", "phantom_membrane"))
            .actionKey(Technique.ActionKey.SPACE)
            .timing(12, 4, 10)
            .multiplier(1.15)
            .aoe(0.0, 0.0)
            .massScaling(0.0)
            .consecutiveStackable(true)
            .build());

    public static final Technique DISENGAGE = register(new Technique.Builder("disengage", TechniqueType.LINKER)
            .source(new ResourceLocation("minecraft", "rabbit_foot"))
            .actionKey(Technique.ActionKey.SPACE)
            .timing(12, 4, 10)
            .multiplier(1.12)
            .aoe(0.0, 0.0)
            .massScaling(0.0)
            .consecutiveStackable(true)
            .build());

    public static final Technique SHADOW_STEP = register(new Technique.Builder("shadow_step", TechniqueType.LINKER)
            .source(new ResourceLocation("minecraft", "ender_eye"))
            .actionKey(Technique.ActionKey.RMB)
            .timing(10, 3, 8)
            .multiplier(1.20)
            .aoe(0.0, 0.0)
            .massScaling(0.0)
            .consecutiveStackable(true)
            .build());

    public static final Technique BATTLE_CRY = register(new Technique.Builder("battle_cry", TechniqueType.LINKER)
            .source(new ResourceLocation("minecraft", "goat_horn"))
            .actionKey(Technique.ActionKey.RMB)
            .timing(14, 5, 11)
            .multiplier(1.18)
            .aoe(5.0, 360.0)
            .massScaling(0.0)
            .consecutiveStackable(true)
            .build());

    public static final Technique DELAYED_FEINT = register(new Technique.Builder("delayed_feint", TechniqueType.LINKER)
            .source(new ResourceLocation("minecraft", "clock"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(16, 7, 13)
            .multiplier(1.15)
            .aoe(2.5, 90.0)
            .massScaling(0.0)
            .consecutiveStackable(true)
            .build());

    public static final Technique DEFLECT_PARRY = register(new Technique.Builder("deflect_parry", TechniqueType.LINKER)
            .source(new ResourceLocation("minecraft", "amethyst_shard"))
            .actionKey(Technique.ActionKey.RMB)
            .timing(12, 3, 9)
            .multiplier(1.22)
            .aoe(2.0, 180.0)
            .massScaling(0.0)
            .consecutiveStackable(true)
            .build());

    private static Technique register(Technique technique) {
        LINKERS.put(technique.getId(), technique);
        return technique;
    }

    public static Collection<Technique> getAll() {
        return Collections.unmodifiableCollection(LINKERS.values());
    }

    public static Technique get(String id) {
        return LINKERS.get(id);
    }

    public static Optional<Technique> getByIngredient(ResourceLocation ingredientLoc) {
        return LINKERS.values().stream()
                .filter(t -> t.getCraftSourceItem() != null && t.getCraftSourceItem().equals(ingredientLoc))
                .findFirst();
    }
}
