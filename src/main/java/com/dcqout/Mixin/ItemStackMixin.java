package com.dcqout.Mixin;

import net.dcqmod.refer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static com.dcqout.Main.registrator.DataComponents.ECHO;

@Mixin(ItemStack.class) @SuppressWarnings("UnreachableCode")
public class ItemStackMixin {

    private int getEchoValue(ItemStack stack) {
        refer.LOGGER.warn("called getval");
        return Mth.clamp((Integer)stack.getOrDefault(ECHO.get(), 0), 0, stack.getMaxDamage());
    }

    private void setEchoValue(ItemStack stack,int damage) {
        refer.LOGGER.warn("called setval, to: "+damage);
        stack.set(ECHO.get(), Mth.clamp(getEchoValue(stack)-damage, 0, stack.getMaxDamage()));
    }

    @Overwrite
    private void applyDamage(int amount, @Nullable LivingEntity p_364853_, Consumer<Item> p_360895_) {
        ItemStack itemStack = (ItemStack) (Object) this;
        if (getEchoValue(itemStack) > 0) {
            setEchoValue(itemStack,amount);
        } else {
            if (p_364853_ instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(serverPlayer, itemStack, amount);
            }
            itemStack.setDamageValue(amount);
        }
        if (itemStack.isBroken()) {
            Item item = itemStack.getItem();
            itemStack.shrink(1);
            p_360895_.accept(item);
        }
    }
}
