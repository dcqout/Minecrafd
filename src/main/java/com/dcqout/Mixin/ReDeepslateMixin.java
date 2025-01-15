package com.dcqout.Mixin;

import com.dcqout.Blocks.RefDeepslate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(Blocks.class)
public class ReDeepslateMixin {

    @Shadow private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {return null;}

    @Overwrite
    private static Block register(String name, BlockBehaviour.Properties properties) {
        if (name.contains("reinforced_deepslate")) {return register(name, RefDeepslate::new, properties);}
        return register(name, Block::new, properties);
    }

}
