package com.vscombatstance.arts.combat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class CombatStyle {
    public static final int SLOTS = 4;
    private final ItemStack[] scrolls = new ItemStack[SLOTS];

    public CombatStyle() {
        for (int i = 0; i < SLOTS; i++) {
            scrolls[i] = ItemStack.EMPTY;
        }
    }

    public ItemStack getScroll(int slot) {
        if (slot >= 0 && slot < SLOTS) {
            return scrolls[slot];
        }
        return ItemStack.EMPTY;
    }

    public void setScroll(int slot, ItemStack stack) {
        if (slot >= 0 && slot < SLOTS) {
            scrolls[slot] = stack.copy();
        }
    }

    public boolean isEmpty() {
        for (ItemStack s : scrolls) {
            if (!s.isEmpty()) return false;
        }
        return true;
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (int i = 0; i < SLOTS; i++) {
            if (!scrolls[i].isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                scrolls[i].save(registries, itemTag);
                list.add(itemTag);
            }
        }
        tag.put("Scrolls", list);
        return tag;
    }

    public void load(HolderLookup.Provider registries, CompoundTag tag) {
        for (int i = 0; i < SLOTS; i++) {
            scrolls[i] = ItemStack.EMPTY;
        }
        if (tag.contains("Scrolls", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Scrolls", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag itemTag = list.getCompound(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot < SLOTS) {
                    scrolls[slot] = ItemStack.parse(registries, itemTag).orElse(ItemStack.EMPTY);
                }
            }
        }
    }
}
