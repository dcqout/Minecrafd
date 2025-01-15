package net.dcqmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.function.Supplier;

public class registry {
    public static final DeferredRegister.DataComponents COMPOS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE,"dcq");
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks("dcq");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems("dcq");
    public static final DeferredRegister<Attribute> ATTS = DeferredRegister.create(Registries.ATTRIBUTE,"dcq");
    //public static final DeferredRegister<DamageType> DMG_TYPE = DeferredRegister.create(Registries.DAMAGE_TYPE,"dcq");
    public static final HashMap<String,DeferredHolder<?,?>> registered = new HashMap<String,DeferredHolder<?,?>>();

    public static <T,D extends T> DeferredHolder<T,D> reg(String name, DeferredRegister<T> reg, Supplier<? extends D> obj) {
        return reg.register(name, obj);
    }

    public static <T,D extends T> DeferredHolder<T,D> register(String name,DeferredRegister<T> dr, Supplier<? extends D> sup) {
        DeferredHolder<T,D> res = reg(name,dr,sup);
        registered.put(name,res);
        return res;
    }
}
