package com.dcqout.Mixin;

import com.dcqout.Main.ILiveEntity;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import net.dcqmod.refer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

@Mixin(LivingEntity.class) @SuppressWarnings("UnreachableCode")
public abstract class LivingEntityMixin extends Entity implements Attackable, ILivingEntityExtension, ILiveEntity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    private LivingShieldBlockEvent lastEv;
    @Shadow private DamageSource lastDamageSource; @Shadow private long lastDamageStamp;
    @Shadow private boolean checkTotemDeathProtection(DamageSource damageSource) {return true;}

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot var1);
    @Shadow public Collection<MobEffectInstance> getActiveEffects() {return null;}

    @Shadow protected SoundEvent getDeathSound() {return null;}
    @Shadow protected void playHurtSound(DamageSource source) {}
    @Shadow protected void resolveMobResponsibleForDamage(DamageSource damageSource) {}
    @Shadow protected Player resolvePlayerResponsibleForDamage(DamageSource damageSource) {return null;}
    @Shadow protected void hurtHelmet(DamageSource damageSource, float damageAmount) {}
    @Shadow protected int noActionTime;
    @Shadow protected float lastHurt;
    @Shadow protected void actuallyHurt(ServerLevel level, DamageSource damageSource, float amount) {}
    @Shadow protected Stack<DamageContainer> damageContainers;
    @Shadow protected void hurtCurrentlyUsedShield(float damageAmount) {}
    @Shadow protected void blockUsingShield(LivingEntity attacker) {}


    @Shadow public boolean isDamageSourceBlocked(DamageSource damageSource) {return false;}

    @Override
    public void blockshield(LivingEntity livingEntity, float shield_dmg) {
        if (shield_dmg > 0.0f) {
            this.hurtCurrentlyUsedShield(shield_dmg);
        }
        this.blockUsingShield(livingEntity);
    }

    @Nullable @Override
    public LivingShieldBlockEvent getShieldEvent(DamageSource dmg_source, float amount) {
        if (!dmg_source.is(DamageTypeTags.IS_PROJECTILE)) {
            LivingEntity entity_cast = (LivingEntity) (Object) this;
            this.damageContainers.push(new DamageContainer(dmg_source,amount));
            LivingShieldBlockEvent ev = CommonHooks.onDamageBlock(entity_cast,(DamageContainer)this.damageContainers.peek(), entity_cast.isDamageSourceBlocked(dmg_source));
            this.lastEv = ev;
            return ev;
        }
        return null;
    }

    @Override
    public boolean shieldSimulate(ServerLevel slvl, DamageSource dmg_source, LivingShieldBlockEvent ev, float amount) {
        LivingEntity entity_cast = (LivingEntity) (Object) this;
            if (CommonHooks.onEntityIncomingDamage(entity_cast, (DamageContainer)this.damageContainers.peek())) {return false;
            } else {
                if (entity_cast.isSleeping()) {
                    entity_cast.stopSleeping();
                }
                this.noActionTime = 0;
                amount = ((DamageContainer)this.damageContainers.peek()).getNewDamage();
                if (amount < 0.0F) {
                    amount = 0.0F;
                }
                float f = amount;
                boolean flag = false;
                float f1 = 0.0F;
                if (amount > 0.0F && ev.getBlocked()) {
                    ((DamageContainer)this.damageContainers.peek()).setBlockedDamage(ev);
                    if (ev.shieldDamage() > 0.0F) {
                        this.hurtCurrentlyUsedShield(ev.shieldDamage());
                    }
                    f1 = ev.getBlockedDamage();
                    amount = ev.getDamageContainer().getNewDamage();
                    if (!dmg_source.is(DamageTypeTags.IS_PROJECTILE)) {
                        Entity var9 = dmg_source.getDirectEntity();
                        if (var9 instanceof LivingEntity) {
                            LivingEntity livingentity = (LivingEntity)var9;
                            this.blockUsingShield(livingentity);
                        }
                    }
                    flag = amount <= 0.0F;
                }
                if (dmg_source.is(DamageTypeTags.IS_FREEZING) && entity_cast.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                    amount *= 5.0F;
                }
                if (dmg_source.is(DamageTypeTags.DAMAGES_HELMET) && !entity_cast.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                    this.hurtHelmet(dmg_source, amount);
                    amount *= 0.75F;
                }
                entity_cast.walkAnimation.setSpeed(1.5F);
                if (Float.isNaN(amount) || Float.isInfinite(amount)) {
                    amount = Float.MAX_VALUE;
                }
                ((DamageContainer)this.damageContainers.peek()).setNewDamage(amount);
                boolean flag1 = true;
                if ((float)entity_cast.invulnerableTime > 10.0F && !dmg_source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
                    if (amount <= this.lastHurt) {
                        this.damageContainers.pop();
                        return false;
                    }
                    this.actuallyHurt(slvl, dmg_source, amount - this.lastHurt);
                    this.lastHurt = amount;
                    flag1 = false;
                } else {
                    this.lastHurt = amount;
                    entity_cast.invulnerableTime = ((DamageContainer)this.damageContainers.peek()).getPostAttackInvulnerabilityTicks();
                    this.actuallyHurt(slvl, dmg_source, amount);
                    entity_cast.hurtDuration = 10;
                    entity_cast.hurtTime = entity_cast.hurtDuration;
                }
                amount = ((DamageContainer)this.damageContainers.peek()).getNewDamage();
                this.resolveMobResponsibleForDamage(dmg_source);
                this.resolvePlayerResponsibleForDamage(dmg_source);
                if (flag1) {
                    if (flag) {
                        slvl.broadcastEntityEvent(entity_cast, (byte)29);
                    } else {
                        slvl.broadcastDamageEvent(entity_cast, dmg_source);
                    }

                    if (!dmg_source.is(DamageTypeTags.NO_IMPACT) && (!flag || amount > 0.0F)) {
                        this.markHurt();
                    }

                    if (!dmg_source.is(DamageTypeTags.NO_KNOCKBACK)) {
                        double d1 = 0.0;
                        double d0 = 0.0;
                        Entity var14 = dmg_source.getDirectEntity();
                        if (var14 instanceof Projectile) {
                            Projectile projectile = (Projectile)var14;
                            DoubleDoubleImmutablePair doubledoubleimmutablepair = projectile.calculateHorizontalHurtKnockbackDirection(entity_cast, dmg_source);
                            d1 = -doubledoubleimmutablepair.leftDouble();
                            d0 = -doubledoubleimmutablepair.rightDouble();
                        } else if (dmg_source.getSourcePosition() != null) {
                            d1 = dmg_source.getSourcePosition().x() - entity_cast.getX();
                            d0 = dmg_source.getSourcePosition().z() - entity_cast.getZ();
                        }

                        entity_cast.knockback(0.4000000059604645, d1, d0);
                        if (!flag) {
                            entity_cast.indicateDamage(d1, d0);
                        }
                    }
                }
                if (entity_cast.isDeadOrDying()) {
                    if (!this.checkTotemDeathProtection(dmg_source)) {
                        if (flag1) {
                            entity_cast.makeSound(this.getDeathSound());
                        }

                        entity_cast.die(dmg_source);
                    }
                } else if (flag1) {
                    this.playHurtSound(dmg_source);
                }
                boolean flag2 = !flag || amount > 0.0F;
                if (flag2) {
                    lastDamageSource = dmg_source;
                    lastDamageStamp = entity_cast.level().getGameTime();
                    Iterator var10 = entity_cast.getActiveEffects().iterator();

                    while(var10.hasNext()) {
                        MobEffectInstance mobeffectinstance = (MobEffectInstance)var10.next();
                        mobeffectinstance.onMobHurt(slvl, entity_cast, dmg_source, amount);
                    }
                }
                ServerPlayer serverplayer1;
                if (entity_cast instanceof ServerPlayer serverplayer2) {
                    serverplayer1 = serverplayer2;
                    CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(serverplayer1, dmg_source, f, amount, flag);
                    if (f1 > 0.0F && f1 < 3.4028235E37F) {
                        serverplayer1.awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f1 * 10.0F));
                        refer.LOGGER.warn("Shield");
                    }
                }
                Entity var20 = dmg_source.getEntity();
                if (var20 instanceof ServerPlayer) {
                    serverplayer1 = (ServerPlayer)var20;
                    CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(serverplayer1, entity_cast, dmg_source, f, amount, flag);
                }
                this.damageContainers.pop();
                return flag2;
            }
    }

    @Overwrite @SuppressWarnings("UnreachableCode")
    public boolean hurtServer(ServerLevel slvl, DamageSource dmg_source, float amount) {
        LivingEntity entity_cast = (LivingEntity) (Object) this;
        if (entity_cast.isInvulnerableTo(slvl, dmg_source)) {
            return false;
        } else if (entity_cast.isDeadOrDying()) {
            return false;
        } else if (dmg_source.is(DamageTypeTags.IS_FIRE) && entity_cast.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        } else {

            if ((!this.damageContainers.isEmpty()) && this.damageContainers.peek().getSource().equals(dmg_source)) {
                return shieldSimulate(slvl,dmg_source,this.lastEv,amount);
            } else {
                this.damageContainers.push(new DamageContainer(dmg_source,amount));
            }
            if (CommonHooks.onEntityIncomingDamage(entity_cast, (DamageContainer)this.damageContainers.peek())) {
                return false;
            } else {
                if (entity_cast.isSleeping()) {
                    entity_cast.stopSleeping();
                }

                this.noActionTime = 0;
                amount = ((DamageContainer)this.damageContainers.peek()).getNewDamage();
                if (amount < 0.0F) {
                    amount = 0.0F;
                }

                float f = amount;
                boolean flag = false;
                float f1 = 0.0F;
                LivingShieldBlockEvent ev;
                if (amount > 0.0F && (ev = CommonHooks.onDamageBlock(entity_cast, (DamageContainer)this.damageContainers.peek(), entity_cast.isDamageSourceBlocked(dmg_source))).getBlocked()) {
                    ((DamageContainer)this.damageContainers.peek()).setBlockedDamage(ev);
                    if (ev.shieldDamage() > 0.0F) {
                        this.hurtCurrentlyUsedShield(ev.shieldDamage());
                    }

                    f1 = ev.getBlockedDamage();
                    amount = ev.getDamageContainer().getNewDamage();
                    if (!dmg_source.is(DamageTypeTags.IS_PROJECTILE)) {
                        Entity var9 = dmg_source.getDirectEntity();
                        if (var9 instanceof LivingEntity) {
                            LivingEntity livingentity = (LivingEntity)var9;
                            this.blockUsingShield(livingentity);
                        }
                    }

                    flag = amount <= 0.0F;
                }

                if (dmg_source.is(DamageTypeTags.IS_FREEZING) && entity_cast.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                    amount *= 5.0F;
                }

                if (dmg_source.is(DamageTypeTags.DAMAGES_HELMET) && !entity_cast.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                    this.hurtHelmet(dmg_source, amount);
                    amount *= 0.75F;
                }

                entity_cast.walkAnimation.setSpeed(1.5F);
                if (Float.isNaN(amount) || Float.isInfinite(amount)) {
                    amount = Float.MAX_VALUE;
                }

                ((DamageContainer)this.damageContainers.peek()).setNewDamage(amount);
                boolean flag1 = true;
                if ((float)entity_cast.invulnerableTime > 10.0F && !dmg_source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
                    if (amount <= this.lastHurt) {
                        this.damageContainers.pop();
                        return false;
                    }

                    this.actuallyHurt(slvl, dmg_source, amount - this.lastHurt);
                    this.lastHurt = amount;
                    flag1 = false;
                } else {
                    this.lastHurt = amount;
                    entity_cast.invulnerableTime = ((DamageContainer)this.damageContainers.peek()).getPostAttackInvulnerabilityTicks();
                    this.actuallyHurt(slvl, dmg_source, amount);
                    entity_cast.hurtDuration = 10;
                    entity_cast.hurtTime = entity_cast.hurtDuration;
                }

                amount = ((DamageContainer)this.damageContainers.peek()).getNewDamage();
                this.resolveMobResponsibleForDamage(dmg_source);
                this.resolvePlayerResponsibleForDamage(dmg_source);
                if (flag1) {
                    if (flag) {
                        slvl.broadcastEntityEvent(entity_cast, (byte)29);
                    } else {
                        slvl.broadcastDamageEvent(entity_cast, dmg_source);
                    }

                    if (!dmg_source.is(DamageTypeTags.NO_IMPACT) && (!flag || amount > 0.0F)) {
                        this.markHurt();
                    }

                    if (!dmg_source.is(DamageTypeTags.NO_KNOCKBACK)) {
                        double d1 = 0.0;
                        double d0 = 0.0;
                        Entity var14 = dmg_source.getDirectEntity();
                        if (var14 instanceof Projectile) {
                            Projectile projectile = (Projectile)var14;
                            DoubleDoubleImmutablePair doubledoubleimmutablepair = projectile.calculateHorizontalHurtKnockbackDirection(entity_cast, dmg_source);
                            d1 = -doubledoubleimmutablepair.leftDouble();
                            d0 = -doubledoubleimmutablepair.rightDouble();
                        } else if (dmg_source.getSourcePosition() != null) {
                            d1 = dmg_source.getSourcePosition().x() - entity_cast.getX();
                            d0 = dmg_source.getSourcePosition().z() - entity_cast.getZ();
                        }

                        entity_cast.knockback(0.4000000059604645, d1, d0);
                        if (!flag) {
                            entity_cast.indicateDamage(d1, d0);
                        }
                    }
                }

                if (entity_cast.isDeadOrDying()) {
                    if (!this.checkTotemDeathProtection(dmg_source)) {
                        if (flag1) {
                            entity_cast.makeSound(this.getDeathSound());
                        }

                        entity_cast.die(dmg_source);
                    }
                } else if (flag1) {
                    this.playHurtSound(dmg_source);
                }

                boolean flag2 = !flag || amount > 0.0F;
                if (flag2) {
                    lastDamageSource = dmg_source;
                    lastDamageStamp = entity_cast.level().getGameTime();
                    Iterator var10 = entity_cast.getActiveEffects().iterator();

                    while(var10.hasNext()) {
                        MobEffectInstance mobeffectinstance = (MobEffectInstance)var10.next();
                        mobeffectinstance.onMobHurt(slvl, entity_cast, dmg_source, amount);
                    }
                }

                ServerPlayer serverplayer1;
                if (entity_cast instanceof ServerPlayer serverplayer2) {
                    serverplayer1 = serverplayer2;
                    CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(serverplayer1, dmg_source, f, amount, flag);
                    if (f1 > 0.0F && f1 < 3.4028235E37F) {
                        serverplayer1.awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f1 * 10.0F));
                        refer.LOGGER.warn("Shield");
                    }
                }

                Entity var20 = dmg_source.getEntity();
                if (var20 instanceof ServerPlayer) {
                    serverplayer1 = (ServerPlayer)var20;
                    CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(serverplayer1, entity_cast, dmg_source, f, amount, flag);
                }

                this.damageContainers.pop();
                return flag2;
            }
        }
    }

}
