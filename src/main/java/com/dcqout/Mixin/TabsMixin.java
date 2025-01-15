package com.dcqout.Mixin;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.dcqout.Main.registrator.Items.*;

@Mixin(CreativeModeTabs.class)
public class TabsMixin {

    @Redirect(method = "bootstrap", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/world/item/CreativeModeTab$Builder;displayItems(Lnet/minecraft/world/item/CreativeModeTab$DisplayItemsGenerator;)Lnet/minecraft/world/item/CreativeModeTab$Builder;"))
    private static CreativeModeTab.Builder naturalTab(CreativeModeTab.Builder instance, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        instance.displayItems((a,b) -> {
            b.accept(echo_crystal.get());
        });
        instance.displayItems(displayItemsGenerator);
        return instance;
    }

    @Redirect(method = "bootstrap", at = @At(value = "INVOKE", ordinal = 6, target = "Lnet/minecraft/world/item/CreativeModeTab$Builder;displayItems(Lnet/minecraft/world/item/CreativeModeTab$DisplayItemsGenerator;)Lnet/minecraft/world/item/CreativeModeTab$Builder;"))
    private static CreativeModeTab.Builder toolsTab(CreativeModeTab.Builder instance, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        instance.displayItems((a,b) -> {
            //b.accept(registry.iron_hammer.get());
            b.accept(diamond_hammer.get());
            b.accept(golden_hammer.get());
            b.accept(netherite_hammer.get());
        });
        instance.displayItems(displayItemsGenerator);
        return instance;
    }

    @Redirect(method = "bootstrap", at = @At(value = "INVOKE", ordinal = 7, target = "Lnet/minecraft/world/item/CreativeModeTab$Builder;displayItems(Lnet/minecraft/world/item/CreativeModeTab$DisplayItemsGenerator;)Lnet/minecraft/world/item/CreativeModeTab$Builder;"))
    private static CreativeModeTab.Builder combatTab(CreativeModeTab.Builder instance, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        instance.displayItems((a,b) -> {
            b.accept(s_wooden_sword.get());
            b.accept(s_stone_sword.get());
            b.accept(s_iron_sword.get());
            b.accept(s_golden_sword.get());
            b.accept(s_diamond_sword.get());
            b.accept(s_netherite_sword.get());
        });
        instance.displayItems((a,b) -> {
            b.accept(netherite_hammer.get());
        });
        instance.displayItems(displayItemsGenerator);
        return instance;
    }
}
