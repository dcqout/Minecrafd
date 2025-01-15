package com.dcqout.Mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(Items.class)
public class PotionsMixin {

    @Shadow public static Item registerItem(ResourceKey<Item> key, Function<Item.Properties, Item> factory, Item.Properties properties) {return null;}
    @Shadow private static ResourceKey<Item> vanillaItemId(String name) { return null; }

    @Overwrite
    public static Item registerItem(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
        if (name.startsWith("potion")) {return registerItem(vanillaItemId(name), factory, properties.stacksTo(8));}
        return registerItem(vanillaItemId(name), factory, properties);
    }

}
