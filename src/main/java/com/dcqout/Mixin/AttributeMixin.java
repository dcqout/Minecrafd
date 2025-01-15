package com.dcqout.Mixin;

import com.dcqout.Items.ShortSword;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import static com.dcqout.Main.registrator.Attributes.MAX_COMBO;

@Mixin(Attribute.class) @SuppressWarnings("UnreachableCode")
public abstract class AttributeMixin implements IAttributeExtension {
    @Override
    public @Nullable ResourceLocation getBaseId() {
        if ((Attribute) (Object) this == MAX_COMBO.value()) {
            return ShortSword.COMBO_ID;
        }
        return IAttributeExtension.super.getBaseId();
    }
}
