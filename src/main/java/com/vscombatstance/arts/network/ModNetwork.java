package com.vscombatstance.arts.network;

import com.vscombatstance.VSCombatStanceMod;
import com.vscombatstance.arts.combat.CombatStyleCapability;
import com.vscombatstance.arts.combat.ComboEngine;
import com.vscombatstance.arts.technique.Technique;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = VSCombatStanceMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetwork {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(PacketCycleStyle.TYPE, PacketCycleStyle.STREAM_CODEC, PacketCycleStyle::handle);
        registrar.playToServer(PacketTriggerCombat.TYPE, PacketTriggerCombat.STREAM_CODEC, PacketTriggerCombat::handle);
    }

    public record PacketCycleStyle() implements CustomPacketPayload {
        public static final Type<PacketCycleStyle> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(VSCombatStanceMod.MODID, "cycle_style"));
        public static final StreamCodec<ByteBuf, PacketCycleStyle> STREAM_CODEC = StreamCodec.unit(new PacketCycleStyle());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(PacketCycleStyle msg, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.player() instanceof ServerPlayer player) {
                    CombatStyleCapability cap = CombatStyleCapability.get(player);
                    if (cap != null) {
                        cap.cycleActiveStyle();
                    }
                }
            });
        }
    }

    public record PacketTriggerCombat(Technique.ActionKey actionKey) implements CustomPacketPayload {
        public static final Type<PacketTriggerCombat> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(VSCombatStanceMod.MODID, "trigger_combat"));
        public static final StreamCodec<ByteBuf, PacketTriggerCombat> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.idMapper(i -> Technique.ActionKey.values()[i], Technique.ActionKey::ordinal),
                PacketTriggerCombat::actionKey,
                PacketTriggerCombat::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(PacketTriggerCombat msg, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.player() instanceof ServerPlayer player) {
                    ComboEngine.triggerKey(player, msg.actionKey());
                }
            });
        }
    }
}
