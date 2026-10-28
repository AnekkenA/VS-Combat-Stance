package com.vscombatstance.arts.client;

import com.vscombatstance.VSCombatStanceMod;
import com.vscombatstance.arts.combat.CombatStyleCapability;
import com.vscombatstance.arts.combat.ComboEngine;
import com.vscombatstance.arts.combat.ComboState;
import com.vscombatstance.arts.network.ModNetwork;
import com.vscombatstance.arts.technique.Technique;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = VSCombatStanceMod.MODID, value = Dist.CLIENT)
public class KeyInputHandler {

    public static final KeyMapping KEY_STANCE = new KeyMapping(
            "key.customarts.stance",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.customarts"
    );

    public static final KeyMapping KEY_CYCLE_STYLE = new KeyMapping(
            "key.customarts.cycle_style",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            "key.categories.customarts"
    );

    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KEY_STANCE);
        event.register(KEY_CYCLE_STYLE);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.screen != null) return;

        if (KEY_CYCLE_STYLE.consumeClick()) {
            CombatStyleCapability cap = CombatStyleCapability.get(player);
            if (cap != null) {
                cap.cycleActiveStyle();
            }
            PacketDistributor.sendToServer(new ModNetwork.PacketCycleStyle());
        }

        if (KEY_STANCE.consumeClick()) {
            ComboEngine.triggerKey(player, Technique.ActionKey.LMB);
            PacketDistributor.sendToServer(new ModNetwork.PacketTriggerCombat(Technique.ActionKey.LMB));
        }

        ComboState state = ComboEngine.getState(player);
        if (state.isActive()) {
            if (mc.options.keyJump.consumeClick()) {
                ComboEngine.triggerKey(player, Technique.ActionKey.SPACE);
                PacketDistributor.sendToServer(new ModNetwork.PacketTriggerCombat(Technique.ActionKey.SPACE));
            }
        }
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.screen != null) return;

        ComboState state = ComboEngine.getState(player);
        if (state.isActive() && event.getAction() == GLFW.GLFW_PRESS) {
            Technique.ActionKey key = null;
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
                key = Technique.ActionKey.LMB;
            } else if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_2) {
                key = Technique.ActionKey.RMB;
            }

            if (key != null) {
                ComboEngine.triggerKey(player, key);
                PacketDistributor.sendToServer(new ModNetwork.PacketTriggerCombat(key));
            }
        }
    }
}
