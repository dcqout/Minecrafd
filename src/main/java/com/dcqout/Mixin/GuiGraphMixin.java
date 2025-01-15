package com.dcqout.Mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.dcqout.Main.registrator.DataComponents.ECHO;

@Mixin(GuiGraphics.class)
public class GuiGraphMixin {

    @Shadow public void fill(RenderType renderType, int minX, int minY, int maxX, int maxY, int z, int color) {}
    public int getEchoBarColor(ItemStack stack, int echo, int stackMaxDamage) {
        float f = Math.max(0.35F, ((float)stackMaxDamage-(float)echo)/(float)stackMaxDamage);
        return Mth.hsvToRgb(0.54F, 1.0F,f/0.9F);
    }

    @Overwrite
    private void renderItemBar(ItemStack stack, int x, int y) {
        if (!stack.isDamageableItem()) return;
        int max = stack.getMaxDamage();
        int dmg = Mth.clamp((Integer)stack.getOrDefault(DataComponents.DAMAGE, 0), 0,max);
        int echo = max - Mth.clamp((Integer)stack.getOrDefault(ECHO.get(), 0), 0, stack.getMaxDamage());
        int i = x + 2;
        int j = y + 13;
        if (echo < max) {
            if (dmg > -1) {
                fill(RenderType.gui(), i, j, i + 13, j + 3, 200, -16777216);
                fill(RenderType.gui(), i, j+2, i + (Math.round(13.0F - (float) dmg * 13.0F / (float) max)),
                        j+3, 200, ARGB.opaque(stack.getBarColor()));
                fill(RenderType.gui(), i, j, i + (Math.round(13.0F-(float)echo*13.0F/(float)max)),
                        j+2, 200, ARGB.opaque(getEchoBarColor(stack,echo,max)));
            } else {
                fill(RenderType.gui(), i, j, i + 13, j + 2, 200, -16777216);
                fill(RenderType.gui(), i, j, i + (Math.round(13.0F-(float)echo*13.0F/(float)max)),
                        j+1, 200, ARGB.opaque(getEchoBarColor(stack,echo,max)));
            }
        } else if (dmg > 0) {
            fill(RenderType.gui(), i, j, i + 13, j + 2, 200, -16777216);
            fill(RenderType.gui(), i, j, i + (Math.round(13.0F-(float)dmg*13.0F/(float)max)),
                    j+1, 200, ARGB.opaque(stack.getBarColor()));
        }
    }
}
