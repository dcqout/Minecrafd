package com.dcqout.Items;

import com.dcqout.Main.IPlayer;
import com.dcqout.Main.ToolMaterialW;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.HashMap;
import java.util.List;

import static com.dcqout.Main.registrator.Attributes.MAX_COMBO;

public class ShortSword extends Item implements IShortSword,IItemSet {

    public static final ResourceLocation COMBO_ID = ResourceLocation.fromNamespaceAndPath("dcq","max_combo");
    public static final ResourceLocation ATTACK_SPEED_ID = ResourceLocation.withDefaultNamespace("base_attack_speed");
    private final HashMap<String,Integer> tabPos = new HashMap<String,Integer>();


    public ShortSword(ToolMaterial material, double attackDamage, float attackSpeed, int maxCombo, Item.Properties properties) {
        super(ToolMaterialW.get(material).applyShortSword(properties, attackDamage, attackSpeed,maxCombo));
    }

    public ShortSword(HashMap<String,Integer> ntabPos,ToolMaterial material, double attackDamage, float attackSpeed, int maxCombo, Item.Properties properties) {
        super(ToolMaterialW.get(material).applyShortSword(properties, attackDamage, attackSpeed,maxCombo));
        tabPos.putAll(ntabPos);
    }

    public ShortSword(Item.Properties properties) {
        super(properties);
    }

    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    public float getBonusDamage(int current, double max, double damage) {
        if (current == max) {
            return (float) ((damage)*((max-1.0d)*2.0d));
        }
        return 0.0f;
    }

    public void postHurtEnemy(ItemStack p_345553_, LivingEntity p_345771_, LivingEntity p_346282_) {
        p_345553_.hurtAndBreak(1, p_346282_, EquipmentSlot.MAINHAND);
    }
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return false;
        // return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(itemAbility);
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        int maxCombo = (int) (attacker.getAttributeBaseValue(MAX_COMBO) + compute(stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()));
        if (attacker instanceof Player plr) {
            return ((IPlayer)plr).getCombo() >= maxCombo-1;
        }
        return false;
    }

    public float compute(List<ItemAttributeModifiers.Entry> modifiers) {
        for (ItemAttributeModifiers.Entry itemattributemodifiers$entry : modifiers) {
            if (itemattributemodifiers$entry.modifier().id().getPath().equals(COMBO_ID.getPath())) {
                return (float) itemattributemodifiers$entry.modifier().amount();
            }
        }
        return 0.0f;
    }

    @Override
    public void hashurt(LivingEntity entity) {
        if (entity instanceof Player plr) {
            ((IPlayer)entity).addCombo(1);
        }
    }

    @Override
    public int getCreativeTabPos(String tab) {
        return tabPos.getOrDefault(tab,0);
    }
}
