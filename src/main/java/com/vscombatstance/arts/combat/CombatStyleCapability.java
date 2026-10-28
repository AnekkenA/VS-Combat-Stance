package com.vscombatstance.arts.combat;

import com.vscombatstance.VSCombatStanceMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class CombatStyleCapability implements INBTSerializable<CompoundTag> {

    public static final int STYLE_COUNT = 3;
    private final CombatStyle[] styles = new CombatStyle[STYLE_COUNT];
    private int activeStyleIndex = 0;

    public CombatStyleCapability() {
        for (int i = 0; i < STYLE_COUNT; i++) {
            styles[i] = new CombatStyle();
        }
    }

    public CombatStyle getStyle(int index) {
        if (index >= 0 && index < STYLE_COUNT) {
            return styles[index];
        }
        return styles[0];
    }

    public CombatStyle getActiveStyle() {
        return getStyle(activeStyleIndex);
    }

    public int getActiveStyleIndex() {
        return activeStyleIndex;
    }

    public void setActiveStyleIndex(int index) {
        if (index >= 0 && index < STYLE_COUNT) {
            this.activeStyleIndex = index;
        }
    }

    public int cycleActiveStyle() {
        activeStyleIndex = (activeStyleIndex + 1) % STYLE_COUNT;
        return activeStyleIndex;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ActiveStyle", activeStyleIndex);
        for (int i = 0; i < STYLE_COUNT; i++) {
            tag.put("Style_" + i, styles[i].save(provider));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        if (nbt.contains("ActiveStyle")) {
            this.activeStyleIndex = nbt.getInt("ActiveStyle");
        }
        for (int i = 0; i < STYLE_COUNT; i++) {
            if (nbt.contains("Style_" + i)) {
                styles[i].load(provider, nbt.getCompound("Style_" + i));
            }
        }
    }

    public static CombatStyleCapability get(Player player) {
        if (player == null) return null;
        return player.getData(VSCombatStanceMod.COMBAT_STYLES);
    }
}
