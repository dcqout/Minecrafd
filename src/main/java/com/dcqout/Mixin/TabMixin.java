package com.dcqout.Mixin;

import com.dcqout.Main.ItemDisplayBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Set;

@Mixin(CreativeModeTab.class)
public class TabMixin {
    @Shadow private Collection<ItemStack> displayItems;
    @Shadow private Set<ItemStack> displayItemsSearchTab;
    @Shadow private final CreativeModeTab.DisplayItemsGenerator displayItemsGenerator;

    public TabMixin(CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        this.displayItemsGenerator = displayItemsGenerator;
    }

    @Overwrite @SuppressWarnings("UnreachableCode")
    public void buildContents(CreativeModeTab.ItemDisplayParameters parameters) {
        CreativeModeTab caster = ((CreativeModeTab)(Object)this);
        ItemDisplayBuilder creativemodetab$itemdisplaybuilder = new ItemDisplayBuilder(caster, parameters.enabledFeatures());
        ResourceKey<CreativeModeTab> resourcekey = BuiltInRegistries.CREATIVE_MODE_TAB
                .getResourceKey(caster)
                .orElseThrow(() -> new IllegalStateException("Unregistered creative tab: " + this));
        net.neoforged.neoforge.event.EventHooks.onCreativeModeTabBuildContents(caster, resourcekey, displayItemsGenerator, parameters, creativemodetab$itemdisplaybuilder);
        displayItems = creativemodetab$itemdisplaybuilder.tabContents;
        displayItemsSearchTab = creativemodetab$itemdisplaybuilder.searchTabContents;
    }

}
