package com.vscombatstance.threats.client.model;

import com.vscombatstance.VSCombatStanceMod;
import com.vscombatstance.threats.entity.EliteGuardEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EliteGuardModel extends GeoModel<EliteGuardEntity> {

    @Override
    public ResourceLocation getModelResource(EliteGuardEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(VSCombatStanceMod.MODID, "geo/elite_guard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EliteGuardEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(VSCombatStanceMod.MODID, "textures/entity/elite_guard.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EliteGuardEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(VSCombatStanceMod.MODID, "animations/elite_guard.animation.json");
    }
}
