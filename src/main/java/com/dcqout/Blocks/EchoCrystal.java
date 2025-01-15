package com.dcqout.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class EchoCrystal extends AmethystBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED;
    public static final EnumProperty<Direction> FACING;
    private final float height;
    private final float aabbOffset;
    protected final VoxelShape northAabb;
    protected final VoxelShape southAabb;
    protected final VoxelShape eastAabb;
    protected final VoxelShape westAabb;
    protected final VoxelShape upAabb;
    protected final VoxelShape downAabb;

    public EchoCrystal(float height, float aabbOffset, BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, false)).setValue(FACING, Direction.UP));
        this.upAabb = Block.box((double)aabbOffset, 0.0, (double)aabbOffset, (double)(16.0F - aabbOffset), (double)height, (double)(16.0F - aabbOffset));
        this.downAabb = Block.box((double)aabbOffset, (double)(16.0F - height), (double)aabbOffset, (double)(16.0F - aabbOffset), 16.0, (double)(16.0F - aabbOffset));
        this.northAabb = Block.box((double)aabbOffset, (double)aabbOffset, (double)(16.0F - height), (double)(16.0F - aabbOffset), (double)(16.0F - aabbOffset), 16.0);
        this.southAabb = Block.box((double)aabbOffset, (double)aabbOffset, 0.0, (double)(16.0F - aabbOffset), (double)(16.0F - aabbOffset), (double)height);
        this.eastAabb = Block.box(0.0, (double)aabbOffset, (double)aabbOffset, (double)height, (double)(16.0F - aabbOffset), (double)(16.0F - aabbOffset));
        this.westAabb = Block.box((double)(16.0F - height), (double)aabbOffset, (double)aabbOffset, 16.0, (double)(16.0F - aabbOffset), (double)(16.0F - aabbOffset));
        this.height = height;
        this.aabbOffset = aabbOffset;
    }

    protected VoxelShape getShape(BlockState p_152021_, BlockGetter p_152022_, BlockPos p_152023_, CollisionContext p_152024_) {
        Direction direction = (Direction)p_152021_.getValue(FACING);
        switch (direction) {
            case NORTH:
                return this.northAabb;
            case SOUTH:
                return this.southAabb;
            case EAST:
                return this.eastAabb;
            case WEST:
                return this.westAabb;
            case DOWN:
                return this.downAabb;
            case UP:
            default:
                return this.upAabb;
        }
    }

    protected boolean canSurvive(BlockState p_152026_, LevelReader p_152027_, BlockPos p_152028_) {
        Direction direction = (Direction)p_152026_.getValue(FACING);
        BlockPos blockpos = p_152028_.relative(direction.getOpposite());
        return p_152027_.getBlockState(blockpos).isFaceSturdy(p_152027_, blockpos, direction);
    }

    protected BlockState updateShape(BlockState p_152036_, LevelReader p_374202_, ScheduledTickAccess p_374490_, BlockPos p_152040_, Direction p_152037_, BlockPos p_152041_, BlockState p_152038_, RandomSource p_374353_) {
        if ((Boolean)p_152036_.getValue(WATERLOGGED)) {
            p_374490_.scheduleTick(p_152040_, Fluids.WATER, Fluids.WATER.getTickDelay(p_374202_));
        }

        return p_152037_ == ((Direction)p_152036_.getValue(FACING)).getOpposite() && !p_152036_.canSurvive(p_374202_, p_152040_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_152036_, p_374202_, p_374490_, p_152040_, p_152037_, p_152041_, p_152038_, p_374353_);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_152019_) {
        LevelAccessor levelaccessor = p_152019_.getLevel();
        BlockPos blockpos = p_152019_.getClickedPos();
        return (BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER)).setValue(FACING, p_152019_.getClickedFace());
    }

    protected BlockState rotate(BlockState p_152033_, Rotation p_152034_) {
        return (BlockState)p_152033_.setValue(FACING, p_152034_.rotate((Direction)p_152033_.getValue(FACING)));
    }

    protected BlockState mirror(BlockState p_152030_, Mirror p_152031_) {
        return p_152030_.rotate(p_152031_.getRotation((Direction)p_152030_.getValue(FACING)));
    }

    protected FluidState getFluidState(BlockState p_152045_) {
        return (Boolean)p_152045_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_152045_);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_152043_) {
        p_152043_.add(new Property[]{WATERLOGGED, FACING});
    }

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        FACING = BlockStateProperties.FACING;
    }
}
