package com.vscombatstance.arts.client.animator;

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
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProceduralPlayerAnimator {

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        ComboState state = ComboEngine.getState(player);
        if (!state.isActive()) return;

        CombatStyleCapability.get(player).ifPresent(cap -> {
            CombatStyle style = cap.getActiveStyle();
            if (style == null) return;

            int step = state.getCurrentStep();
            ItemStack scroll = style.getScroll(step);
            if (scroll.isEmpty()) return;

            String techId = ScrollItem.getTechniqueId(scroll);
            Technique tech = TechniqueCatalog.find(techId);
            if (tech == null) return;

            // Если для данного приема активна ключевая анимация playerAnimator,
            // не накладываем процедурные матричные трансформации вручную
            if (PlayerAnimationManager.playTechniqueAnimation(player, tech.getId(), player.getMainHandItem())) {
                return;
            }

            float progress = state.getWindowProgress();
            PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();

            applyProceduralPose(model, tech.getId(), progress);
        });
    }

    private static void applyProceduralPose(PlayerModel<AbstractClientPlayer> model, String techId, float p) {
        switch (techId) {
            case "overhead_slam", "heavy_guillotine" -> {
                // Two-handed high raise and slam down
                if (p < 0.45f) {
                    float windup = Easing.easeOutBack(p / 0.45f);
                    model.rightArm.xRot = -2.8f * windup;
                    model.leftArm.xRot = -2.8f * windup;
                    model.rightArm.zRot = -0.2f * windup;
                    model.leftArm.zRot = 0.2f * windup;
                    model.body.xRot = -0.35f * windup;
                } else if (p < 0.75f) {
                    float strikeP = (p - 0.45f) / 0.30f;
                    float strike = Easing.easeInCubic(strikeP);
                    model.rightArm.xRot = -2.8f + (1.3f - (-2.8f)) * strike;
                    model.leftArm.xRot = -2.8f + (1.3f - (-2.8f)) * strike;
                    model.body.xRot = -0.35f + (0.55f - (-0.35f)) * strike;
                } else {
                    // Hold and recovery
                    model.rightArm.xRot = 1.3f;
                    model.leftArm.xRot = 1.3f;
                    model.body.xRot = 0.55f;
                }
            }
            case "whirlwind" -> {
                // 360 degree spin with arms outstretched horizontally
                float angle = p * (float) (Math.PI * 2.0);
                model.body.yRot = angle;
                model.head.yRot = angle;
                model.rightArm.xRot = 0f;
                model.leftArm.xRot = 0f;
                model.rightArm.zRot = 1.4f;
                model.leftArm.zRot = -1.4f;
            }
            case "flurry" -> {
                // Rapid zig-zag blade slashes
                float wave = (float) Math.sin(p * Math.PI * 6.0);
                model.rightArm.xRot = -1.2f + wave * 0.8f;
                model.rightArm.yRot = wave * 0.6f;
                model.body.yRot = wave * 0.2f;
            }
            case "crescent_cleave" -> {
                // Wide horizontal slash
                float swing = Easing.easeInOutQuad(p);
                model.rightArm.xRot = -1.2f;
                model.rightArm.yRot = -1.4f + swing * 2.8f;
                model.body.yRot = -0.3f + swing * 0.6f;
            }
            case "shield_bash" -> {
                // Shield ram forward
                float ram = Easing.easeOutCubic(p);
                model.leftArm.xRot = -1.5f * ram;
                model.leftArm.yRot = 0.7f * ram;
                model.body.xRot = 0.35f * ram;
            }
            case "fortress_bastion", "deflect_parry" -> {
                // Tight defensive guard
                model.rightArm.xRot = -1.3f;
                model.rightArm.zRot = -0.6f;
                model.leftArm.xRot = -1.4f;
                model.leftArm.zRot = 0.6f;
                model.body.xRot = 0.15f;
            }
            case "piercing_lunge", "phantom_thrust" -> {
                // Linear forward thrust
                float thrust = Easing.easeOutBack(p);
                model.rightArm.xRot = -1.57f * thrust;
                model.rightArm.yRot = -0.2f * thrust;
                model.body.yRot = -0.4f * thrust;
            }
            case "rising_uppercut" -> {
                // Upward swing
                float uppercut = Easing.easeOutBack(p);
                model.rightArm.xRot = 0.5f - uppercut * 2.5f;
                model.body.xRot = -0.3f * uppercut;
            }
            case "vault_leap" -> {
                // Leg tuck jump
                float jump = Easing.easeInOutQuad(p);
                model.rightLeg.xRot = -0.7f * jump;
                model.leftLeg.xRot = -0.7f * jump;
                model.rightArm.xRot = 0.8f * jump;
                model.leftArm.xRot = 0.8f * jump;
            }
            case "disengage" -> {
                // Backflip tuck
                float flip = p * (float) (Math.PI * 2.0);
                model.body.xRot = -flip;
                model.rightLeg.xRot = -0.5f;
                model.leftLeg.xRot = -0.5f;
            }
            case "battle_cry" -> {
                // Chest expand shouting pose
                float roar = (float) Math.sin(p * Math.PI);
                model.rightArm.xRot = -1.8f * roar;
                model.rightArm.zRot = 0.6f * roar;
                model.leftArm.xRot = -1.8f * roar;
                model.leftArm.zRot = -0.6f * roar;
                model.head.xRot = -0.5f * roar;
            }
            case "delayed_feint" -> {
                // Hesitation windup
                model.rightArm.xRot = -2.2f;
                model.body.yRot = -0.4f;
            }
        }
    }
}
