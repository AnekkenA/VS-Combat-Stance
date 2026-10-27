package com.vscombatstance.arts.network;

import com.vscombatstance.arts.CustomArtsMod;
import com.vscombatstance.arts.combat.CombatStyleCapability;
import com.vscombatstance.arts.combat.ComboEngine;
import com.vscombatstance.arts.technique.Technique;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(VSCombatStanceMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, PacketCycleStyle.class,
                PacketCycleStyle::encode, PacketCycleStyle::decode, PacketCycleStyle::handle);
        CHANNEL.registerMessage(id++, PacketTriggerCombat.class,
                PacketTriggerCombat::encode, PacketTriggerCombat::decode, PacketTriggerCombat::handle);
    }

    public static class PacketCycleStyle {
        public PacketCycleStyle() {}
        public static void encode(PacketCycleStyle msg, FriendlyByteBuf buf) {}
        public static PacketCycleStyle decode(FriendlyByteBuf buf) { return new PacketCycleStyle(); }
        public static void handle(PacketCycleStyle msg, NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();
                if (player != null) {
                    CombatStyleCapability.get(player).ifPresent(cap -> {
                        int newIdx = cap.cycleActiveStyle();
                    });
                }
            });
            ctx.setPacketHandled(true);
        }
    }

    public static class PacketTriggerCombat {
        private final Technique.ActionKey actionKey;

        public PacketTriggerCombat(Technique.ActionKey actionKey) {
            this.actionKey = actionKey;
        }

        public static void encode(PacketTriggerCombat msg, FriendlyByteBuf buf) {
            buf.writeEnum(msg.actionKey);
        }

        public static PacketTriggerCombat decode(FriendlyByteBuf buf) {
            return new PacketTriggerCombat(buf.readEnum(Technique.ActionKey.class));
        }

        public static void handle(PacketTriggerCombat msg, NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();
                if (player != null) {
                    ComboEngine.triggerKey(player, msg.actionKey);
                }
            });
            ctx.setPacketHandled(true);
        }
    }
}
