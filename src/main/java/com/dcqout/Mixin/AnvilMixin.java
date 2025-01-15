package com.dcqout.Mixin;

import com.dcqout.Items.Hammer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.dcqmod.refer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.dcqout.Main.registrator.Items.*;
import static com.dcqout.Main.registrator.DataComponents.ECHO;

@Mixin(AnvilMenu.class) @SuppressWarnings("UnreachableCode")
public abstract class AnvilMixin extends ItemCombinerMenu {

    public AnvilMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(menuType, containerId, inventory, access, slotDefinition);
    }

    @Shadow public static int calculateIncreasedRepairCost(int oldRepairCost) {return 0;}
    @Shadow private final DataSlot cost = DataSlot.standalone();
    private boolean hammer_dmg = false;
    @Nullable @Shadow private String itemName;
    @Shadow private boolean onlyRenaming = false;

    @Overwrite
    protected void onTake(Player p_150474_, ItemStack p_150475_) {
        AnvilMenu menucast = (AnvilMenu) (Object) this;
        if (!p_150474_.getAbilities().instabuild) {
            p_150474_.giveExperienceLevels(-this.cost.get());
        }
        if (this.hammer_dmg) {
            player.getOffhandItem().hurtAndBreak(2,player,player.getOffhandItem().getEquipmentSlot());
        }
        this.hammer_dmg = false;
        float breakChance = CommonHooks.onAnvilRepair(p_150474_, p_150475_, this.inputSlots.getItem(0), this.inputSlots.getItem(1));
        if (menucast.repairItemCountCost > 0) {
            ItemStack itemstack = this.inputSlots.getItem(1);
            if (!itemstack.isEmpty() && itemstack.getCount() > menucast.repairItemCountCost) {
                itemstack.shrink(menucast.repairItemCountCost);
                this.inputSlots.setItem(1, itemstack);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else if (!this.onlyRenaming) {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }

        this.cost.set(0);
        this.inputSlots.setItem(0, ItemStack.EMPTY);
        this.access.execute((p_150479_, p_150480_) -> {
            BlockState blockstate = p_150479_.getBlockState(p_150480_);
            if (!p_150474_.getAbilities().instabuild && blockstate.is(BlockTags.ANVIL) && p_150474_.getRandom().nextFloat() < breakChance) {
                BlockState blockstate1 = AnvilBlock.damage(blockstate);
                if (blockstate1 == null) {
                    p_150479_.removeBlock(p_150480_, false);
                    p_150479_.levelEvent(1029, p_150480_, 0);
                } else {
                    p_150479_.setBlock(p_150480_, blockstate1, 2);
                    p_150479_.levelEvent(1030, p_150480_, 0);
                }
            } else {
                p_150479_.levelEvent(1030, p_150480_, 0);
            }

        });
    }

    @Overwrite
    public void createResult() {
        AnvilMenu menucast = (AnvilMenu) (Object) this;
        ItemStack itemstack = this.inputSlots.getItem(0);
        onlyRenaming = false;
        cost.set(1);
        int i = 0;
        long j = 0L;
        int k = 0;
        if (!itemstack.isEmpty() && EnchantmentHelper.canStoreEnchantments(itemstack)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getItem(1);
            boolean flag11 = itemstack2.is(Items.ECHO_SHARD);
            ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(itemstack1));
            j += (long)itemstack.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0)).intValue()
                    + (long)itemstack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0)).intValue();
            menucast.repairItemCountCost = 0;
            boolean flag = false;
            if (!net.neoforged.neoforge.common.CommonHooks.onAnvilChange(menucast, itemstack, itemstack2, resultSlots, itemName, j, this.player)) return;
            if (!itemstack2.isEmpty()) {
                flag = itemstack2.has(DataComponents.STORED_ENCHANTMENTS);
                if (itemstack1.isDamageableItem() && (itemstack.isValidRepairItem(itemstack2) || flag11)) {
                    int j3;
                    if (flag11) {
                        int maxD = itemstack1.getMaxDamage();
                        if (maxD-Mth.clamp(itemstack1.getOrDefault(ECHO.get(),0),0,maxD) > 0) {
                            for (j3 = 0; j3 < itemstack2.getCount(); j3++) {
                                int actual = Mth.clamp(itemstack1.getOrDefault(ECHO.get(),0),0,maxD);
                                int nactual = actual+Mth.ceil(((float)maxD)/64.0f);
                                itemstack1.set(ECHO.get(),Mth.clamp(nactual, 0,maxD));
                                i++;
                                if (!(maxD-nactual > 0)) { break; }
                            }
                        } else j3 = 0;
                    } else {
                        int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                        if (l2 <= 0) {
                            this.resultSlots.setItem(0, ItemStack.EMPTY);
                            cost.set(0);
                            return;
                        }
                        for (j3 = 0; l2 > 0 && j3 < itemstack2.getCount(); j3++) {
                            int k3 = itemstack1.getDamageValue() - l2;
                            itemstack1.setDamageValue(k3);
                            i++;
                            l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                        }
                    }

                    menucast.repairItemCountCost = j3;
                } else {
                    if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        cost.set(0);
                        return;
                    }

                    if (itemstack1.isDamageableItem() && !flag) {
                        int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
                        int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
                        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = itemstack1.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }

                        if (l1 < itemstack1.getDamageValue()) {
                            itemstack1.setDamageValue(l1);
                            i += 2;
                        }
                    }

                    ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemstack2);
                    boolean flag2 = false;
                    boolean flag3 = false;

                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
                        Holder<Enchantment> holder = entry.getKey();
                        int i2 = itemenchantments$mutable.getLevel(holder);
                        int j2 = entry.getIntValue();
                        j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                        Enchantment enchantment = holder.value();
                        // Neo: Respect IItemExtension#supportsEnchantment - we also delegate the logic for Enchanted Books to this method.
                        // Though we still allow creative players to combine any item with any enchantment in the anvil here.
                        boolean flag1 = itemstack.supportsEnchantment(holder);
                        if (this.player.getAbilities().instabuild) {
                            flag1 = true;
                        }

                        for (Holder<Enchantment> holder1 : itemenchantments$mutable.keySet()) {
                            if (!holder1.equals(holder) && !Enchantment.areCompatible(holder, holder1)) {
                                flag1 = false;
                                i++;
                            }
                        }

                        if (!flag1) {
                            flag3 = true;
                        } else {
                            flag2 = true;
                            if (j2 > enchantment.getMaxLevel()) {
                                j2 = enchantment.getMaxLevel();
                            }

                            itemenchantments$mutable.set(holder, j2);
                            int l3 = enchantment.getAnvilCost();
                            if (flag) {
                                l3 = Math.max(1, l3 / 2);
                            }

                            i += l3 * j2;
                            if (itemstack.getCount() > 1) {
                                i = 70;
                            }
                        }
                    }

                    if (flag3 && !flag2) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        cost.set(0);
                        return;
                    }
                }
            }

            if (itemName != null && !StringUtil.isBlank(itemName)) {
                if (!itemName.equals(itemstack.getHoverName().getString())) {
                    k = 1;
                    i += k;
                    itemstack1.set(DataComponents.CUSTOM_NAME, Component.literal(itemName));
                }
            } else if (itemstack.has(DataComponents.CUSTOM_NAME)) {
                k = 1;
                i += k;
                itemstack1.remove(DataComponents.CUSTOM_NAME);
            }
            if (flag && !itemstack1.isBookEnchantable(itemstack2)) itemstack1 = ItemStack.EMPTY;

            int k2 = i <= 0 ? 0 : (int) Mth.clamp(j + (long)i, 0L, 2147483647L);
            cost.set(k2);
            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (k == i && k > 0) {
                if (cost.get() >= 70) {
                    cost.set(69);
                }

                onlyRenaming = true;
            }
            int discount = 0;
            if (player.getOffhandItem().getItem() instanceof Hammer && cost.get() > 2) {
                ItemStack itemstackf = player.getOffhandItem();
                if (itemstackf.is(golden_hammer.get()) && ((itemstack2.is(Items.ENCHANTED_BOOK)||itemstack2.isEnchanted()))) { this.hammer_dmg = true;
                    discount = Mth.floor(((float)cost.get())/2.0f);
                    refer.LOGGER.warn("price adjusted for gold");
                } else if (itemstackf.is(diamond_hammer.get()) && !itemstack2.is(Items.ENCHANTED_BOOK)){ this.hammer_dmg = true;
                    discount = Mth.floor(((float)cost.get())/5.0f);
                    refer.LOGGER.warn("price adjusted for diamond");
                } else if (itemstackf.is(netherite_hammer.get())) { this.hammer_dmg = true;
                    discount = Mth.floor(((float)cost.get())/4.0f);
                    refer.LOGGER.warn("price adjusted for netherite");
                } else {
                    this.hammer_dmg = false;
                }
            } else {
                this.hammer_dmg = false;
            }

            if (discount > 0) {
                cost.set(cost.get()-discount);
            }

            if (cost.get() >= 70 && !this.player.getAbilities().instabuild) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (!itemstack1.isEmpty()) {
                int i3 = itemstack1.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
                if (i3 < itemstack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0))) {
                    i3 = itemstack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
                }

                if (k != i || k == 0) {
                    i3 = flag11 ? i3 : calculateIncreasedRepairCost(i3);
                }

                itemstack1.set(DataComponents.REPAIR_COST, i3);
                EnchantmentHelper.setEnchantments(itemstack1, itemenchantments$mutable.toImmutable());
            }

            this.resultSlots.setItem(0, itemstack1);
            this.broadcastChanges();
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            cost.set(0);
        }
    }


}
