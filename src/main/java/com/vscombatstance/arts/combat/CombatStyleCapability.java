package com.vscombatstance.arts.combat;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.common.capabilities.ICapabilitySerializable;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CombatStyleCapability implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<CombatStyleCapability> INSTANCE =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static final int STYLE_COUNT = 3;
    private final CombatStyle[] styles = new CombatStyle[STYLE_COUNT];
    private int activeStyleIndex = 0;

    private final LazyOptional<CombatStyleCapability> optional = LazyOptional.of(() -> this);

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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == INSTANCE ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ActiveStyle", activeStyleIndex);
        for (int i = 0; i < STYLE_COUNT; i++) {
            tag.put("Style_" + i, styles[i].save());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("ActiveStyle")) {
            this.activeStyleIndex = nbt.getInt("ActiveStyle");
        }
        for (int i = 0; i < STYLE_COUNT; i++) {
            if (nbt.contains("Style_" + i)) {
                styles[i].load(nbt.getCompound("Style_" + i));
            }
        }
    }

    public static LazyOptional<CombatStyleCapability> get(Player player) {
        return player.getCapability(INSTANCE);
    }
}
