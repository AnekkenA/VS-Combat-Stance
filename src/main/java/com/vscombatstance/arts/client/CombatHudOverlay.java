package com.vscombatstance.arts.client;

import com.vscombatstance.arts.combat.CombatStyle;
import com.vscombatstance.arts.combat.CombatStyleCapability;
import com.vscombatstance.arts.combat.ComboEngine;
import com.vscombatstance.arts.combat.ComboState;
import com.vscombatstance.arts.item.ScrollItem;
import com.vscombatstance.arts.technique.Technique;
import com.vscombatstance.arts.technique.TechniqueCatalog;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.overlay.ForgeGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

public class CombatHudOverlay implements IGuiOverlay {

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        CombatStyleCapability.get(player).ifPresent(cap -> {
            renderStyleIndicator(graphics, mc.font, screenWidth, screenHeight, cap);

            ComboState state = ComboEngine.getState(player);
            if (state.isActive()) {
                renderComboRhythm(graphics, mc.font, screenWidth, screenHeight, state, cap.getActiveStyle());
            }
        });
    }

    private void renderStyleIndicator(GuiGraphics graphics, Font font, int width, int height, CombatStyleCapability cap) {
        int x = width / 2 - 25;
        int y = height - 40;

        int styleIdx = cap.getActiveStyleIndex() + 1;
        String styleText = "◆ СТИЛЬ " + (styleIdx == 1 ? "I" : styleIdx == 2 ? "II" : "III");
        graphics.drawString(font, styleText, x, y, 0xFFE0A0, true);
    }

    private void renderComboRhythm(GuiGraphics graphics, Font font, int width, int height, ComboState state, CombatStyle style) {
        int cx = width / 2;
        int cy = height / 2;

        int step = state.getCurrentStep();
        ItemStack scroll = style != null ? style.getScroll(step) : ItemStack.EMPTY;
        String techId = ScrollItem.getTechniqueId(scroll);
        Technique tech = TechniqueCatalog.find(techId);

        // 1. Timing Window Indicator
        boolean inWindow = state.isInTimingWindow();
        int ringColor = inWindow ? 0xFF55FF55 : 0xFFAAAAAA; // Bright green when ready!

        int timer = state.getTimer();
        int max = state.getStepDuration();
        float p = (float) timer / (float) Math.max(1, max);

        // Draw timing progress bar under crosshair
        int barW = 40;
        int barH = 3;
        int barX = cx - barW / 2;
        int barY = cy + 16;

        // Background
        graphics.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0x88000000);

        // Timing target window zone
        float winStartP = (float) state.getWindowStart() / (float) max;
        float winEndP = (float) state.getWindowEnd() / (float) max;
        int winX1 = barX + (int) (winStartP * barW);
        int winX2 = barX + (int) (winEndP * barW);
        graphics.fill(winX1, barY, winX2, barY + barH, 0x6600FF00);

        // Active cursor
        int cursorX = barX + (int) (p * barW);
        graphics.fill(cursorX - 1, barY - 1, cursorX + 1, barY + barH + 1, ringColor);

        // 2. Action Key Cue
        if (tech != null) {
            String keyName = "[" + tech.getActionKey().name() + "]";
            int keyColor = inWindow ? 0xFF00FF00 : 0xFFFFAA00;
            int keyW = font.width(keyName);
            graphics.drawString(font, keyName, cx - keyW / 2, cy + 24, keyColor, true);

            // Step counter & Multiplier
            String stepStr = String.format("Шаг %d/4  ×%.2f", step + 1, state.getAccumulatedDamageMult());
            int stepW = font.width(stepStr);
            graphics.drawString(font, stepStr, cx - stepW / 2, cy + 34, 0xFFFFFF, true);

            // Consecutive stack notice
            if (state.getConsecutiveCount() > 1) {
                String stackNotice = "⚡ Серия сцепок x" + state.getConsecutiveCount() + "!";
                int stackW = font.width(stackNotice);
                graphics.drawString(font, stackNotice, cx - stackW / 2, cy - 25, 0x55FFFF, true);
            }
        }
    }
}
