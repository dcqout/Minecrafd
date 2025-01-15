package com.dcqout.Items;

import com.dcqout.Main.ToolMaterialW;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.HashMap;

public class Hammer extends Item implements IItemSet {

    private final HashMap<String,Integer> tabPos = new HashMap<String,Integer>();

    public Hammer(ToolMaterial material,double attackDamage,double knock, float attackSpeed, Item.Properties properties) {
        super(ToolMaterialW.get(material).applyHammer(properties, attackDamage, knock, attackSpeed));
    }
    public Hammer(HashMap<String,Integer> ntabPos,ToolMaterial material,double attackDamage,double knock, float attackSpeed, Item.Properties properties) {
        super(ToolMaterialW.get(material).applyHammer(properties, attackDamage, knock, attackSpeed));
        tabPos.putAll(ntabPos);
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return stack.getItem() instanceof Hammer;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.getKey().equals(Enchantments.KNOCKBACK) || enchantment.getKey().equals(Enchantments.MENDING)
                || enchantment.getKey().equals(Enchantments.VANISHING_CURSE);
    }

    @Override
    public int getCreativeTabPos(String tab) {
        return tabPos.getOrDefault(tab,0);
    }
}
