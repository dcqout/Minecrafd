package com.dcqout.Main;

import com.dcqout.Items.ShortSword;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Tool.Rule;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

import static com.dcqout.Main.registrator.Attributes.MAX_COMBO;

public record ToolMaterialW(TagKey<Block> incorrectBlocksForDrops, int durability, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Item> repairItems) {
    public static final ToolMaterialW WOOD; public static final ToolMaterialW STONE; public static final ToolMaterialW IRON; public static final ToolMaterialW DIAMOND;
    public static final ToolMaterialW GOLD; public static final ToolMaterialW NETHERITE;

    public ToolMaterialW(TagKey<Block> incorrectBlocksForDrops, int durability, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Item> repairItems) {
        this.incorrectBlocksForDrops = incorrectBlocksForDrops; this.durability = durability; this.speed = speed; this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue; this.repairItems = repairItems;
    }

    public static ToolMaterialW get(ToolMaterial mat) {return new ToolMaterialW(mat.incorrectBlocksForDrops(),mat.durability(),mat.speed(),mat.attackDamageBonus(),mat.enchantmentValue(),mat.repairItems());}
    private Item.Properties applyCommonProperties(Item.Properties properties) {return properties.durability(this.durability).repairable(this.repairItems).enchantable(this.enchantmentValue);}
    private ItemAttributeModifiers createToolAttributes(float attackDamage, float attackSpeed) {return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, (double)(attackDamage + this.attackDamageBonus), Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (double)attackSpeed, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();}

    public Item.Properties applyToolProperties(Item.Properties properties, TagKey<Block> mineableBlocks, float attackDamage, float attackSpeed) {
        HolderGetter<Block> holdergetter = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return this.applyCommonProperties(properties).component(DataComponents.TOOL, new Tool(List.of(Rule.deniesDrops(holdergetter.getOrThrow(this.incorrectBlocksForDrops)), Rule.minesAndDrops(holdergetter.getOrThrow(mineableBlocks), this.speed)), 1.0F, 1)).attributes(this.createToolAttributes(attackDamage, attackSpeed));
    }

    public TagKey<Block> incorrectBlocksForDrops() {return this.incorrectBlocksForDrops;}
    public int durability() {return this.durability;}
    public float speed() {return this.speed;}
    public float attackDamageBonus() {return this.attackDamageBonus;}
    public int enchantmentValue() {return this.enchantmentValue;}
    public TagKey<Item> repairItems() {return this.repairItems;}

    public Item.Properties applyShortSword(Item.Properties properties, double attackDamage, float attackSpeed, int maxcombo) {
        HolderGetter<Block> holdergetter = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return this.applyCommonProperties(properties).component(DataComponents.TOOL, new Tool(List.of(Rule.minesAndDrops(HolderSet.direct(new Holder[]{Blocks.COBWEB.builtInRegistryHolder()}), 15.0F), Rule.overrideSpeed(holdergetter.getOrThrow(BlockTags.SWORD_EFFICIENT), 1.5F)), 1.0F, 2)).attributes(this.createSword(attackDamage, attackSpeed, maxcombo));
    }

    private ItemAttributeModifiers createSword(double attackDamage, float attackSpeed, int maxcombo) {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage, Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED,
                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (double)attackSpeed, Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND).add(MAX_COMBO,
                new AttributeModifier(ShortSword.COMBO_ID,(double)maxcombo, Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND).build();
    }

    public Item.Properties applyHammer(Item.Properties properties, double attackDamage,double knock, float attackSpeed) {
        return this.applyCommonProperties(properties).component(DataComponents.TOOL, new Tool(List.of(Rule.minesAndDrops(HolderSet.direct(new Holder[]{Blocks.ANVIL.builtInRegistryHolder()}),15.0F)),1.0F,2)).attributes(this.createHammer(attackDamage, knock, attackSpeed));
    }

    private ItemAttributeModifiers createHammer(double attackDamage,double knock,float attackSpeed) {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage, Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED,
                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (double)attackSpeed, Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_KNOCKBACK,
                new AttributeModifier(ResourceLocation.withDefaultNamespace("attack_knockback"),knock,Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND).build();
    }

    static {
        WOOD = new ToolMaterialW(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 59, 2.0F, 0.0F, 15, ItemTags.WOODEN_TOOL_MATERIALS);
        STONE = new ToolMaterialW(BlockTags.INCORRECT_FOR_STONE_TOOL, 131, 4.0F, 1.0F, 5, ItemTags.STONE_TOOL_MATERIALS);
        IRON = new ToolMaterialW(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6.0F, 2.0F, 14, ItemTags.IRON_TOOL_MATERIALS);
        DIAMOND = new ToolMaterialW(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 8.0F, 3.0F, 10, ItemTags.DIAMOND_TOOL_MATERIALS);
        GOLD = new ToolMaterialW(BlockTags.INCORRECT_FOR_GOLD_TOOL, 32, 12.0F, 0.0F, 22, ItemTags.GOLD_TOOL_MATERIALS);
        NETHERITE = new ToolMaterialW(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4.0F, 15, ItemTags.NETHERITE_TOOL_MATERIALS);
    }
}
