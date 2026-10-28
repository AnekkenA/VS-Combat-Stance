package com.vscombatstance.arts.technique;

import net.minecraft.ChatFormatting;

public enum TechniqueType {
    MARTIAL_ART("technique.type.martial_art", ChatFormatting.RED, ChatFormatting.DARK_RED),
    LINKER("technique.type.linker", ChatFormatting.AQUA, ChatFormatting.DARK_AQUA);

    private final String translationKey;
    private final ChatFormatting primaryColor;
    private final ChatFormatting secondaryColor;

    TechniqueType(String translationKey, ChatFormatting primaryColor, ChatFormatting secondaryColor) {
        this.translationKey = translationKey;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public ChatFormatting getPrimaryColor() {
        return primaryColor;
    }

    public ChatFormatting getSecondaryColor() {
        return secondaryColor;
    }
}
