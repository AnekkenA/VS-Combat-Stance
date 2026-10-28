package com.vscombatstance.arts.combat;

import com.vscombatstance.arts.technique.Technique;
import net.minecraft.world.item.ItemStack;

public class ComboState {
    private boolean active = false;
    private int currentStep = 0;
    private int timer = 0;
    private int stepDuration = 20;
    private int windowStart = 8;
    private int windowEnd = 14;

    private double accumulatedDamageMult = 1.0;
    private double accumulatedSpeedMult = 1.0;

    private String lastTechniqueId = "";
    private int consecutiveCount = 0;

    private int airborneLevel = 0;
    private int riposteLevel = 0;
    private int feintLevel = 0;
    private int disengageLevel = 0;

    public void startCombo(Technique firstTech, ItemStack scroll) {
        this.active = true;
        this.currentStep = 0;
        this.timer = 0;
        this.accumulatedDamageMult = 1.0;
        this.accumulatedSpeedMult = 1.0;
        this.consecutiveCount = 0;
        this.lastTechniqueId = "";
        this.airborneLevel = 0;
        this.riposteLevel = 0;
        this.feintLevel = 0;
        this.disengageLevel = 0;

        setupStep(firstTech, scroll);
    }

    public void setupStep(Technique tech, ItemStack scroll) {
        this.timer = 0;
        double speedMult = com.vscombatstance.arts.item.ScrollItem.getSpeedMult(scroll);
        this.stepDuration = Math.max(8, (int) Math.round(tech.getDurationTicks() / speedMult));
        this.windowStart = Math.max(2, (int) Math.round(tech.getTimingWindowStart() / speedMult));
        this.windowEnd = Math.min(stepDuration - 1, (int) Math.round(tech.getTimingWindowEnd() / speedMult));

        if (tech.getId().equals(lastTechniqueId) && tech.isConsecutiveStackable()) {
            consecutiveCount++;
        } else {
            consecutiveCount = 1;
            lastTechniqueId = tech.getId();
        }
    }

    public void tick() {
        if (!active) return;
        timer++;
        if (timer > stepDuration) {
            // Window expired without input -> combo dropped
            reset();
        }
    }

    public boolean isInTimingWindow() {
        return active && timer >= windowStart && timer <= windowEnd;
    }

    public float getWindowProgress() {
        if (!active || stepDuration <= 0) return 0f;
        return (float) timer / (float) stepDuration;
    }

    public void multiplyDamage(double multiplier) {
        this.accumulatedDamageMult *= multiplier;
    }

    public void multiplySpeed(double multiplier) {
        this.accumulatedSpeedMult *= multiplier;
    }

    public void reset() {
        this.active = false;
        this.currentStep = 0;
        this.timer = 0;
        this.accumulatedDamageMult = 1.0;
        this.accumulatedSpeedMult = 1.0;
        this.consecutiveCount = 0;
        this.lastTechniqueId = "";
        this.airborneLevel = 0;
        this.riposteLevel = 0;
        this.feintLevel = 0;
        this.disengageLevel = 0;
    }

    // Getters and Setters
    public boolean isActive() { return active; }
    public int getCurrentStep() { return currentStep; }
    public void setCurrentStep(int step) { this.currentStep = step; }
    public int getTimer() { return timer; }
    public int getMaxTimer() { return stepDuration; }
    public int getStepDuration() { return stepDuration; }
    public int getWindowStart() { return windowStart; }
    public int getWindowEnd() { return windowEnd; }
    public double getAccumulatedDamageMult() { return accumulatedDamageMult; }
    public double getAccumulatedSpeedMult() { return accumulatedSpeedMult; }
    public int getConsecutiveCount() { return consecutiveCount; }
    public String getLastTechniqueId() { return lastTechniqueId; }

    public int getAirborneLevel() { return airborneLevel; }
    public void setAirborneLevel(int level) { this.airborneLevel = level; }
    public boolean isAirborne() { return airborneLevel > 0; }

    public int getRiposteLevel() { return riposteLevel; }
    public void setRiposteLevel(int level) { this.riposteLevel = level; }
    public boolean isRiposteReady() { return riposteLevel > 0; }

    public int getFeintLevel() { return feintLevel; }
    public void setFeintLevel(int level) { this.feintLevel = level; }
    public boolean isFeintReady() { return feintLevel > 0; }

    public int getDisengageLevel() { return disengageLevel; }
    public void setDisengageLevel(int level) { this.disengageLevel = level; }
}
