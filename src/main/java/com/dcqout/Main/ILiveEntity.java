package com.dcqout.Main;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

public interface ILiveEntity {
    LivingShieldBlockEvent getShieldEvent(DamageSource dmg_source, float amount);
    boolean shieldSimulate(ServerLevel slvl, DamageSource dmg_source, LivingShieldBlockEvent ev, float amount);
    void blockshield(LivingEntity livingEntity, float shield_dmg);
}
