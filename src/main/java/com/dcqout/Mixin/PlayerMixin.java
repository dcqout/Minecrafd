package com.dcqout.Mixin;

import com.dcqout.Items.IShortSword;
import com.dcqout.Items.ShortSword;
import com.dcqout.Packets.Reload;
import com.dcqout.Main.ILiveEntity;
import com.dcqout.Main.IPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.dcqout.Main.registrator.Attributes.MAX_COMBO;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mixin(Player.class) @SuppressWarnings("UnreachableCode")
public abstract class PlayerMixin extends LivingEntity implements IPlayer {
    public final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.withDefaultNamespace("base_attack_damage");
    private int lastcombo = 0; private int combo = 0; private int step = 0;
    @Shadow private int sleepCounter;@Shadow private long lastDayTimeTick;@Shadow private ItemStack lastItemInMainHand;
    @Shadow private int currentImpulseContextResetGraceTime;@Shadow private boolean isEquipped(Item item) {return true;}
    @Shadow private void turtleHelmetTick() {}@Shadow private void moveCloak() {}
    @Shadow @Nullable protected boolean updateIsUnderwater() {return true;}
    @Shadow @Nullable protected void updatePlayerPose() {}
    @Shadow protected void removeEntitiesOnShoulder() {}

    @Override
    public int getCombo() {
        return this.combo;
    }
    @Override
    public void setCombo(int n) {
        this.combo = n;
    }
    @Override
    public void addCombo(int  n) {
        if (n > 0) {
            this.combo += n;
            return;
        }
        this.combo += 1;
    }
    @Override
    public void resetAttackTicker() {
        this.attackStrengthTicker = 0;
    }
    @Overwrite public void resetAttackStrengthTicker() {} //who is calling this?

    protected PlayerMixin(EntityType<? extends LivingEntity> plr, Level lvl) {
        super(plr, lvl);
    }

    @Overwrite
    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.1F)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.LUCK)
                .add(Attributes.BLOCK_INTERACTION_RANGE, 4.5)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 3.0)
                .add(Attributes.BLOCK_BREAK_SPEED)
                .add(Attributes.SUBMERGED_MINING_SPEED)
                .add(Attributes.SNEAKING_SPEED)
                .add(Attributes.MINING_EFFICIENCY)
                .add(Attributes.SWEEPING_DAMAGE_RATIO)
                .add(net.neoforged.neoforge.common.NeoForgeMod.CREATIVE_FLIGHT)
                .add(MAX_COMBO);
    }

    private float getlocalKnockback(Entity attacker, DamageSource damageSource) {
        float f = (float)((LivingEntity)(Object)this).getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        Level var5 = ((LivingEntity)(Object)this).level();
        float var10000;
        if (var5 instanceof ServerLevel serverlevel) {
            var10000 = EnchantmentHelper.modifyKnockback(serverlevel, ((LivingEntity)(Object)this).getWeaponItem(), attacker, damageSource, f);
        } else {
            var10000 = f;
        }
        return var10000;
    }
    private double compute(List<ItemAttributeModifiers.Entry> modifiers) {
        for (ItemAttributeModifiers.Entry itemattributemodifiers$entry : modifiers) {
            if (itemattributemodifiers$entry.modifier().id().getPath().equals(ATTACK_DAMAGE_ID.getPath())) {
                return itemattributemodifiers$entry.modifier().amount();
            }
        }
        return 0.0f;
    }
    private boolean canPlayerBeDamaged(Player plr, ServerLevel slvl, DamageSource src) {
        if (plr.isInvulnerableTo(slvl, src)) {
            return false;
        } else if (plr.getAbilities().invulnerable && !src.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        return true;
    }

    @Overwrite
    public void disableShield(ItemStack stack) {
        Player plr_cast = (Player)(Object)this;
        plr_cast.getCooldowns().addCooldown(stack, 100);
        plr_cast.stopUsingItem();
        plr_cast.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,90));
        plr_cast.level().broadcastEntityEvent(this, (byte)30);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        Player caster =(Player) (Object)this;
        if (!canPlayerBeDamaged(caster,serverLevel,source)) {
            return false;
        } else {
            this.noActionTime = 0;
            if (this.isDeadOrDying()) {
                return false;
            } else {
                removeEntitiesOnShoulder();
                amount = Math.max(0.0F, source.type().scaling().getScalingFunction().scaleDamage(source, caster, amount, caster.level().getDifficulty()));
                return amount == 0.0F ? false : super.hurtServer(serverLevel, source, amount);
            }
        }
    }

    @Overwrite
    public void attack(Entity target) {
        Player plr_cast = (Player)(Object)this;
        if (!net.neoforged.neoforge.common.CommonHooks.onPlayerAttackTarget(plr_cast, target)) return;
        if (target.isAttackable()) {
            if (!target.skipAttackInteraction(plr_cast)) {

                AttributeInstance att = plr_cast.getAttribute(Attributes.ATTACK_DAMAGE);
                ItemStack itemstack = plr_cast.getWeaponItem();
                boolean reset = false;
                boolean flag10 = itemstack.getItem() instanceof ShortSword;
                if (flag10) {
                    if (plr_cast.getAttackStrengthScale(0.0F) < 1) {
                        return;
                    }
                } else {
                    reset = true;
                }
                double item_dmg = compute(itemstack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers());
                double dmg = att.hasModifier(ATTACK_DAMAGE_ID) ? plr_cast.getAttribute(Attributes.ATTACK_DAMAGE).getModifier(ATTACK_DAMAGE_ID).amount() : item_dmg;
                float f = plr_cast.isAutoSpinAttack() ? autoSpinAttackDmg : (float) (plr_cast.getAttributeValue(Attributes.ATTACK_DAMAGE)-(dmg-item_dmg == 0.0d ? 0.0d : dmg-item_dmg));
                if (flag10) { f += ((ShortSword)itemstack.getItem())
                        .getBonusDamage(((IPlayer) plr_cast).getCombo()+1,plr_cast.getAttributeValue(MAX_COMBO),item_dmg); }
                DamageSource damagesource = Optional.ofNullable(itemstack.getItem().getDamageSource(plr_cast)).orElse(plr_cast.damageSources().playerAttack(plr_cast));
                float f1 = 0.0f; // enchant dmg - f
                //refer.LOGGER.warn("f1: "+String.valueOf(f));
                float f2 = plr_cast.getAttackStrengthScale(0.5F);
                f *= 0.2F + f2 * f2 * 0.8F;
                f1 *= f2;
                if (target.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE)
                        && target instanceof Projectile projectile
                        && projectile.deflect(ProjectileDeflection.AIM_DEFLECT, plr_cast, plr_cast, true)) {
                    plr_cast.level().playSound(null, plr_cast.getX(), plr_cast.getY(), plr_cast.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, plr_cast.getSoundSource());
                    return;
                }
                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag3 = f2 > 0.9F;
                    boolean flag;
                    if (plr_cast.isSprinting() && flag3) {
                        plr_cast.level()
                                .playSound(null, plr_cast.getX(), plr_cast.getY(), plr_cast.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, plr_cast.getSoundSource(), 1.0F, 1.0F);
                        flag = true;
                    } else {
                        flag = false;
                    }

                    f += itemstack.getItem().getAttackDamageBonus(target, f, damagesource);
                    boolean flag1 = flag3
                            && plr_cast.fallDistance > 0.0F
                            && !plr_cast.onGround()
                            && !plr_cast.onClimbable()
                            && !plr_cast.isInWater()
                            && !plr_cast.hasEffect(MobEffects.BLINDNESS)
                            && !plr_cast.isPassenger()
                            && target instanceof LivingEntity
                            && !plr_cast.isSprinting();
                    // Neo: Fire the critical hit event and override the critical hit status and damage multiplier based on the event.
                    // The boolean local above (flag1) is the vanilla critical hit result.
                    var critEvent = net.neoforged.neoforge.common.CommonHooks.fireCriticalHit(plr_cast, target, flag1, flag1 ? 1.5F : 1.0F);
                    flag1 = critEvent.isCriticalHit();
                    if (flag1) {
                        f *= critEvent.getDamageMultiplier();
                    }

                    float f3 = f + f1;
                    boolean flag2 = false;
                    // Neo: Replace !flag1 (!isCriticalHit) with the logic from the CriticalHitEvent.
                    boolean critBlocksSweep = critEvent.isCriticalHit() && critEvent.disableSweep();
                    if (flag3 && !critBlocksSweep && !flag && plr_cast.onGround()) {
                        double d0 = plr_cast.getKnownMovement().horizontalDistanceSqr();
                        double d1 = (double)plr_cast.getSpeed() * 2.5;
                        // Neo: Make sweep attacks check SWORD_SWEEP instead of instanceof SwordItem.
                        if (d0 < Mth.square(d1) && plr_cast.getItemInHand(InteractionHand.MAIN_HAND).canPerformAction(net.neoforged.neoforge.common.ItemAbilities.SWORD_SWEEP)) {
                            flag2 = true;
                        }
                    }

                    // Neo: Fire the SweepAttackEvent and overwrite the value of flag2 (the local controlling if a sweep will occur).
                    var sweepEvent = net.neoforged.neoforge.common.CommonHooks.fireSweepAttack(plr_cast, target, flag2);
                    flag2 = sweepEvent.isSweeping();

                    float f6 = 0.0F;
                    if (target instanceof LivingEntity livingentity) {
                        f6 = livingentity.getHealth();
                    }
                    Vec3 vec3 = target.getDeltaMovement();

                    boolean flag4 = false;
                    boolean flag11 = false;

                    if (!plr_cast.level().isClientSide && flag10) {
                        LivingShieldBlockEvent ev = ((ILiveEntity)target).getShieldEvent(damagesource,f3);
                        if (ev.getBlocked() && damagesource.getDirectEntity() instanceof LivingEntity dmg_source) {
                            ((ILiveEntity)target).blockshield(dmg_source,ev.shieldDamage());
                            flag11 = true;
                            ((IShortSword)itemstack.getItem()).hashurt(plr_cast);
                            reset = false;
                            if (((IPlayer) plr_cast).getCombo() == (int) plr_cast.getAttributeValue(MAX_COMBO)) {
                                ((IPlayer)plr_cast).setCombo(0);
                                this.step = 0;
                                PacketDistributor.sendToPlayer((ServerPlayer) plr_cast,new Reload(0));
                                reset = true;
                            }
                        } else {
                            flag4 = target.hurtOrSimulate(damagesource,f3);
                        }
                    } else {
                        flag4 = target.hurtOrSimulate(damagesource, f3);
                    }
                    if (flag4) {
                        //refer.LOGGER.warn("f3: "+String.valueOf(f3));
                        if (!flag11 && flag10 && !plr_cast.level().isClientSide) {
                            ((IShortSword)itemstack.getItem()).hashurt(plr_cast);
                            reset = false;
                            if (((IPlayer) plr_cast).getCombo() == (int) plr_cast.getAttributeValue(MAX_COMBO)) {
                                ((IPlayer)plr_cast).setCombo(0);
                                this.step = 0;
                                PacketDistributor.sendToPlayer((ServerPlayer) plr_cast,new Reload(0));
                                reset = true;
                            }
                        }
                        float f4 = getlocalKnockback(target, damagesource) + (flag ? 1.0F : 0.0F);
                        if (f4 > 0.0F) {
                            if (target instanceof LivingEntity livingentity1) {
                                livingentity1.knockback(
                                        (double)(f4 * 0.5F),
                                        (double)Mth.sin(plr_cast.getYRot() * (float) (Math.PI / 180.0)),
                                        (double)(-Mth.cos(plr_cast.getYRot() * (float) (Math.PI / 180.0)))
                                );
                            } else {
                                target.push(
                                        (double)(-Mth.sin(plr_cast.getYRot() * (float) (Math.PI / 180.0)) * f4 * 0.5F),
                                        0.1,
                                        (double)(Mth.cos(plr_cast.getYRot() * (float) (Math.PI / 180.0)) * f4 * 0.5F)
                                );
                            }

                            plr_cast.setDeltaMovement(plr_cast.getDeltaMovement().multiply(0.6, 1.0, 0.6));
                            plr_cast.setSprinting(false);
                        }

                        if (flag2) {
                            float f7 = 1.0F + (float)plr_cast.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * f;

                            for (LivingEntity livingentity2 : plr_cast.level()
                                    .getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(1.0, 0.25, 1.0))) {
                                double entityReachSq = Mth.square(plr_cast.entityInteractionRange()); // Use entity reach instead of constant 9.0. Vanilla uses bottom center-to-center checks here, so don't update this to use canReach, since it uses closest-corner checks.
                                if (livingentity2 != plr_cast
                                        && livingentity2 != target
                                        && !plr_cast.isAlliedTo(livingentity2)
                                        && (!(livingentity2 instanceof ArmorStand) || !((ArmorStand)livingentity2).isMarker())
                                        && plr_cast.distanceToSqr(livingentity2) < entityReachSq) {
                                    float f5 = f7 * f2; // enchant damage f7
                                    livingentity2.knockback(
                                            0.4F,
                                            (double)Mth.sin(plr_cast.getYRot() * (float) (Math.PI / 180.0)),
                                            (double)(-Mth.cos(plr_cast.getYRot() * (float) (Math.PI / 180.0)))
                                    );
                                    livingentity2.hurt(damagesource, f5);
                                    if (plr_cast.level() instanceof ServerLevel serverlevel) {
                                        EnchantmentHelper.doPostAttackEffects(serverlevel, livingentity2, damagesource);
                                    }
                                }
                            }

                            plr_cast.level()
                                    .playSound(null, plr_cast.getX(), plr_cast.getY(), plr_cast.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, plr_cast.getSoundSource(), 1.0F, 1.0F);
                            plr_cast.sweepAttack();
                        }

                        if (target instanceof ServerPlayer && target.hurtMarked) {
                            ((ServerPlayer)target).connection.send(new ClientboundSetEntityMotionPacket(target));
                            target.hurtMarked = false;
                            target.setDeltaMovement(vec3);
                        }

                        if (flag1) {
                            plr_cast.level()
                                    .playSound(null, plr_cast.getX(), plr_cast.getY(), plr_cast.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, plr_cast.getSoundSource(), 1.0F, 1.0F);
                            plr_cast.crit(target);
                        }

                        if (!flag1 && !flag2) {
                            if (flag3) {
                                plr_cast.level()
                                        .playSound(null, plr_cast.getX(), plr_cast.getY(), plr_cast.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, plr_cast.getSoundSource(), 1.0F, 1.0F);
                            } else {
                                plr_cast.level()
                                        .playSound(null, plr_cast.getX(), plr_cast.getY(), plr_cast.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, plr_cast.getSoundSource(), 1.0F, 1.0F);
                            }
                        }

                        if (f1 > 0.0F) {
                            plr_cast.magicCrit(target);
                        }

                        plr_cast.setLastHurtMob(target);
                        Entity entity = target;
                        if (target instanceof net.neoforged.neoforge.entity.PartEntity) {
                            entity = ((net.neoforged.neoforge.entity.PartEntity<?>) target).getParent();
                        }

                        boolean flag5 = false;
                        ItemStack copy = itemstack.copy();
                        if (plr_cast.level() instanceof ServerLevel serverlevel1) {
                            if (entity instanceof LivingEntity livingentity3) {
                                flag5 = itemstack.hurtEnemy(livingentity3, plr_cast);
                            }

                            EnchantmentHelper.doPostAttackEffects(serverlevel1, target, damagesource);
                        }

                        if (!plr_cast.level().isClientSide && !itemstack.isEmpty() && entity instanceof LivingEntity) {
                            if (flag5) {
                                itemstack.postHurtEnemy((LivingEntity)entity, plr_cast);
                            }

                            if (itemstack.isEmpty()) {
                                net.neoforged.neoforge.event.EventHooks.onPlayerDestroyItem(plr_cast, copy, itemstack == plr_cast.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
                                if (itemstack == plr_cast.getMainHandItem()) {
                                    plr_cast.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                                } else {
                                    plr_cast.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                                }
                            }
                        }

                        if (target instanceof LivingEntity) {
                            float f8 = f6 - ((LivingEntity)target).getHealth();
                            plr_cast.awardStat(Stats.DAMAGE_DEALT, Math.round(f8 * 10.0F));
                            if (plr_cast.level() instanceof ServerLevel && f8 > 2.0F) {
                                int i = (int)((double)f8 * 0.5);
                                ((ServerLevel)plr_cast.level())
                                        .sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5), target.getZ(), i, 0.1, 0.0, 0.1, 0.2);
                            }
                        }

                        plr_cast.causeFoodExhaustion(0.1F);
                    } else {
                        plr_cast.level()
                                .playSound(null, plr_cast.getX(), plr_cast.getY(), plr_cast.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, plr_cast.getSoundSource(), 1.0F, 1.0F);
                    }
                }
                if (reset) {
                    this.resetAttackTicker(); // FORGE: Moved from beginning of attack() so that getAttackStrengthScale() returns an accurate value during all attack events
                }
            }
        }
    }

    @Overwrite
    public void tick() {
        Player plr_cast = (Player)(Object)this;
        net.neoforged.neoforge.event.EventHooks.firePlayerTickPre(plr_cast);
        plr_cast.noPhysics = plr_cast.isSpectator();
        if (plr_cast.isSpectator() || plr_cast.isPassenger()) {
            plr_cast.setOnGround(false);
        }

        if (plr_cast.takeXpDelay > 0) {
            plr_cast.takeXpDelay--;
        }

        if (plr_cast.isSleeping()) {
            sleepCounter++;
            if (sleepCounter > 100) {
                sleepCounter = 100;
            }

            if (!this.level().isClientSide && !net.neoforged.neoforge.event.EventHooks.canEntityContinueSleeping(this, this.level().isDay() ? Player.BedSleepingProblem.NOT_POSSIBLE_NOW : null)) {
                plr_cast.stopSleepInBed(false, true);
            }
        } else if (sleepCounter > 0) {
            sleepCounter++;
            if (sleepCounter >= 110) {
                sleepCounter = 0;
            }
        }

        updateIsUnderwater();
        if (this.combo > 0) {
            if (this.combo != this.lastcombo) {
                this.lastcombo = this.combo;
                this.step = this.tickCount;
            }
            if (this.tickCount - this.step > 50) {
                this.combo = 0;
                this.lastcombo = 0;
                this.step = 0;
            }
        }

        super.tick();
        if (!this.level().isClientSide && plr_cast.containerMenu != null && !plr_cast.containerMenu.stillValid(plr_cast)) {
            plr_cast.closeContainer();
            plr_cast.containerMenu = plr_cast.inventoryMenu;
        }

        moveCloak();
        if (plr_cast instanceof ServerPlayer serverplayer) {
            plr_cast.getFoodData().tick(serverplayer);
            plr_cast.awardStat(Stats.PLAY_TIME);
            plr_cast.awardStat(Stats.TOTAL_WORLD_TIME);
            if (plr_cast.isAlive()) {
                plr_cast.awardStat(Stats.TIME_SINCE_DEATH);
            }

            if (plr_cast.isDiscrete()) {
                plr_cast.awardStat(Stats.CROUCH_TIME);
            }

            if (!plr_cast.isSleeping()) {
                // Neo: Advance TIME_SINCE_REST if (a) vanilla daytime handling in effect, or (b) days are shorter, or (c) dayTime has ticked, or (d) dayTime advances are off and we need to ignore day length
                if (level().getDayTimeFraction() < 0 || level().getDayTimeFraction() >= 1 || lastDayTimeTick != level().getDayTime() || !serverplayer.serverLevel().getGameRules().getRule(GameRules.RULE_DAYLIGHT).get()) {
                    lastDayTimeTick = level().getDayTime();
                    plr_cast.awardStat(Stats.TIME_SINCE_REST);
                }
            }
        }

        int i = 29999999;
        double d0 = Mth.clamp(this.getX(), -2.9999999E7, 2.9999999E7);
        double d1 = Mth.clamp(this.getZ(), -2.9999999E7, 2.9999999E7);
        if (d0 != this.getX() || d1 != this.getZ()) {
            this.setPos(d0, this.getY(), d1);
        }

        this.attackStrengthTicker++;
        ItemStack itemstack = plr_cast.getMainHandItem();
        if (!ItemStack.matches(lastItemInMainHand, itemstack)) {
            if (!ItemStack.isSameItem(lastItemInMainHand, itemstack)) {
                this.resetAttackTicker();
            }

            lastItemInMainHand = itemstack.copy();
        }

        if (!plr_cast.isEyeInFluid(FluidTags.WATER) && isEquipped(Items.TURTLE_HELMET)) {
            turtleHelmetTick();
        }

        plr_cast.getCooldowns().tick();
        updatePlayerPose();
        if (currentImpulseContextResetGraceTime > 0) {
            currentImpulseContextResetGraceTime--;
        }
        net.neoforged.neoforge.event.EventHooks.firePlayerTickPost(plr_cast);
    }
}
