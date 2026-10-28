package com.vscombatstance.arts.technique;

import net.minecraft.resources.ResourceLocation;
import java.util.*;

public class TechniqueRegistry {
    private static final Map<String, Technique> ARTS = new LinkedHashMap<>();

    public static final Technique OVERHEAD_SLAM = register(new Technique.Builder("overhead_slam", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "rib_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(18, 9, 15)
            .multiplier(1.25)
            .aoe(3.5, 120.0)
            .massScaling(1.0)
            .build());

    public static final Technique WHIRLWIND = register(new Technique.Builder("whirlwind", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "snout_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(20, 10, 17)
            .multiplier(1.20)
            .aoe(4.0, 360.0)
            .massScaling(0.8)
            .build());

    public static final Technique FLURRY = register(new Technique.Builder("flurry", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "wild_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(14, 6, 12)
            .multiplier(1.15)
            .aoe(2.5, 60.0)
            .massScaling(0.2)
            .build());

    public static final Technique CRESCENT_CLEAVE = register(new Technique.Builder("crescent_cleave", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "coast_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(16, 7, 13)
            .multiplier(1.18)
            .aoe(3.2, 140.0)
            .massScaling(0.5)
            .build());

    public static final Technique SHIELD_BASH = register(new Technique.Builder("shield_bash", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "sentry_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.RMB)
            .timing(15, 6, 12)
            .multiplier(1.15)
            .aoe(2.8, 90.0)
            .massScaling(0.7)
            .build());

    public static final Technique FORTRESS_BASTION = register(new Technique.Builder("fortress_bastion", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "eye_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.RMB)
            .timing(22, 5, 18)
            .multiplier(1.10)
            .aoe(2.0, 180.0)
            .massScaling(0.3)
            .build());

    public static final Technique PIERCING_LUNGE = register(new Technique.Builder("piercing_lunge", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "spire_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(14, 6, 11)
            .multiplier(1.22)
            .aoe(3.8, 30.0)
            .massScaling(0.3)
            .build());

    public static final Technique SHADOW_SEVER = register(new Technique.Builder("shadow_sever", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "silence_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(16, 8, 14)
            .multiplier(1.25)
            .aoe(2.5, 90.0)
            .massScaling(0.4)
            .build());

    public static final Technique VOLLEY_SPREAD = register(new Technique.Builder("volley_spread", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "dune_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.RMB)
            .timing(18, 8, 15)
            .multiplier(1.15)
            .aoe(12.0, 45.0)
            .massScaling(0.0)
            .build());

    public static final Technique SNIPER_PIERCING = register(new Technique.Builder("sniper_piercing", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "ward_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.RMB)
            .timing(24, 14, 21)
            .multiplier(1.25)
            .aoe(25.0, 10.0)
            .massScaling(0.0)
            .build());

    public static final Technique RISING_UPPERCUT = register(new Technique.Builder("rising_uppercut", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "tide_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(15, 6, 12)
            .multiplier(1.20)
            .aoe(2.5, 80.0)
            .massScaling(0.6)
            .build());

    public static final Technique EARTHSHAKER = register(new Technique.Builder("earthshaker", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "shaper_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(20, 10, 17)
            .multiplier(1.24)
            .aoe(4.5, 360.0)
            .massScaling(1.0)
            .build());

    public static final Technique CROSS_SLASH = register(new Technique.Builder("cross_slash", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "wayfinder_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(16, 7, 13)
            .multiplier(1.20)
            .aoe(3.0, 100.0)
            .massScaling(0.4)
            .build());

    public static final Technique METEOR_CRASH = register(new Technique.Builder("meteor_crash", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "raiser_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(20, 9, 16)
            .multiplier(1.25)
            .aoe(4.0, 360.0)
            .massScaling(1.0)
            .build());

    public static final Technique DRAGON_FANG = register(new Technique.Builder("dragon_fang", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "host_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(17, 8, 14)
            .multiplier(1.22)
            .aoe(3.0, 75.0)
            .massScaling(0.5)
            .build());

    public static final Technique PHANTOM_THRUST = register(new Technique.Builder("phantom_thrust", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "vex_armor_trim_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(15, 6, 12)
            .multiplier(1.18)
            .aoe(3.5, 40.0)
            .massScaling(0.3)
            .build());

    public static final Technique HEAVY_GUILLOTINE = register(new Technique.Builder("heavy_guillotine", TechniqueType.MARTIAL_ART)
            .source(ResourceLocation.fromNamespaceAndPath("minecraft", "netherite_upgrade_smithing_template"))
            .actionKey(Technique.ActionKey.LMB)
            .timing(22, 12, 19)
            .multiplier(1.25)
            .aoe(3.2, 90.0)
            .massScaling(1.0)
            .build());

    private static Technique register(Technique technique) {
        ARTS.put(technique.getId(), technique);
        return technique;
    }

    public static Collection<Technique> getAll() {
        return Collections.unmodifiableCollection(ARTS.values());
    }

    public static Technique get(String id) {
        return ARTS.get(id);
    }

    public static Optional<Technique> getByTemplate(ResourceLocation templateLoc) {
        return ARTS.values().stream()
                .filter(t -> t.getCraftSourceItem() != null && t.getCraftSourceItem().equals(templateLoc))
                .findFirst();
    }
}
