package com.vscombatstance.arts.item;

import net.minecraft.ChatFormatting;
import java.util.Random;

public class RarityRoller {

    public static final double MIN_STAT = 1.05;
    public static final double MAX_STAT = 1.25;
    public static final double RANGE = MAX_STAT - MIN_STAT; // 0.20
    public static final double EXPONENT = 2.5;

    public enum QualityTier {
        COMMON("rarity.customarts.common", ChatFormatting.WHITE, ChatFormatting.GRAY),
        RARE("rarity.customarts.rare", ChatFormatting.AQUA, ChatFormatting.DARK_AQUA),
        GOD_ROLL("rarity.customarts.god_roll", ChatFormatting.GOLD, ChatFormatting.YELLOW);

        private final String translationKey;
        private final ChatFormatting titleColor;
        private final ChatFormatting valueColor;

        QualityTier(String translationKey, ChatFormatting titleColor, ChatFormatting valueColor) {
            this.translationKey = translationKey;
            this.titleColor = titleColor;
            this.valueColor = valueColor;
        }

        public String getTranslationKey() { return translationKey; }
        public ChatFormatting getTitleColor() { return titleColor; }
        public ChatFormatting getValueColor() { return valueColor; }
    }

    /**
     * Non-linear random roll using:
     * Stat = 1.05 + (Random^2.5) * 0.20
     */
    public static double rollStat(Random random) {
        double r = random.nextDouble();
        double skewed = Math.pow(r, EXPONENT);
        double result = MIN_STAT + skewed * RANGE;
        return Math.round(result * 100.0) / 100.0; // Round to 2 decimal places
    }

    public static QualityTier getTier(double stat) {
        if (stat >= 1.22) {
            return QualityTier.GOD_ROLL;
        } else if (stat >= 1.15) {
            return QualityTier.RARE;
        } else {
            return QualityTier.COMMON;
        }
    }

    public static QualityTier getOverallTier(double mult1, double mult2, double mult3) {
        double avg = (mult1 + mult2 + mult3) / 3.0;
        return getTier(avg);
    }
}
