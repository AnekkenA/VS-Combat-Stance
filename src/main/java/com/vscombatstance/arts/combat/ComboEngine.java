package com.vscombatstance.arts.combat;

import com.vscombatstance.arts.adapter.WeightAdapter;
import com.vscombatstance.arts.item.ScrollItem;
import com.vscombatstance.arts.technique.Technique;
import com.vscombatstance.arts.technique.TechniqueCatalog;
import com.vscombatstance.arts.technique.TechniqueType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class ComboEngine {
    private static final Map<UUID, ComboState> PLAYER_STATES = new HashMap<>();

    public static ComboState getState(Player player) {
        return PLAYER_STATES.computeIfAbsent(player.getUUID(), k -> new ComboState());
    }

    public static void tickPlayer(Player player) {
        ComboState state = getState(player);
        if (state.isActive()) {
            state.tick();
        }
    }

    public static boolean triggerKey(Player player, Technique.ActionKey pressedKey) {
        ComboState state = getState(player);
        CombatStyleCapability cap = CombatStyleCapability.get(player);
        if (cap != null) {
            CombatStyle style = cap.getActiveStyle();
            if (style != null && !style.isEmpty()) {
                if (!state.isActive()) {
                    // If not active and key matches slot 0, start combo
                    ItemStack slot0 = style.getScroll(0);
                    if (!slot0.isEmpty()) {
                        String techId = ScrollItem.getTechniqueId(slot0);
                        Technique tech = TechniqueCatalog.find(techId);
                        if (tech != null && (tech.getActionKey() == pressedKey || tech.getActionKey() == Technique.ActionKey.ANY)) {
                            state.startCombo(tech, slot0);
                            executeTechnique(player, tech, slot0, state);
                        }
                    }
                } else {
                    // Currently in combo: check timing window
                    int nextStep = state.getCurrentStep() + 1;
                    if (nextStep >= CombatStyle.SLOTS) {
                        state.reset();
                        return state.isActive();
                    }

                    ItemStack nextScroll = style.getScroll(nextStep);
                    if (nextScroll.isEmpty()) {
                        state.reset();
                        return state.isActive();
                    }

                    String techId = ScrollItem.getTechniqueId(nextScroll);
                    Technique tech = TechniqueCatalog.find(techId);
                    if (tech == null) {
                        state.reset();
                        return state.isActive();
                    }

                    if (state.isInTimingWindow() && (tech.getActionKey() == pressedKey || tech.getActionKey() == Technique.ActionKey.ANY)) {
                        // Successful rhythm hit!
                        state.setCurrentStep(nextStep);
                        state.setupStep(tech, nextScroll);
                        executeTechnique(player, tech, nextScroll, state);
                    } else if (state.getTimer() < state.getWindowStart()) {
                        // Pressed too early -> dropped combo!
                        state.reset();
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 0.7f, 1.5f);
                    }
                }
            }
        }

        return state.isActive();
    }

    private static void executeTechnique(Player player, Technique tech, ItemStack scroll, ComboState state) {
        double rollDmg = ScrollItem.getDamageMult(scroll);
        double rollSpd = ScrollItem.getSpeedMult(scroll);

        // Multiplicative stacking of stats
        state.multiplyDamage(rollDmg);
        state.multiplySpeed(rollSpd);

        ItemStack weapon = player.getMainHandItem();
        double weight = WeightAdapter.getEffectiveWeight(weapon);

        if (tech.getType() == TechniqueType.MARTIAL_ART) {
            executeMartialArt(player, tech, scroll, state, weight);
        } else {
            executeLinker(player, tech, scroll, state, weight);
        }
    }

    private static void executeMartialArt(Player player, Technique tech, ItemStack scroll, ComboState state, double weight) {
        double baseAttackDmg = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double massBonus = WeightAdapter.calculateMassBonus(weight, tech.getMassScaling());
        double totalMult = state.getAccumulatedDamageMult() * massBonus;

        // Airborne downward strike bonus (scales with vault leap levels!)
        if (state.isAirborne()) {
            totalMult *= (1.0 + 0.5 * state.getAirborneLevel());
            state.setAirborneLevel(0);
        }

        // Riposte counter-attack bonus (scales with parry levels!)
        if (state.isRiposteReady()) {
            totalMult *= (1.0 + 0.5 * state.getRiposteLevel());
            state.setRiposteLevel(0);
        }

        float finalDamage = (float) (baseAttackDmg * totalMult);

        Vec3 look = player.getLookAngle();
        Vec3 pos = player.position();
        double radius = tech.getAoeRadius();
        AABB hitBox = player.getBoundingBox().inflate(radius, 1.5, radius);

        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class, hitBox,
                e -> e != player && e.isAlive() && !player.isAlliedTo(e));

        // Feint level 3 ignores armor by using bypassArmor damage source if available
        DamageSource source = (state.getFeintLevel() >= 3)
                ? player.damageSources().magic()
                : player.damageSources().playerAttack(player);

        for (LivingEntity target : targets) {
            Vec3 toTarget = target.position().subtract(pos).normalize();
            double dot = look.dot(toTarget);
            double angleDeg = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, dot))));

            if (angleDeg <= (tech.getAoeAngle() / 2.0)) {
                // Check backstab condition for shadow sever
                if (tech.getId().equals("shadow_sever")) {
                    Vec3 targetLook = target.getLookAngle();
                    if (look.dot(targetLook) > 0.5) {
                        finalDamage *= 2.0f; // True backstab!
                    }
                }

                // Heavy guillotine execute
                if (tech.getId().equals("heavy_guillotine") && target.getHealth() / target.getMaxHealth() <= 0.35f) {
                    finalDamage *= 2.0f;
                }

                // Apply feint guard-break consequences
                if (state.isFeintReady()) {
                    int fLvl = state.getFeintLevel();
                    target.knockback(0.5 * fLvl, -look.x, -look.z);
                    if (fLvl >= 2) {
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60 * fLvl, 1));
                        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60 * fLvl, 0));
                    }
                    state.setFeintLevel(0);
                }

                target.hurt(source, finalDamage);
            }
        }

        // Visual and sound effects
        if (player.level() instanceof ServerLevel serverLevel) {
            spawnMartialParticles(serverLevel, player, tech);
        }
        playMartialSound(player, tech);
    }

    private static void executeLinker(Player player, Technique tech, ItemStack scroll, ComboState state, double weight) {
        int count = state.getConsecutiveCount();
        double rollDmg = ScrollItem.getDamageMult(scroll);
        // Consecutive linkers stack multiplicatively!
        double compoundedScale = Math.pow(rollDmg, Math.max(1, count));

        switch (tech.getId()) {
            case "vault_leap" -> {
                // Jump height scales with count!
                double vertical = 0.58 * compoundedScale;
                player.setDeltaMovement(player.getDeltaMovement().x, vertical, player.getDeltaMovement().z);
                player.hasImpulse = true;
                state.setAirborneLevel(count); // Amplifies subsequent slam by +50% per stack!
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0f, 1.0f + count * 0.15f);
                if (player.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 10 * count, 0.3, 0.1, 0.3, 0.05);
                }
            }
            case "disengage" -> {
                // Backflip distance scales with count!
                Vec3 look = player.getLookAngle();
                double horizontal = 0.75 * compoundedScale;
                player.setDeltaMovement(-look.x * horizontal, 0.42 * (1.0 + count * 0.1), -look.z * horizontal);
                player.hasImpulse = true;
                state.setDisengageLevel(count);
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 40 * count, Math.min(1, count - 1)));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.3f + count * 0.2f);
            }
            case "shadow_step" -> {
                // Teleport distance scales with count!
                Vec3 look = player.getLookAngle();
                double distance = 4.0 * compoundedScale;
                Vec3 targetPos = player.position().add(look.x * distance, 0, look.z * distance);
                player.teleportTo(targetPos.x, targetPos.y, targetPos.z);

                // Level 2+ grants Invisibility so mobs lose tracking!
                if (count >= 2) {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40 * count, 0, false, false));
                }

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8f, 1.2f + count * 0.15f);
                if (player.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 15 * count, 0.4, 0.6, 0.4, 0.1);
                }
            }
            case "battle_cry" -> {
                // Buff tier scales: Speed I, II, III. Pitch becomes deeper and thunderous!
                float pitch = (float) Math.max(0.5, 1.2 - count * 0.2);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(0).value(), SoundSource.PLAYERS, 1.5f, pitch);

                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60 * count, Math.min(2, count - 1)));
                state.multiplyDamage(1.0 + 0.15 * count);

                // Radial sonic knockback shockwave on enemies
                double cryRadius = 4.0 + count * 2.0;
                AABB shockwave = player.getBoundingBox().inflate(cryRadius);
                List<LivingEntity> enemies = player.level().getEntitiesOfClass(LivingEntity.class, shockwave,
                        e -> e != player && e.isAlive() && !player.isAlliedTo(e));

                for (LivingEntity e : enemies) {
                    Vec3 push = e.position().subtract(player.position()).normalize();
                    e.knockback(0.8 * count, -push.x, -push.z);
                    if (count >= 2) {
                        e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60 * count, 0));
                    }
                }

                if (player.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.SONIC_BOOM, player.getX(), player.getY() + 1.2, player.getZ(), count, 0, 0, 0, 0);
                }
            }
            case "delayed_feint" -> {
                // Guard break power: Level 1 (shield drop) -> Level 2 (slowness/stumble) -> Level 3 (bypass 100% armor)
                state.setFeintLevel(count);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ARMOR_EQUIP_CHAIN, SoundSource.PLAYERS, 1.0f, 0.9f);
                if (player.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.ENCHANTED_HIT, player.getX(), player.getY() + 1.2, player.getZ(), 10 * count, 0.3, 0.3, 0.3, 0.1);
                }
            }
            case "deflect_parry" -> {
                // Counter-attack damage tier: Level 1 (+50%) -> Level 2 (+100%) -> Level 3 (+150%)
                state.setRiposteLevel(count);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.5f, 1.4f + count * 0.2f);
                if (player.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.CRIT, player.getX(), player.getY() + 1.2, player.getZ(), 8 * count, 0.3, 0.3, 0.3, 0.1);
                }
            }
        }
    }

    private static void spawnMartialParticles(ServerLevel level, Player player, Technique tech) {
        double px = player.getX();
        double py = player.getY() + 0.8;
        double pz = player.getZ();

        switch (tech.getId()) {
            case "overhead_slam", "earthshaker" ->
                    level.sendParticles(ParticleTypes.EXPLOSION, px, player.getY(), pz, 1, 0, 0, 0, 0);
            case "whirlwind" ->
                    level.sendParticles(ParticleTypes.SWEEP_ATTACK, px, py, pz, 8, 1.5, 0.1, 1.5, 0.0);
            case "flurry", "cross_slash" ->
                    level.sendParticles(ParticleTypes.SWEEP_ATTACK, px, py, pz, 3, 0.5, 0.3, 0.5, 0.0);
            case "piercing_lunge" ->
                    level.sendParticles(ParticleTypes.CRIT, px + player.getLookAngle().x * 2, py, pz + player.getLookAngle().z * 2, 8, 0.2, 0.2, 0.2, 0.1);
            case "shadow_sever" ->
                    level.sendParticles(ParticleTypes.SOUL, px, py, pz, 12, 0.4, 0.4, 0.4, 0.02);
            default ->
                    level.sendParticles(ParticleTypes.CRIT, px, py, pz, 5, 0.5, 0.5, 0.5, 0.1);
        }
    }

    private static void playMartialSound(Player player, Technique tech) {
        var level = player.level();
        var pos = player.position();
        switch (tech.getId()) {
            case "overhead_slam", "heavy_guillotine" ->
                    level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.8f, 1.4f);
            case "whirlwind" ->
                    level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.2f, 0.8f);
            case "flurry" ->
                    level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.9f, 1.6f);
            case "shield_bash" ->
                    level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.2f, 0.8f);
            default ->
                    level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }
}
