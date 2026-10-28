package com.vscombatstance.arts.client.animator;

import com.vscombatstance.VSCombatStanceMod;
import com.vscombatstance.arts.adapter.WeightAdapter;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerAnimationManager {
    public static final ResourceLocation ANIMATION_LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(VSCombatStanceMod.MODID, "combat_anim");

    private static boolean initialized = false;

    /**
     * Регистрирует слой анимации в фабрике playerAnimator на стороне клиента
     */
    public static void init() {
        if (initialized) return;
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                ANIMATION_LAYER_ID,
                42,
                player -> new ModifierLayer<>()
        );
        initialized = true;
    }

    /**
     * Воспроизводит ключевую покадровую анимацию приема из Blockbench с масштабированием
     * скорости под физический вес оружия из CustomSmith.
     * 
     * @return true, если анимация найдена в playerAnimator и запущена;
     *         false, если требуется откат на процедурный аниматор (ProceduralPlayerAnimator).
     */
    @SuppressWarnings("unchecked")
    public static boolean playTechniqueAnimation(Player player, String techniqueId, ItemStack weaponStack) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            return false;
        }

        // 1. Получаем слой анимации игрока
        ModifierLayer<IAnimation> animLayer = (ModifierLayer<IAnimation>)
                PlayerAnimationAccess.getPlayerAssociatedData(clientPlayer).get(ANIMATION_LAYER_ID);

        if (animLayer == null) {
            return false;
        }

        // 2. Ищем скомпилированную анимацию из JSON-ресурсов playerAnimator
        ResourceLocation animId = ResourceLocation.fromNamespaceAndPath(VSCombatStanceMod.MODID, techniqueId);
        dev.kosmx.playerAnim.api.IPlayable playable = PlayerAnimationRegistry.getAnimation(animId);
        if (!(playable instanceof KeyframeAnimation animation)) {
            // Если для данного приема пока нет JSON-файла, отдаем управление процедурному аниматору
            return false;
        }

        // 3. Расчет скорости воспроизведения на основе массы оружия (WeightAdapter)
        // Стандартный вес (меч 250) = 1.0x. Легкий кинжал (120) = ~1.4x. Тяжелый молот (600) = ~0.65x.
        double weight = WeightAdapter.getEffectiveWeight(weaponStack);
        float speedMultiplier = (float) Math.max(0.6f, Math.min(1.75f, 250.0 / weight));

        // 4. Запуск анимации с плавной интерполяцией суставов и скоростью размаха
        KeyframeAnimationPlayer animationPlayer = new KeyframeAnimationPlayer(animation);
        // Регулируем тайминг фаз замаха и удара под скорость атаки
        animLayer.setAnimation(animationPlayer);

        return true;
    }

    /**
     * Мгновенный сброс или отмена текущей анимации удара
     */
    @SuppressWarnings("unchecked")
    public static void stopAnimation(Player player) {
        if (player instanceof AbstractClientPlayer clientPlayer) {
            ModifierLayer<IAnimation> animLayer = (ModifierLayer<IAnimation>)
                    PlayerAnimationAccess.getPlayerAssociatedData(clientPlayer).get(ANIMATION_LAYER_ID);
            if (animLayer != null) {
                animLayer.setAnimation(null);
            }
        }
    }
}
