package com.vscombatstance.threats.item;

import com.vscombatstance.threats.world.NightPatrolSpawner;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarHornItem extends Item {
    public static final int COOLDOWN_TICKS = 4800; // 4 minutes

    public WarHornItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.canSeeSky(player.blockPosition())) {
            if (level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.customthreats.cannot_see_sky")
                        .withStyle(ChatFormatting.RED), true);
            }
            player.playSound(SoundEvents.DISPENSER_FAIL, 0.8f, 1.0f);
            return InteractionResultHolder.fail(stack);
        }

        // Play deep ominous war horn sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(4).value(), SoundSource.PLAYERS, 2.0f, 0.55f);

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            boolean spawned = NightPatrolSpawner.spawnWarHornWave(serverLevel, player);
            if (spawned) {
                player.displayClientMessage(Component.translatable("message.customthreats.war_horn_called")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), true);
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            } else {
                player.displayClientMessage(Component.translatable("message.customthreats.no_spawn_space")
                        .withStyle(ChatFormatting.RED), true);
                return InteractionResultHolder.fail(stack);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
        tooltip.add(Component.translatable("tooltip.customthreats.war_horn_desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.customthreats.war_horn_cooldown").withStyle(ChatFormatting.DARK_GRAY));
    }
}
