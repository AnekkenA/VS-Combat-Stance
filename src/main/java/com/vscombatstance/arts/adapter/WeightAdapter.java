package com.vscombatstance.arts.adapter;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class WeightAdapter {

    public static final double MIN_WEIGHT = 60.0;
    public static final double MAX_WEIGHT = 1000.0;
    public static final double DEFAULT_SWORD_WEIGHT = 250.0;
    public static final double HEAVY_THRESHOLD = 350.0;

    /**
     * Reads the physical weight from Voxel Stand CustomData NBT if available,
     * or computes the effective virtual mass from generic attack speed.
     */
    public static double getEffectiveWeight(ItemStack stack) {
        if (stack.isEmpty()) return MIN_WEIGHT;

        // 1. Direct integration with Voxel Stand NBT via 1.21 DataComponents.CUSTOM_DATA
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag != null && !tag.isEmpty()) {
            if (tag.contains("VoxelStandStats") || tag.contains("CustomSmithStats")) {
                CompoundTag stats = tag.contains("VoxelStandStats") ? tag.getCompound("VoxelStandStats") : tag.getCompound("CustomSmithStats");
                if (stats.contains("Weight")) {
                    double customWeight = stats.getDouble("Weight");
                    if (customWeight > 0.0) {
                        return Math.max(MIN_WEIGHT, Math.min(MAX_WEIGHT, customWeight));
                    }
                }
            }
            if (tag.contains("totalWeight")) {
                double customWeight = tag.getDouble("totalWeight");
                if (customWeight > 0.0) {
                    return Math.max(MIN_WEIGHT, Math.min(MAX_WEIGHT, customWeight));
                }
            }
            if (tag.contains("Weight")) {
                double customWeight = tag.getDouble("Weight");
                if (customWeight > 0.0) {
                    return Math.max(MIN_WEIGHT, Math.min(MAX_WEIGHT, customWeight));
                }
            }
        }

        // 2. Fallback for vanilla and external mod weapons
        double speed = getAttackSpeed(stack);
        if (speed <= 0.0) speed = 1.6; // Default standard sword speed

        // Formula: clamp((4.0 / attackSpeed) * 100, 60, 1000)
        double calculated = (4.0 / Math.max(0.4, speed)) * 100.0;
        return Math.max(MIN_WEIGHT, Math.min(MAX_WEIGHT, calculated));
    }

    public static double getAttackSpeed(ItemStack stack) {
        if (stack.isEmpty()) return 4.0;

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag != null && !tag.isEmpty()) {
            if (tag.contains("CustomSmithStats")) {
                CompoundTag stats = tag.getCompound("CustomSmithStats");
                if (stats.contains("AttackSpeed")) {
                    return stats.getDouble("AttackSpeed");
                }
            }
            if (tag.contains("attackSpeed")) {
                return tag.getDouble("attackSpeed");
            }
        }

        return 1.6; // Default sword attack speed
    }

    /**
     * Calculates the mass-scaling bonus damage for heavy techniques (Overhead Slam, Earthshaker, etc.).
     */
    public static double calculateMassBonus(double weight, double techniqueMassScaling) {
        if (techniqueMassScaling <= 0.0) return 1.0;
        // Weight 250 -> 1.0x. Weight 500 -> 1.25x. Weight 800 -> 1.55x.
        double ratio = weight / 250.0;
        return 1.0 + (ratio - 1.0) * (0.25 * techniqueMassScaling);
    }

    public static boolean isHeavy(double weight) {
        return weight >= HEAVY_THRESHOLD;
    }
}
