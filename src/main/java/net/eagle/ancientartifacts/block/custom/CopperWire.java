package net.eagle.ancientartifacts.block.custom;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("deprecation")
public class CopperWire extends Block implements SimpleWaterloggedBlock {

    // ---- Properties ----
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING    = BlockStateProperties.FACING;

    public static final BooleanProperty WIRE_CONNECTION_NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty WIRE_CONNECTION_SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WIRE_CONNECTION_EAST  = BlockStateProperties.EAST;
    public static final BooleanProperty WIRE_CONNECTION_WEST  = BlockStateProperties.WEST;
    public static final BooleanProperty WIRE_CONNECTION_UP    = BlockStateProperties.UP;
    public static final BooleanProperty WIRE_CONNECTION_DOWN  = BlockStateProperties.DOWN;

    public static final BooleanProperty IS_ROOT   = BooleanProperty.create("is_root");
    public static final BooleanProperty IS_POWERED= BooleanProperty.create("is_powered");
    public static final IntegerProperty POWER     = IntegerProperty.create("power", 0, 15);

    // ---- Shapes ----
    private static final VoxelShape ROD_X_SHAPE;
    private static final VoxelShape ROD_Y_SHAPE;
    private static final VoxelShape ROD_Z_SHAPE;
    private static final Map<Direction, VoxelShape> DIRECTION_TO_SIDE_SHAPE;

    // Cache shapes by state (ignoring power/ispowered)
    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

    public static final Map<Direction, BooleanProperty> DIRECTION_TO_WIRE_CONNECTION_PROPERTY =
            Maps.newHashMap(ImmutableMap.of(
                    Direction.NORTH, WIRE_CONNECTION_NORTH,
                    Direction.EAST,  WIRE_CONNECTION_EAST,
                    Direction.SOUTH, WIRE_CONNECTION_SOUTH,
                    Direction.WEST,  WIRE_CONNECTION_WEST,
                    Direction.UP,    WIRE_CONNECTION_UP,
                    Direction.DOWN,  WIRE_CONNECTION_DOWN
            ));

    public CopperWire(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(IS_ROOT, false)
                        .setValue(IS_POWERED, false)
                        .setValue(WATERLOGGED, false)
                        .setValue(FACING, Direction.DOWN)
                        .setValue(WIRE_CONNECTION_DOWN,  false)
                        .setValue(WIRE_CONNECTION_UP,    false)
                        .setValue(WIRE_CONNECTION_NORTH, false)
                        .setValue(WIRE_CONNECTION_SOUTH, false)
                        .setValue(WIRE_CONNECTION_EAST,  false)
                        .setValue(WIRE_CONNECTION_WEST,  false)
                        .setValue(POWER, 0)
        );

        // Precompute shapes for every state variant but normalize POWER/IS_POWERED
        for (BlockState st : this.stateDefinition.getPossibleStates()) {
            if (st.getValue(POWER) != 0) continue;
            SHAPES.put(st, computeShapeForState(st));
        }
    }

    // ---------- Shapes ----------
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        // normalize to POWER=0, IS_POWERED=false for cache hit
        BlockState key = state
                .setValue(POWER, 0)
                .setValue(IS_POWERED, false);
        return SHAPES.getOrDefault(key, computeShapeForState(key));
    }

    private VoxelShape computeShapeForState(BlockState state) {
        VoxelShape shape = baseShapeForFacing(state);
        for (Direction dir : Direction.values()) {
            if (state.getValue(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir))) {
                shape = Shapes.or(shape, DIRECTION_TO_SIDE_SHAPE.get(dir));
            }
        }
        return shape.optimize();
    }

    private VoxelShape baseShapeForFacing(BlockState state) {
        return switch (state.getValue(FACING).getAxis()) {
            case X -> ROD_X_SHAPE;
            case Z -> ROD_Z_SHAPE;
            case Y -> ROD_Y_SHAPE;
        };
    }

    // ---------- Placement ----------
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        boolean water = level.getFluidState(pos).getType() == Fluids.WATER;
        BlockState placed = this.defaultBlockState()
                .setValue(FACING, ctx.getClickedFace())
                .setValue(WATERLOGGED, water);

        return initStateFromNeighbors(level, placed, pos);
    }

    private BlockState initStateFromNeighbors(Level level, BlockState state, BlockPos pos) {
        // Set connection booleans, find roots, and precompute power
        for (Direction dir : Direction.values()) {
            BlockPos npos = pos.relative(dir);
            BlockState nst = level.getBlockState(npos);

            if (nst.is(Blocks.COPPER_BLOCK) || nst.is(Blocks.WAXED_COPPER_BLOCK)) {
                // Root: powered copper block adjacent to an EtherLever (custom block)
                BlockPos leverPos = findEtherLever(level, npos);
                BlockState leverState = level.getBlockState(leverPos);
                if (!leverPos.equals(pos) && (leverState.getBlock() instanceof EtherLever)) {
                    state = state
                            .setValue(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir), true)
                            .setValue(IS_ROOT, true)
                            .setValue(POWER, level.getBestNeighborSignal(npos)); // vanilla strongest redstone at npos
                } else {
                    state = state
                            .setValue(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir), true)
                            .setValue(IS_ROOT, false);
                }
            } else if (nst.getBlock() instanceof CopperWire) {
                // connect to other wire, also set their opposite connection
                state = state.setValue(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir), true);
                level.setBlock(npos, nst.setValue(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir.getOpposite()), true), Block.UPDATE_ALL);
            } else {
                // connect into solid blocks (embed/conduit look)
                if (nst.isSolid()) {
                    state = state.setValue(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir), true);
                }
            }
        }

        int strongest = strongestWirePowerFromNeighbors(level, pos);
        if (strongest == 0) {
            state = state.setValue(POWER, 0);
        } else {
            state = state.setValue(POWER, strongest - 1);
            level.levelEvent(null, LevelEvent.PARTICLES_ELECTRIC_SPARK, pos, state.getValue(FACING).getAxis().ordinal());
        }
        state = state.setValue(IS_POWERED, state.getValue(POWER) > 0);
        return state;
    }

    // ---------- Break / replacement ----------
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, moved);
            return;
        }
        super.onRemove(state, level, pos, newState, moved);
        if (level.isClientSide) return;

        // Update all neighbors (wire and non-wire)
        for (Direction dir : Direction.values()) {
            level.updateNeighborsAt(pos.relative(dir), this);
        }
        updateNeighbors(level, pos);
        updateOffsetNeighbors(level, pos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);

        // When broken: update adjacent wires and clear their opposite side connection
        for (Direction dir : Direction.values()) {
            BlockPos npos = pos.relative(dir);
            BlockState nst = level.getBlockState(npos);
            if (nst.getBlock() instanceof CopperWire wire) {
                BlockState updated = nst.setValue(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir.getOpposite()), false);
                if (state.getValue(IS_ROOT)) {
                    updated = updated.setValue(POWER, 0).setValue(IS_POWERED, false);
                }
                level.setBlock(npos, updated, Block.UPDATE_ALL);
                updateNeighbors(level, pos);
            }
        }
        return state;
    }

    // ---------- Neighbor notifications ----------
    private void updateNeighbors(Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        for (Direction dir : Direction.values()) {
            level.updateNeighborsAt(pos.relative(dir), this);
        }
    }

    private void updateOffsetNeighbors(Level level, BlockPos pos) {
        // Update all offset neighbors + above/below for solid blocks similar to your Fabric code
        for (Direction dir : Direction.values()) {
            updateNeighbors(level, pos.relative(dir));
        }
        for (Direction dir : Direction.values()) {
            BlockPos bp = pos.relative(dir);
            if (level.getBlockState(bp).isSolid()) {
                updateNeighbors(level, bp.above());
            } else {
                updateNeighbors(level, bp.below());
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(state, level, pos, block, fromPos, notify);

        int netPower = 0;
        BlockState fromState = level.getBlockState(fromPos);

        if (fromState.is(Blocks.COPPER_BLOCK) || fromState.is(Blocks.WAXED_COPPER_BLOCK)) {
            BlockPos leverPos = findEtherLever(level, fromPos);
            BlockState lever = level.getBlockState(leverPos);

            if (lever.getBlock() instanceof EtherLever) {
                netPower = level.getBestNeighborSignal(fromPos);
                state = state.setValue(POWER, netPower).setValue(IS_ROOT, true);
                if (state.getValue(POWER) > 0) {
                    state = state.setValue(IS_POWERED, true);
                    level.levelEvent(null, LevelEvent.PARTICLES_ELECTRIC_SPARK, pos, state.getValue(FACING).getAxis().ordinal());
                } else {
                    state = state.setValue(IS_POWERED, false);
                }
                level.setBlock(pos, state, Block.UPDATE_ALL);
                return;
            } else {
                state = state.setValue(POWER, 0).setValue(IS_ROOT, false);
                state = state.setValue(IS_POWERED, false);
                level.setBlock(pos, state, Block.UPDATE_ALL);
                return;
            }
        } else if (fromState.getBlock() instanceof CopperWire) {
            if (!state.getValue(IS_ROOT)) {
                int strongest = strongestWirePowerFromNeighbors(level, pos);
                // Also consider emitted power per-direction (mirrors your fabric loop)
                for (Direction dir : Direction.values()) {
                    BlockPos npos = pos.relative(dir);
                    BlockState nst = level.getBlockState(npos);
                    if (nst.getBlock() instanceof CopperWire) {
                        int emitted = level.getSignal(npos, dir);
                        strongest = Math.max(strongest, emitted);
                    }
                }
                state = state.setValue(POWER, strongest == 0 ? 0 : strongest - 1);
            }
        }

        state = state.setValue(IS_POWERED, state.getValue(POWER) > 0);

        if (state.getValue(IS_ROOT)) {
            // if root lost adjacency to copper, drop root
            boolean hasCopperNeighbor = false;
            for (Direction dir : Direction.values()) {
                BlockPos npos = pos.relative(dir);
                BlockState nst = level.getBlockState(npos);
                if (nst.is(Blocks.COPPER_BLOCK) || nst.is(Blocks.WAXED_COPPER_BLOCK)) {
                    hasCopperNeighbor = true;
                    break;
                }
            }
            if (!hasCopperNeighbor) {
                state = state.setValue(POWER, 0).setValue(IS_POWERED, false).setValue(IS_ROOT, false);
            }
        } else {
            // if not root and no wire neighbors, zero out power
            boolean hasWireNeighbor = false;
            for (Direction dir : Direction.values()) {
                if (level.getBlockState(pos.relative(dir)).getBlock() instanceof CopperWire) {
                    hasWireNeighbor = true;
                    break;
                }
            }
            if (!hasWireNeighbor) {
                state = state.setValue(POWER, 0).setValue(IS_POWERED, false);
            }
        }

        level.setBlock(pos, state, Block.UPDATE_ALL);
    }

    private int strongestWirePowerFromNeighbors(Level level, BlockPos pos) {
        int strongest = 0;
        for (Direction dir : Direction.values()) {
            BlockPos npos = pos.relative(dir);
            BlockState nst = level.getBlockState(npos);
            if (nst.getBlock() instanceof CopperWire) {
                strongest = Math.max(strongest, nst.getValue(POWER));
            }
        }
        return strongest;
    }

    private BlockPos findEtherLever(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos check = pos.relative(dir);
            BlockState st = level.getBlockState(check);
            if (st.getBlock() instanceof EtherLever) {
                return check;
            }
        }
        return pos; // not found; return self (your original behavior)
    }

    // ---------- Waterlogging ----------
    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    // ---------- Redstone ----------
    @Override
    public boolean isSignalSource(BlockState state) { // was emitsRedstonePower
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) { // weak power
        if (state.getValue(POWER) == 0) return 0;
        if (state.getValue(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir))) {
            return state.getValue(POWER);
        }
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) { // strong power
        return getSignal(state, level, pos, dir);
    }

    // ---------- State container ----------
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_ROOT, WATERLOGGED, IS_POWERED, POWER,
                FACING,
                WIRE_CONNECTION_NORTH, WIRE_CONNECTION_SOUTH,
                WIRE_CONNECTION_EAST,  WIRE_CONNECTION_WEST,
                WIRE_CONNECTION_UP,    WIRE_CONNECTION_DOWN);
    }

    // ---------- Static shapes ----------
    static {
        // X-axis rod: [0..16] units
        VoxelShape vx1 = Block.box(0,   6.3, 6.3,  2.5, 9.7,  9.7);
        VoxelShape vx2 = Block.box(2.5, 7,   7,    13.5, 9,   9);
        VoxelShape vx3 = Block.box(13.5,6.3, 6.3,  16,  9.7,  9.7);
        ROD_X_SHAPE = Shapes.or(vx1, vx2, vx3).optimize();

        // Y-axis rod
        VoxelShape vy1 = Block.box(6.3, 0,   6.3,  9.7, 2.5,  9.7);
        VoxelShape vy2 = Block.box(7,   2.5, 7,    9,   13.5, 9);
        VoxelShape vy3 = Block.box(6.3, 13.5,6.3,  9.7, 16,   9.7);
        ROD_Y_SHAPE = Shapes.or(vy1, vy2, vy3).optimize();

        // Z-axis rod
        VoxelShape vz1 = Block.box(6.3, 6.3, 0,    9.7, 9.7,  2.5);
        VoxelShape vz2 = Block.box(7,   7,   2.5,  9,   9,    13.5);
        VoxelShape vz3 = Block.box(6.3, 6.3, 13.5, 9.7, 9.7,  16);
        ROD_Z_SHAPE = Shapes.or(vz1, vz2, vz3).optimize();

        DIRECTION_TO_SIDE_SHAPE = Maps.newEnumMap(ImmutableMap.of(
                Direction.NORTH, ROD_Z_SHAPE,
                Direction.SOUTH, ROD_Z_SHAPE,
                Direction.EAST,  ROD_X_SHAPE,
                Direction.WEST,  ROD_X_SHAPE,
                Direction.UP,    ROD_Y_SHAPE,
                Direction.DOWN,  ROD_Y_SHAPE
        ));
    }
}
