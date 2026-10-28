package com.vscombatstance.threats.entity;

import com.vscombatstance.threats.ai.DynamicShieldGoal;
import com.vscombatstance.threats.ai.HarpoonHookGoal;
import com.vscombatstance.threats.ai.LeashTerritoryGoal;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EliteGuardEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Анимационные последовательности Blockbench
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.elite_guard.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.elite_guard.walk");
    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("animation.elite_guard.run");
    private static final RawAnimation HARPOON_ANIM = RawAnimation.begin().thenPlay("animation.elite_guard.harpoon_throw");
    private static final RawAnimation SHIELD_BLOCK_ANIM = RawAnimation.begin().thenPlay("animation.elite_guard.shield_block");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("animation.elite_guard.attack");

    public EliteGuardEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 15;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        // Тактические цели: блок щитом и анти-пиллар гарпун
        this.goalSelector.addGoal(2, new DynamicShieldGoal(this));
        this.goalSelector.addGoal(3, new HarpoonHookGoal(this));
        this.goalSelector.addGoal(4, new LeashTerritoryGoal(this, 32.0));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.25D, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Контроллер передвижения (базовые циклы покоя и бега)
        controllers.add(new AnimationController<>(this, "movement", 5, event -> {
            if (event.isMoving()) {
                if (isSprinting()) {
                    return event.setAndContinue(RUN_ANIM);
                }
                return event.setAndContinue(WALK_ANIM);
            }
            return event.setAndContinue(IDLE_ANIM);
        }));

        // Контроллер тактических приемов и атак с триггерами из AI Goals
        controllers.add(new AnimationController<>(this, "combat", 2, event -> PlayState.STOP)
                .triggerableAnim("harpoon_throw", HARPOON_ANIM)
                .triggerableAnim("shield_block", SHIELD_BLOCK_ANIM)
                .triggerableAnim("attack", ATTACK_ANIM));
    }

    public void triggerHarpoonAnimation() {
        this.triggerAnim("combat", "harpoon_throw");
    }

    public void triggerShieldBlockAnimation() {
        this.triggerAnim("combat", "shield_block");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.VINDICATOR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }
}
