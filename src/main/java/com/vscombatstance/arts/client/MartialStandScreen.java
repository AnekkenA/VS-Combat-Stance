package com.vscombatstance.arts.client;

import com.vscombatstance.arts.block.MartialStandMenu;
import com.vscombatstance.arts.combat.CombatStyle;
import com.vscombatstance.arts.item.ScrollItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MartialStandScreen extends AbstractContainerScreen<MartialStandMenu> {

    public MartialStandScreen(MartialStandMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        int startX = leftPos + 30;
        int btnY = topPos + 16;
        int btnW = 36;
        int btnH = 16;

        for (int i = 0; i < 3; i++) {
            final int styleIndex = i;
            String label = "Стиль " + (i == 0 ? "I" : i == 1 ? "II" : "III");
            addRenderableWidget(Button.builder(Component.literal(label), b -> {
                menu.setViewingStyle(styleIndex);
            }).bounds(startX + i * 40, btnY, btnW, btnH).build());
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // Draw dark themed background panel
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF242424);
        graphics.fill(leftPos + 2, topPos + 2, leftPos + imageWidth - 2, topPos + imageHeight - 2, 0xFF353535);

        // Highlight active viewing tab
        int activeIdx = menu.getViewingStyle();
        int tabHighlightX = leftPos + 30 + activeIdx * 40;
        graphics.fill(tabHighlightX - 1, topPos + 15, tabHighlightX + 37, topPos + 33, 0xFFE0A040);

        // Render slot frames
        int startX = leftPos + 44;
        int startY = topPos + 36;
        for (int i = 0; i < CombatStyle.SLOTS; i++) {
            int sx = startX + i * 24;
            graphics.fill(sx - 1, startY - 1, sx + 17, startY + 17, 0xFF181818);
            graphics.fill(sx, startY, sx + 16, startY + 16, 0xFF484848);

            // Arrow connectors between slots
            if (i < CombatStyle.SLOTS - 1) {
                graphics.drawString(font, "➔", sx + 18, startY + 4, 0xAAAAAA, false);
            }
        }

        // Slot frames for inventory
        int invY = topPos + 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int sx = leftPos + 8 + col * 18;
                int sy = invY + row * 18;
                graphics.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF181818);
                graphics.fill(sx, sy, sx + 16, sy + 16, 0xFF424242);
            }
        }

        // Slot frames for hotbar
        for (int col = 0; col < 9; ++col) {
            int sx = leftPos + 8 + col * 18;
            int sy = topPos + 142;
            graphics.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF181818);
            graphics.fill(sx, sy, sx + 16, sy + 16, 0xFF424242);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 6, 0xFFFFFF, false);

        // Calculate compounded multiplier of current style
        double totalMult = 1.0;
        int activeSlots = 0;
        for (int i = 0; i < CombatStyle.SLOTS; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                double dmg = ScrollItem.getDamageMult(stack);
                totalMult *= dmg;
                activeSlots++;
            }
        }

        if (activeSlots > 0) {
            String summary = String.format("Множитель цепи: ×%.2f", totalMult);
            graphics.drawString(this.font, summary, 38, 58, 0x55FF55, false);
        } else {
            graphics.drawString(this.font, "Вставьте свитки приемов в слоты", 20, 58, 0x888888, false);
        }

        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, 0xAAAAAA, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
