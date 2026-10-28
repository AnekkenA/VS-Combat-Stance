package com.vscombatstance.arts.client;

import com.vscombatstance.arts.combat.CombatStyle;
import com.vscombatstance.arts.combat.CombatStyleCapability;
import com.vscombatstance.arts.combat.ComboEngine;
import com.vscombatstance.arts.combat.ComboState;
import com.vscombatstance.arts.item.ScrollItem;
import com.vscombatstance.arts.technique.Technique;
import com.vscombatstance.arts.technique.TechniqueCatalog;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CombatHudOverlay {

    private static long styleIndicatorEndTimeMs = 0;

    public static void showStyleIndicator() {
        styleIndicatorEndTimeMs = System.currentTimeMillis() + 2000L; // 2 секунды
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        CombatStyleCapability cap = CombatStyleCapability.get(player);
        if (cap != null) {
            int screenWidth = graphics.guiWidth();
            int screenHeight = graphics.guiHeight();

            // Показываем индикатор только в течение 2 секунд после переключения стиля
            if (System.currentTimeMillis() < styleIndicatorEndTimeMs) {
                renderStyleIndicator(graphics, mc.font, screenWidth, screenHeight, cap);
            }

            ComboState state = ComboEngine.getState(player);
            if (state.isActive()) {
                renderComboRhythm(graphics, mc.font, screenWidth, screenHeight, state, cap.getActiveStyle());
            }
        }
    }

    private static void renderStyleIndicator(GuiGraphics graphics, Font font, int width, int height, CombatStyleCapability cap) {
        int x = width / 2 - 25;
        int y = height - 40;

        int styleIdx = cap.getActiveStyleIndex() + 1;
        String styleText = "◆ СТИЛЬ " + (styleIdx == 1 ? "I" : styleIdx == 2 ? "II" : "III");
        graphics.drawString(font, styleText, x, y, 0xFFE0A0, true);
    }

    private static void renderComboRhythm(GuiGraphics graphics, Font font, int width, int height, ComboState state, CombatStyle style) {
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
        int maxTimer = state.getMaxTimer();
        if (maxTimer > 0) {
            float progress = (float) timer / (float) maxTimer;
            int ringRadius = (int) (14 + progress * 8);
            drawCircle(graphics, cx, cy, ringRadius, ringColor);
        }

        // 2. Step and Key Indicator
        String stepText = "Комбо " + (step + 1) + "/4";
        graphics.drawCenteredString(font, stepText, cx, cy + 22, 0xFFFFFF);

        if (tech != null) {
            String techName = tech.getName();
            String keyName = "[" + tech.getRequiredKey().name() + "]";
            int color = inWindow ? 0x55FF55 : 0xFFFF55;
            graphics.drawCenteredString(font, keyName + " " + techName, cx, cy + 32, color);
        }
    }

    private static void drawCircle(GuiGraphics graphics, int cx, int cy, int radius, int color) {
        int segments = 16;
        for (int i = 0; i < segments; i++) {
            double angle1 = 2 * Math.PI * i / segments;
            double angle2 = 2 * Math.PI * (i + 1) / segments;

            int x1 = cx + (int) (Math.cos(angle1) * radius);
            int y1 = cy + (int) (Math.sin(angle1) * radius);
            int x2 = cx + (int) (Math.cos(angle2) * radius);
            int y2 = cy + (int) (Math.sin(angle2) * radius);

            graphics.fill(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2) + 1, Math.max(y1, y2) + 1, color);
        }
    }
}
