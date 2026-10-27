package com.vscombatstance.arts.item;

import com.vscombatstance.arts.technique.Technique;
import com.vscombatstance.arts.technique.TechniqueCatalog;
import com.vscombatstance.arts.technique.TechniqueType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ScrollItem extends Item {

    public static final String TAG_TECHNIQUE_ID = "TechniqueId";
    public static final String TAG_DAMAGE_MULT = "DamageMult";
    public static final String TAG_SYNERGY_MULT = "SynergyMult";
    public static final String TAG_SPEED_MULT = "SpeedMult";

    public ScrollItem(Properties properties) {
        super(properties);
    }

    public static ItemStack createScroll(Technique technique, Random random) {
        ItemStack stack = new ItemStack(ModItems.SCROLL.get());
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(TAG_TECHNIQUE_ID, technique.getId());
            tag.putDouble(TAG_DAMAGE_MULT, RarityRoller.rollStat(random));
            tag.putDouble(TAG_SYNERGY_MULT, RarityRoller.rollStat(random));
            tag.putDouble(TAG_SPEED_MULT, RarityRoller.rollStat(random));
        });
        return stack;
    }

    public static void rerollStats(ItemStack stack, Random random) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putDouble(TAG_DAMAGE_MULT, RarityRoller.rollStat(random));
            tag.putDouble(TAG_SYNERGY_MULT, RarityRoller.rollStat(random));
            tag.putDouble(TAG_SPEED_MULT, RarityRoller.rollStat(random));
        });
    }

    public static String getTechniqueId(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag != null && tag.contains(TAG_TECHNIQUE_ID) ? tag.getString(TAG_TECHNIQUE_ID) : null;
    }

    public static double getDamageMult(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag != null && tag.contains(TAG_DAMAGE_MULT) ? tag.getDouble(TAG_DAMAGE_MULT) : 1.05;
    }

    public static double getSynergyMult(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag != null && tag.contains(TAG_SYNERGY_MULT) ? tag.getDouble(TAG_SYNERGY_MULT) : 1.05;
    }

    public static double getSpeedMult(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag != null && tag.contains(TAG_SPEED_MULT) ? tag.getDouble(TAG_SPEED_MULT) : 1.05;
    }

    @Override
    public Component getName(ItemStack stack) {
        String techId = getTechniqueId(stack);
        if (techId != null) {
            Technique technique = TechniqueCatalog.find(techId);
            if (technique != null) {
                return Component.translatable(technique.getNameKey()).withStyle(technique.getType().getPrimaryColor());
            }
        }
        return Component.translatable("item.vs_combatstance.scroll_blank").withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
        appendHoverTextInternal(stack, tooltip);
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        appendHoverTextInternal(stack, tooltip);
    }

    private void appendHoverTextInternal(ItemStack stack, List<Component> tooltip) {
        String techId = getTechniqueId(stack);
        if (techId == null) {
            tooltip.add(Component.translatable("tooltip.vs_combatstance.scroll_unbound").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        Technique technique = TechniqueCatalog.find(techId);
        if (technique == null) return;

        double dmg = getDamageMult(stack);
        double syn = getSynergyMult(stack);
        double spd = getSpeedMult(stack);

        RarityRoller.QualityTier tier = RarityRoller.getOverallTier(dmg, syn, spd);

        // Header: Type badge & Tier
        Component typeBadge = Component.translatable(technique.getType().getTranslationKey())
                .withStyle(technique.getType().getPrimaryColor(), ChatFormatting.BOLD);
        Component tierBadge = Component.translatable(tier.getTranslationKey())
                .withStyle(tier.getTitleColor(), ChatFormatting.BOLD);
        tooltip.add(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY)
                .append(typeBadge)
                .append(Component.literal(" • ").withStyle(ChatFormatting.GRAY))
                .append(tierBadge)
                .append(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY)));

        tooltip.add(Component.empty());

        // Stats with color coded tiers
        tooltip.add(Component.literal("  ⚔ ")
                .append(Component.translatable("tooltip.vs_combatstance.stat_damage"))
                .append(Component.literal(": "))
                .append(Component.literal(String.format("×%.2f", dmg))
                        .withStyle(RarityRoller.getTier(dmg).getValueColor(), ChatFormatting.BOLD)));

        tooltip.add(Component.literal("  ✦ ")
                .append(Component.translatable("tooltip.vs_combatstance.stat_synergy"))
                .append(Component.literal(": "))
                .append(Component.literal(String.format("×%.2f", syn))
                        .withStyle(RarityRoller.getTier(syn).getValueColor(), ChatFormatting.BOLD)));

        tooltip.add(Component.literal("  ⚡ ")
                .append(Component.translatable("tooltip.vs_combatstance.stat_speed"))
                .append(Component.literal(": "))
                .append(Component.literal(String.format("×%.2f", spd))
                        .withStyle(RarityRoller.getTier(spd).getValueColor(), ChatFormatting.BOLD)));

        tooltip.add(Component.empty());

        // Action trigger key
        tooltip.add(Component.translatable("tooltip.vs_combatstance.trigger_key", technique.getActionKey().name())
                .withStyle(ChatFormatting.GOLD));

        // Description
        tooltip.add(Component.translatable(technique.getDescKey()).withStyle(ChatFormatting.GRAY));

        if (technique.isConsecutiveStackable()) {
            tooltip.add(Component.translatable("tooltip.vs_combatstance.consecutive_stack_hint")
                    .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC));
        }
    }
}
