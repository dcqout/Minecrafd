package com.dcqout.Mixin;

import net.minecraft.world.item.CreativeModeTab.Builder;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Builder.class)
public abstract class TabsBuilderMixin {
    private DisplayItemsGenerator merge(DisplayItemsGenerator newGen, DisplayItemsGenerator oldGen) {
        return (parameter, item) -> { newGen.accept(parameter,item); oldGen.accept(parameter,item); }; }
    @Shadow private DisplayItemsGenerator displayItemsGenerator; @Overwrite
    public Builder displayItems(DisplayItemsGenerator newGen) {
        displayItemsGenerator = merge(displayItemsGenerator,newGen);
        return ((Builder)(Object)this);
    }
}
