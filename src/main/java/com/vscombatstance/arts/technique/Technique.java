package com.vscombatstance.arts.technique;

import net.minecraft.resources.ResourceLocation;

public class Technique {
    private final String id;
    private final String nameKey;
    private final String descKey;
    private final TechniqueType type;
    private final ResourceLocation craftSourceItem; // Smithing template or thematic ingredient
    private final ActionKey actionKey;
    private final int durationTicks;
    private final int timingWindowStart;
    private final int timingWindowEnd;
    private final double baseMultiplier;
    private final double aoeRadius;
    private final double aoeAngle;
    private final double massScaling;
    private final boolean consecutiveStackable;

    public enum ActionKey {
        LMB, RMB, SPACE, ANY
    }

    public Technique(Builder builder) {
        this.id = builder.id;
        this.nameKey = builder.nameKey;
        this.descKey = builder.descKey;
        this.type = builder.type;
        this.craftSourceItem = builder.craftSourceItem;
        this.actionKey = builder.actionKey;
        this.durationTicks = builder.durationTicks;
        this.timingWindowStart = builder.timingWindowStart;
        this.timingWindowEnd = builder.timingWindowEnd;
        this.baseMultiplier = builder.baseMultiplier;
        this.aoeRadius = builder.aoeRadius;
        this.aoeAngle = builder.aoeAngle;
        this.massScaling = builder.massScaling;
        this.consecutiveStackable = builder.consecutiveStackable;
    }

    public String getId() { return id; }
    public String getNameKey() { return nameKey; }
    public String getDescKey() { return descKey; }
    public TechniqueType getType() { return type; }
    public ResourceLocation getCraftSourceItem() { return craftSourceItem; }
    public ActionKey getActionKey() { return actionKey; }
    public int getDurationTicks() { return durationTicks; }
    public int getTimingWindowStart() { return timingWindowStart; }
    public int getTimingWindowEnd() { return timingWindowEnd; }
    public double getBaseMultiplier() { return baseMultiplier; }
    public double getAoeRadius() { return aoeRadius; }
    public double getAoeAngle() { return aoeAngle; }
    public double getMassScaling() { return massScaling; }
    public boolean isConsecutiveStackable() { return consecutiveStackable; }

    public static class Builder {
        private final String id;
        private final TechniqueType type;
        private String nameKey;
        private String descKey;
        private ResourceLocation craftSourceItem;
        private ActionKey actionKey = ActionKey.LMB;
        private int durationTicks = 16;
        private int timingWindowStart = 8;
        private int timingWindowEnd = 14;
        private double baseMultiplier = 1.15;
        private double aoeRadius = 2.5;
        private double aoeAngle = 90.0;
        private double massScaling = 0.5;
        private boolean consecutiveStackable = false;

        public Builder(String id, TechniqueType type) {
            this.id = id;
            this.type = type;
            this.nameKey = "technique.customarts." + id + ".name";
            this.descKey = "technique.customarts." + id + ".desc";
        }

        public Builder source(ResourceLocation loc) {
            this.craftSourceItem = loc;
            return this;
        }

        public Builder actionKey(ActionKey key) {
            this.actionKey = key;
            return this;
        }

        public Builder timing(int duration, int windowStart, int windowEnd) {
            this.durationTicks = duration;
            this.timingWindowStart = windowStart;
            this.timingWindowEnd = windowEnd;
            return this;
        }

        public Builder multiplier(double mult) {
            this.baseMultiplier = mult;
            return this;
        }

        public Builder aoe(double radius, double angle) {
            this.aoeRadius = radius;
            this.aoeAngle = angle;
            return this;
        }

        public Builder massScaling(double scaling) {
            this.massScaling = scaling;
            return this;
        }

        public Builder consecutiveStackable(boolean stackable) {
            this.consecutiveStackable = stackable;
            return this;
        }

        public Technique build() {
            return new Technique(this);
        }
    }
}
