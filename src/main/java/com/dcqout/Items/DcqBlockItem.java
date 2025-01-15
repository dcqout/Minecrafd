package com.dcqout.Items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;

public class DcqBlockItem extends BlockItem implements IItemSet {

    private final HashMap<String,Integer> tabPos = new HashMap<String,Integer>();

    public DcqBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public DcqBlockItem(HashMap<String,Integer> ntabPos, Block block, Properties properties) {
        super(block, properties);
        tabPos.putAll(ntabPos);
    }

    @Override
    public int getCreativeTabPos(String tab) {
        return tabPos.getOrDefault(tab,0);
    }
}
