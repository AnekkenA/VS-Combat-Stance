package com.vscombatstance.arts.block;

import com.vscombatstance.arts.combat.CombatStyle;
import com.vscombatstance.arts.combat.CombatStyleCapability;
import com.vscombatstance.arts.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MartialStandMenu extends AbstractContainerMenu {
    private final Container styleContainer = new SimpleContainer(CombatStyle.SLOTS);
    private final Player player;
    private int viewingStyle = 0;

    public MartialStandMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv);
    }

    public MartialStandMenu(int containerId, Inventory playerInv) {
        super(ModMenuTypes.MARTIAL_STAND_MENU.get(), containerId);
        this.player = playerInv.player;

        // Load current active style
        CombatStyleCapability.get(player).ifPresent(cap -> {
            this.viewingStyle = cap.getActiveStyleIndex();
            loadStyleIntoContainer(cap.getStyle(viewingStyle));
        });

        // Add 4 Combat Style slots
        int startX = 44;
        int startY = 36;
        for (int i = 0; i < CombatStyle.SLOTS; i++) {
            final int slotIndex = i;
            this.addSlot(new Slot(styleContainer, slotIndex, startX + i * 24, startY) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(ModItems.SCROLL.get());
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });
        }

        // Add Player Inventory (3 rows x 9 columns)
        int invY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, invY + row * 18));
            }
        }

        // Add Player Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public void setViewingStyle(int styleIndex) {
        if (styleIndex < 0 || styleIndex >= CombatStyleCapability.STYLE_COUNT) return;
        saveContainerToStyle();
        this.viewingStyle = styleIndex;
        CombatStyleCapability.get(player).ifPresent(cap -> loadStyleIntoContainer(cap.getStyle(viewingStyle)));
    }

    public int getViewingStyle() {
        return viewingStyle;
    }

    private void loadStyleIntoContainer(CombatStyle style) {
        for (int i = 0; i < CombatStyle.SLOTS; i++) {
            styleContainer.setItem(i, style.getScroll(i).copy());
        }
    }

    private void saveContainerToStyle() {
        CombatStyleCapability.get(player).ifPresent(cap -> {
            CombatStyle style = cap.getStyle(viewingStyle);
            for (int i = 0; i < CombatStyle.SLOTS; i++) {
                style.setScroll(i, styleContainer.getItem(i));
            }
        });
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        saveContainerToStyle();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < CombatStyle.SLOTS) {
                // Moving from style slots to player inventory
                if (!this.moveItemStackTo(stackInSlot, CombatStyle.SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to style slots (only if scroll)
                if (stackInSlot.is(ModItems.SCROLL.get())) {
                    if (!this.moveItemStackTo(stackInSlot, 0, CombatStyle.SLOTS, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
