package com.vscombatstance.arts.client.animator;

import com.vscombatstance.VSCombatStanceMod;
import com.vscombatstance.arts.combat.CombatStyle;
import com.vscombatstance.arts.combat.CombatStyleCapability;
import com.vscombatstance.arts.combat.ComboEngine;
import com.vscombatstance.arts.combat.ComboState;
import com.vscombatstance.arts.item.ScrollItem;
import com.vscombatstance.arts.technique.Technique;
import com.vscombatstance.arts.technique.TechniqueCatalog;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(modid = VSCombatStanceMod.MODID, value = Dist.CLIENT)
public class ProceduralPlayerAnimator {

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        ComboState state = ComboEngine.getState(player);
        if (!state.isActive()) return;

        CombatStyleCapability cap = CombatStyleCapability.get(player);
        if (cap == null) return;
        CombatStyle style = cap.getActiveStyle();
        if (style == null) return;

        int step = state.getCurrentStep();
        ItemStack scroll = style.getScroll(step);
        String techId = ScrollItem.getTechniqueId(scroll);
        Technique tech = TechniqueCatalog.find(techId);
        if (tech == null) return;

        // Если для данного приема запущена Blockbench-анимация через playerAnimator, процедурные суставы отключаются
        if (PlayerAnimationManager.playTechniqueAnimation(player, techId, player.getMainHandItem())) {
            return;
        }

        // Процедурный откат (Procedural Fallback): плавные повороты рук, плеча и наклона тела
        float progress = 1.0f - ((float) state.getTimer() / (float) Math.max(1, state.getStepDuration()));
        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();

        applyProceduralPose(model, tech.getId(), progress);
    }

    private static void applyProceduralPose(PlayerModel<AbstractClientPlayer> model, String techId, float progress) {
        float eased = Easing.easeOutQuad(progress);

        switch (techId) {
            case "overhead_slam" -> {
                // Двуручный замах из-за головы и сокрушительный удар вниз
                float armX = (float) Math.toRadians(-140 + eased * 170); // От -140 до +30 градусов
                model.rightArm.xRot = armX;
                model.leftArm.xRot = armX;
                model.body.xRot = (float) Math.toRadians(eased * 15);
            }
            case "thrust" -> {
                // Молниеносный укол вперед
                model.rightArm.xRot = (float) Math.toRadians(-90);
                model.rightArm.zRot = (float) Math.toRadians(10);
                model.body.yRot = (float) Math.toRadians(-15 * (1.0f - eased));
            }
            case "sweep", "crescent_cleave" -> {
                // Широкий горизонтальный круговой взмах
                model.rightArm.xRot = (float) Math.toRadians(-60);
                model.rightArm.yRot = (float) Math.toRadians(-80 + eased * 140);
                model.body.yRot = (float) Math.toRadians(-30 + eased * 60);
            }
            case "uppercut" -> {
                // Апперкот снизу вверх
                model.rightArm.xRot = (float) Math.toRadians(20 - eased * 130);
                model.body.xRot = (float) Math.toRadians(-10 * (1.0f - eased));
            }
            case "push_kick" -> {
                // Прямой удар ногой
                model.rightLeg.xRot = (float) Math.toRadians(-80 * (float) Math.sin(progress * Math.PI));
                model.body.xRot = (float) Math.toRadians(-15 * (float) Math.sin(progress * Math.PI));
            }
            case "leap_strike" -> {
                // Прыжок с выпадом
                model.rightArm.xRot = (float) Math.toRadians(-120 + eased * 140);
                model.rightLeg.xRot = (float) Math.toRadians(-30);
                model.leftLeg.xRot = (float) Math.toRadians(30);
            }
            case "deflect" -> {
                // Защитная стойка с мечом перед грудью
                model.rightArm.xRot = (float) Math.toRadians(-75);
                model.rightArm.yRot = (float) Math.toRadians(-30);
                model.rightArm.zRot = (float) Math.toRadians(45);
            }
            case "shield_bash" -> {
                // Удар щитом вперед
                model.leftArm.xRot = (float) Math.toRadians(-80);
                model.leftArm.yRot = (float) Math.toRadians(20);
                model.body.xRot = (float) Math.toRadians(10);
            }
            default -> {}
        }
    }
}
