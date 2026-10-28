package com.vscombatstance.arts.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MartialStandBlock extends BaseEntityBlock {
    public static final MapCodec<MartialStandBlock> CODEC = simpleCodec(MartialStandBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public MartialStandBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        Direction right = facing.getClockWise();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        BlockPos pRight = pos.relative(right);
        BlockPos pAbove = pos.above();
        BlockPos pAboveRight = pAbove.relative(right);

        if (!level.getBlockState(pRight).canBeReplaced(context) ||
            !level.getBlockState(pAbove).canBeReplaced(context) ||
            !level.getBlockState(pAboveRight).canBeReplaced(context)) {
            return null;
        }

        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            Direction right = facing.getClockWise();
            BlockPos pRight = pos.relative(right);
            BlockPos pAbove = pos.above();
            BlockPos pAboveRight = pAbove.relative(right);

            setPart(level, pRight, pos);
            setPart(level, pAbove, pos);
            setPart(level, pAboveRight, pos);
        }
    }

    private void setPart(Level level, BlockPos partPos, BlockPos masterPos) {
        level.setBlock(partPos, ModBlocks.MARTIAL_STAND_PART.get().defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(partPos);
        if (be instanceof MartialStandPartBlockEntity part) {
            part.setMasterPos(masterPos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide) {
                Direction facing = state.getValue(FACING);
                Direction right = facing.getClockWise();
                removePart(level, pos.relative(right));
                removePart(level, pos.above());
                removePart(level, pos.above().relative(right));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            Direction right = facing.getClockWise();
            removePart(level, pos.relative(right));
            removePart(level, pos.above());
            removePart(level, pos.above().relative(right));
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private void removePart(Level level, BlockPos partPos) {
        BlockState partState = level.getBlockState(partPos);
        if (partState.is(ModBlocks.MARTIAL_STAND_PART.get())) {
            level.removeBlock(partPos, false);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        openStandMenu(level, pos, player);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        openStandMenu(level, pos, player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void openStandMenu(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            MenuProvider menu = new SimpleMenuProvider(
                    (id, inv, p) -> new MartialStandMenu(id, inv),
                    Component.translatable("block.customarts.martial_stand")
            );
            serverPlayer.openMenu(menu, pos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MartialStandBlockEntity(pos, state);
    }
}
