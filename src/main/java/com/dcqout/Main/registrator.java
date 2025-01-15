package com.dcqout.Main;

import com.dcqout.Blocks.EchoCrystal;
import com.dcqout.Items.DcqBlockItem;
import com.dcqout.Items.Hammer;
import com.dcqout.Items.ShortSword;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.function.Supplier;

import static net.dcqmod.registry.*;

public abstract class registrator {

    public static void setup(IEventBus EventBus)
    {
        DataComponents.setup(EventBus);
        Attributes.setup(EventBus);
        Blocks.setup(EventBus);
        Items.setup(EventBus);
        //registry.DMG_TYPE.register(EventBus);
    }

    // DATA COMPONENTS \\
    public static abstract class DataComponents { public static void setup(IEventBus EventBus) { COMPOS.register(EventBus); }
        public static final Supplier<DataComponentType<Integer>> ECHO = COMPOS.registerComponentType("echo", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    }
    // ATTRIBUTES \\
    public static abstract class Attributes { public static void setup(IEventBus EventBus) { ATTS.register(EventBus); }
        public static final DeferredHolder<Attribute, Attribute> MAX_COMBO = register("max_combo", ATTS, () -> new RangedAttribute("attribute.name.max_combo", 0.0, 0.0, 10.0));
    }
    // BLOCKS \\
    public static abstract class Blocks { public static void setup(IEventBus EventBus) { BLOCKS.register(EventBus); }

        public static final DeferredHolder<Block, EchoCrystal> echo_crystal = register("echo_cluster", BLOCKS, () -> new EchoCrystal(7.0F, 3.0F,
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_BLUE)
                        .forceSolidOn()
                        .noOcclusion()
                        .sound(SoundType.AMETHYST_CLUSTER)
                        .strength(1.5F)
                        .pushReaction(PushReaction.DESTROY)
                        .setId((ResourceKey.create(Registries.BLOCK, ResourceLocation.parse("dcq:echo_cluster"))))));
    }
    // ITEMS \\
    public static abstract class Items { public static void setup(IEventBus EventBus) { ITEMS.register(EventBus); }
        public static final DeferredHolder<Item, Item> echo_crystal = register("echo_cluster",ITEMS,() -> new DcqBlockItem(new HashMap<String,Integer>(){{put("itemGroup.natural",77);}},Blocks.echo_crystal.get()
                ,new Item.Properties().setId((ResourceKey.create(Registries.ITEM, ResourceLocation.parse("dcq:echo_cluster"))))));
        public static final DeferredHolder<Item, Item> s_wooden_sword = register("short_wooden_sword",ITEMS, () -> new ShortSword(new HashMap<String,Integer>(){{put("itemGroup.combat",6);}}, ToolMaterial.WOOD, 1.00d, -3.0F,4,
                (new Item.Properties()).setId((ResourceKey.create(Registries.ITEM, ResourceLocation.parse("dcq:short_wooden_sword"))))));
        public static final DeferredHolder<Item, Item> s_stone_sword = register("short_stone_sword",ITEMS, () -> new ShortSword(new HashMap<String,Integer>(){{put("itemGroup.combat",6);}},ToolMaterial.STONE, 1.50d, -3.0F,4,
                (new Item.Properties()).setId((ResourceKey.create(Registries.ITEM, ResourceLocation.parse("dcq:short_stone_sword"))))));
        public static final DeferredHolder<Item, Item> s_iron_sword = register("short_iron_sword",ITEMS, () -> new ShortSword(new HashMap<String,Integer>(){{put("itemGroup.combat",6);}},ToolMaterial.IRON, 2.00d, -3.0F,4,
                (new Item.Properties()).setId((ResourceKey.create(Registries.ITEM, ResourceLocation.parse("dcq:short_iron_sword"))))));
        public static final DeferredHolder<Item, Item> s_golden_sword = register("short_golden_sword",ITEMS, () -> new ShortSword(new HashMap<String,Integer>(){{put("itemGroup.combat",6);}},ToolMaterial.GOLD, 1.00d, -3.0F,4,
                (new Item.Properties()).setId((ResourceKey.create(Registries.ITEM, ResourceLocation.parse("dcq:short_golden_sword"))))));
        public static final DeferredHolder<Item, Item> s_diamond_sword = register("short_diamond_sword",ITEMS, () -> new ShortSword(new HashMap<String,Integer>(){{put("itemGroup.combat",6);}},ToolMaterial.DIAMOND, 2.30d, -3.0F,4,
                (new Item.Properties()).setId((ResourceKey.create(Registries.ITEM, ResourceLocation.parse("dcq:short_diamond_sword"))))));
        public static final DeferredHolder<Item, Item> s_netherite_sword = register("short_netherite_sword",ITEMS, () -> new ShortSword(new HashMap<String,Integer>(){{put("itemGroup.combat",6);}},ToolMaterial.NETHERITE, 2.70d, -3.0F,4,
                (new Item.Properties()).setId((ResourceKey.create(Registries.ITEM, ResourceLocation.parse("dcq:short_netherite_sword"))))));
        /*public static final DeferredHolder<Item, Item> iron_hammer = register("iron_hammer",ITEMS, () -> new Hammer(new HashMap<String,Integer>(){{put("itemGroup.tools",11);}},ToolMaterial.IRON, 5.50d, -3.1F,
                (new Item.Properties()).setId((ResourceKey.create(Registries.ITEM, ResourceLocation.parse("dcq:iron_hammer"))))));*/
        public static final DeferredHolder<Item, Item> golden_hammer = register("golden_hammer",ITEMS, () -> new Hammer(new HashMap<String,Integer>(){{put("itemGroup.tools",16);}},ToolMaterial.GOLD, 4.00d,1.0d, -3.0F,
                (new Item.Properties()).setId((ResourceKey.create(Registries.ITEM, ResourceLocation.parse("dcq:golden_hammer"))))));
        public static final DeferredHolder<Item, Item> diamond_hammer = register("diamond_hammer",ITEMS, () -> new Hammer(new HashMap<String,Integer>(){{put("itemGroup.tools",20);}},ToolMaterial.DIAMOND, 5.50d,1.0d, -3.0F,
                (new Item.Properties()).setId((ResourceKey.create(Registries.ITEM, ResourceLocation.parse("dcq:diamond_hammer"))))));
        public static final DeferredHolder<Item, Item> netherite_hammer = register("netherite_hammer",ITEMS, () -> new Hammer(new HashMap<String,Integer>(){{put("itemGroup.tools",24);put("itemGroup.combat",12);}},ToolMaterial.NETHERITE, 7.00d,2.0d, -3.0F,
                (new Item.Properties()).setId((ResourceKey.create(Registries.ITEM, ResourceLocation.parse("dcq:netherite_hammer"))))));
    }
}
