package com.dcqout.Main;

import com.dcqout.Items.IItemSet;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

public class ItemDisplayBuilder implements CreativeModeTab.Output {
    public final Collection<ItemStack> tabContents = ItemStackLinkedSet.createTypeAndComponentsSet();
        public final LinkedHashMap<int[],ItemStack> DelayedContents = new LinkedHashMap<>();
    public final Set<ItemStack> searchTabContents = ItemStackLinkedSet.createTypeAndComponentsSet();
    private final CreativeModeTab tab;
    private final FeatureFlagSet featureFlagSet;

    private int mainIndex = 0;
    private int tIndex = 0;
    private int sIndex = 0;

    public ItemDisplayBuilder(CreativeModeTab tab, FeatureFlagSet featureFlagSet) {
        this.tab = tab;
        this.featureFlagSet = featureFlagSet;
    }

    @Override
    public void accept(ItemStack p_250391_, CreativeModeTab.TabVisibility p_251472_) {
        if (p_250391_.getCount() != 1) {
            throw new IllegalArgumentException("Stack size must be exactly 1");
        } else {
            boolean flag = this.tabContents.contains(p_250391_) && p_251472_ != CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
            if (flag) {
                throw new IllegalStateException("Accidentally adding the same item stack twice " + p_250391_.getDisplayName().getString()
                                + " to a Creative Mode Tab: " + this.tab.getDisplayName().getString());
            } else {
                if (p_250391_.getItem().isEnabled(this.featureFlagSet)) {
                    //if (this.tab.getDisplayName().getString().equals("Combat")) {
                        //refer.LOGGER.warn("Combat item "+p_250391_.getDisplayName().getString()+" on indexes "+this.tIndex+" "+this.sIndex+" "+this.mainIndex);
                    //}
                    if (!(p_250391_.getItem() instanceof IItemSet)) {
                    switch (p_251472_) {
                        case PARENT_AND_SEARCH_TABS: this.mainIndex += 1; break;
                        case PARENT_TAB_ONLY: this.tIndex += 1; break;
                        case SEARCH_TAB_ONLY: this.sIndex += 1; break;
                    } }
                    this.DelayedContents.entrySet().removeIf((entry) -> {
                        int index = entry.getKey()[1] > 0 ? (entry.getKey()[1] < 2 ? this.tIndex : this.sIndex )  : this.mainIndex;
                        if (entry.getKey()[0] <= index-1) {
                            //refer.LOGGER.warn("list dcq: "+ entry.getKey()[1] +" for "+entry.getValue().getDisplayName().getString());
                            if (entry.getKey()[1] > 0) {
                                if (entry.getKey()[1] < 2) {
                                    this.tabContents.add(entry.getValue());
                                } else {
                                    this.searchTabContents.add(entry.getValue());
                                }
                            } else {
                                this.tabContents.add(entry.getValue());
                                this.searchTabContents.add(entry.getValue());
                            }
                            return true;
                        }
                        return false;
                    });
                    switch (p_251472_) {
                        case PARENT_AND_SEARCH_TABS:
                            if (p_250391_.getItem() instanceof IItemSet inter) {
                                int newPos = inter.getCreativeTabPos(this.tab.getDisplayName().toString().split("key='")[1].split("'")[0]);
                                if (newPos > 0) {
                                    this.DelayedContents.put(new int[]{newPos,0}, p_250391_);
                                    break;
                                }
                            }
                            this.tabContents.add(p_250391_);
                            this.searchTabContents.add(p_250391_);
                            break;
                        case PARENT_TAB_ONLY:
                            if (p_250391_.getItem() instanceof IItemSet inter) {
                                int newPos = inter.getCreativeTabPos(this.tab.getDisplayName().toString().split("key='")[1].split("'")[0]);
                                if (newPos > 0) {
                                    this.DelayedContents.put(new int[]{newPos,1}, p_250391_);
                                    break;
                                }
                            }
                            this.tabContents.add(p_250391_);
                            break;
                        case SEARCH_TAB_ONLY:
                            if (p_250391_.getItem() instanceof IItemSet inter) {
                                int newPos = inter.getCreativeTabPos(this.tab.getDisplayName().toString().split("key='")[1].split("'")[0]);
                                if (newPos > 0) {
                                    this.DelayedContents.put(new int[]{newPos,2},p_250391_);
                                    break;
                                }
                            }
                            this.searchTabContents.add(p_250391_);
                    }
                }
            }
        }
    }
}