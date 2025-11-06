package net.eagle.ancientartifacts.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class EtherLever extends LeverBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<AttachFace> FACE =
            BlockStateProperties.ATTACH_FACE;

    // Shapes (converted from createCuboidShape -> Block.box)
    protected static final VoxelShape NORTH_WALL_SHAPE = Block.box(6.0, 5.0, 4.0, 10.0, 11.0, 16.0);
    protected static final VoxelShape SOUTH_WALL_SHAPE = Block.box(6.0, 5.0, 0.0, 10.0, 11.0, 10.0);
    protected static final VoxelShape WEST_WALL_SHAPE  = Block.box(4.0, 5.0, 6.0, 16.0, 11.0, 10.0);
    protected static final VoxelShape EAST_WALL_SHAPE  = Block.box(0.0, 5.0, 6.0, 12.0, 11.0, 10.0);
    protected static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.box(6.0, 0.0, 5.0, 10.0, 12.0, 11.0);
    protected static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.box(5.0, 0.0, 6.0, 11.0, 12.0, 10.0);
    protected static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.box(6.0, 4.0, 5.0, 10.0, 16.0, 11.0);
    protected static final VoxelShape CEILING_X_AXIS_SHAPE = Block.box(5.0, 4.0, 6.0, 11.0, 16.0, 10.0);

    public static final Vector3f BLUE = new Vector3f(65f/255f, 102f/255f, 245f/255f);

    public EtherLever(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FACE, AttachFace.WALL)
                .setValue(POWERED, false)
        );
    }

    // Placement keeps vanilla facing/face behavior (LeverBlock handles attachment),
    // but we ensure our default state is sensible.
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placed = super.getStateForPlacement(ctx);
        if (placed == null) return null;
        // super already sets FACE/FACING. We just make sure POWERED=false initially.
        return placed.setValue(POWERED, false);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        AttachFace face = state.getValue(FACE);
        if (face == AttachFace.FLOOR) {
            return (Objects.requireNonNull(state.getValue(FACING).getAxis()) == Direction.Axis.X)
                    ? FLOOR_X_AXIS_SHAPE : FLOOR_Z_AXIS_SHAPE;
        } else if (face == AttachFace.WALL) {
            return switch (state.getValue(FACING)) {
                case EAST  -> EAST_WALL_SHAPE;
                case WEST  -> WEST_WALL_SHAPE;
                case SOUTH -> SOUTH_WALL_SHAPE;
                default    -> NORTH_WALL_SHAPE;
            };
        }
        // ceiling
        return (Objects.requireNonNull(state.getValue(FACING).getAxis()) == Direction.Axis.X)
                ? CEILING_X_AXIS_SHAPE : CEILING_Z_AXIS_SHAPE;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
         Player player, InteractionHand hand, BlockHitResult hit) {
        // client-side preview particles (matches your previous behavior)
        if (level.isClientSide) {
            BlockState preview = state.cycle(POWERED);
            if (preview.getValue(POWERED)) {
                spawnParticles(preview, level, pos, 1.0f);
            }
            return ItemInteractionResult.SUCCESS;
        }

        BlockState newState = togglePower(state, level, pos);
        float pitch = newState.getValue(POWERED) ? 0.6f : 0.5f;
        level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3f, pitch);
        level.gameEvent(player, newState.getValue(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
        return ItemInteractionResult.CONSUME;
    }

    // Player USING with EMPTY hand
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            BlockState preview = state.cycle(POWERED);
            if (preview.getValue(POWERED)) {
                spawnParticles(preview, level, pos, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }

        BlockState newState = togglePower(state, level, pos);
        float pitch = newState.getValue(POWERED) ? 0.6f : 0.5f;
        level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3f, pitch);
        level.gameEvent(player, newState.getValue(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
        return InteractionResult.CONSUME;
    }

    // ——— Custom helpers to talk to your CopperWire network ———

    private void setRootRod(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos copperPos = pos.relative(dir);
            BlockState copper = level.getBlockState(copperPos);
            if (copper.is(Blocks.COPPER_BLOCK) || copper.is(Blocks.WAXED_COPPER_BLOCK)) {
                BlockPos[] adj = new BlockPos[]{
                        copperPos.north(), copperPos.south(), copperPos.east(),
                        copperPos.west(), copperPos.above(), copperPos.below()
                };
                for (BlockPos rodPos : adj) {
                    BlockState rod = level.getBlockState(rodPos);
                    if (rod.getBlock() instanceof CopperWire) {
                        level.setBlock(rodPos,
                                rod
                                        .setValue(CopperWire.IS_ROOT, true)
                                        .setValue(CopperWire.POWER, level.getBestNeighborSignal(pos))
                                        .setValue(CopperWire.IS_POWERED, level.getBlockState(pos).getValue(POWERED)),
                                Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    private void setNonRootRod(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos copperPos = pos.relative(dir);
            BlockState copper = level.getBlockState(copperPos);
            if (copper.is(Blocks.COPPER_BLOCK) || copper.is(Blocks.WAXED_COPPER_BLOCK)) {
                BlockPos[] adj = new BlockPos[]{
                        copperPos.north(), copperPos.south(), copperPos.east(),
                        copperPos.west(), copperPos.above(), copperPos.below()
                };
                for (BlockPos rodPos : adj) {
                    BlockState rod = level.getBlockState(rodPos);
                    if (rod.getBlock() instanceof CopperWire) {
                        level.setBlock(rodPos,
                                rod.setValue(CopperWire.IS_ROOT, false)
                                        .setValue(CopperWire.POWER, 0)
                                        .setValue(CopperWire.IS_POWERED, false),
                                Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    private void setRootRodOff(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos copperPos = pos.relative(dir);
            BlockState copper = level.getBlockState(copperPos);
            if (copper.is(Blocks.COPPER_BLOCK) || copper.is(Blocks.WAXED_COPPER_BLOCK)) {
                BlockPos[] adj = new BlockPos[]{
                        copperPos.north(), copperPos.south(), copperPos.east(),
                        copperPos.west(), copperPos.above(), copperPos.below()
                };
                for (BlockPos rodPos : adj) {
                    BlockState rod = level.getBlockState(rodPos);
                    if (rod.getBlock() instanceof CopperWire) {
                        level.setBlock(rodPos,
                                rod.setValue(CopperWire.POWER, 0)
                                        .setValue(CopperWire.IS_POWERED, false),
                                Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    // ——— Lifecycle hooks to sync with wires ———

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving) {
        super.onPlace(state, level, pos, old, moving);
        setRootRod(level, pos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);
        setNonRootRod(level, pos);
        return state;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved && state.getBlock() != newState.getBlock() && state.getValue(POWERED)) {
            updateNeighbors(state, level, pos);
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    // ——— Redstone + neighbors ———

    private static void spawnParticles(BlockState state, LevelAccessor level, BlockPos pos, float scale) {
        Direction facing = state.getValue(FACING).getOpposite();
        Direction leverDir = LeverBlock.getConnectedDirection(state).getOpposite();

        double x = pos.getX() + 0.5
                + 0.1 * facing.getStepX()
                + 0.2 * leverDir.getStepX();
        double y = pos.getY() + 0.5
                + 0.1 * facing.getStepY()
                + 0.2 * leverDir.getStepY();
        double z = pos.getZ() + 0.5
                + 0.1 * facing.getStepZ()
                + 0.2 * leverDir.getStepZ();

        level.addParticle(new DustParticleOptions(BLUE, scale), x, y, z, 0.0, 0.0, 0.0);
    }

    private void updateNeighbors(BlockState state, Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(LeverBlock.getConnectedDirection(state).getOpposite()), this);
    }

    public BlockState togglePower(BlockState state, Level level, BlockPos pos) {
        state = state.cycle(POWERED);
        if (!state.getValue(POWERED)) {
            setRootRodOff(level, pos);
        }
        level.setBlock(pos, state, Block.UPDATE_ALL);
        updateNeighbors(state, level, pos);
        return state;
    }

    // vanilla lever already handles signal methods; no need to override getSignal/getDirectSignal

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, POWERED);
    }
}
