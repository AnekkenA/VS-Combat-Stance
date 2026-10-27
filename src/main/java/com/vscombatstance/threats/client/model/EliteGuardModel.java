package com.vscombatstance.threats.client.model;

import com.vscombatstance.threats.CustomThreatsMod;
import com.vscombatstance.threats.entity.EliteGuardEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EliteGuardModel extends GeoModel<EliteGuardEntity> {

    @Override
    public ResourceLocation getModelResource(EliteGuardEntity animatable) {
        return new ResourceLocation(VSCombatStanceMod.MODID, "geo/elite_guard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EliteGuardEntity animatable) {
        return new ResourceLocation(VSCombatStanceMod.MODID, "textures/entity/elite_guard.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EliteGuardEntity animatable) {
        return new ResourceLocation(VSCombatStanceMod.MODID, "animations/elite_guard.animation.json");
    }
}
