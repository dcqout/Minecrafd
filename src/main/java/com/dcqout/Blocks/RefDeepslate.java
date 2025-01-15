package com.dcqout.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

import static com.dcqout.Main.registrator.Blocks.echo_crystal;

public class RefDeepslate extends Block {

    public static final BooleanProperty TRIGGERED;
    public RefDeepslate(Properties properties) {
        super(properties.randomTicks());
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(TRIGGERED, true));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{TRIGGERED});
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isNight()) {
            if (!state.getValue(TRIGGERED)) {
                level.setBlock(pos,state.setValue(TRIGGERED,true),3);
                if (random.nextBoolean()) {
                    if (random.nextInt(7) >= 1 && (level.getBlockState(pos.east()).is(Blocks.AIR) || level.getBlockState(pos.east()).is(Blocks.SCULK_VEIN))
                            && level.getBlockState(pos.east().below()).is(Blocks.SCULK)) {
                        level.setBlockAndUpdate(pos.east(), echo_crystal.get().defaultBlockState());
                    }
                    if (random.nextInt(7) >= 3 && (level.getBlockState(pos.west()).is(Blocks.AIR) || level.getBlockState(pos.west()).is(Blocks.SCULK_VEIN))
                            && level.getBlockState(pos.west().below()).is(Blocks.SCULK)) {
                        level.setBlockAndUpdate(pos.west(), echo_crystal.get().defaultBlockState());
                    }
                    if (random.nextInt(7) >= 5 && (level.getBlockState(pos.north()).is(Blocks.AIR) || level.getBlockState(pos.north()).is(Blocks.SCULK_VEIN))
                            && level.getBlockState(pos.north().below()).is(Blocks.SCULK)) {
                        level.setBlockAndUpdate(pos.north(), echo_crystal.get().defaultBlockState());
                    }
                    if (random.nextInt(7) == 7 && (level.getBlockState(pos.south()).is(Blocks.AIR) || level.getBlockState(pos.south()).is(Blocks.SCULK_VEIN))
                            && level.getBlockState(pos.south().below()).is(Blocks.SCULK)) {
                        level.setBlockAndUpdate(pos.south(), echo_crystal.get().defaultBlockState());
                    }
                }
            }
        } else if (level.isDay()) {
            level.setBlock(pos,state.setValue(TRIGGERED,false),3);
        }
    }

    static {
        TRIGGERED = BlockStateProperties.TRIGGERED;
    }
}
