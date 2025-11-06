package net.eagle.ancientartifacts.block.custom;

import com.mojang.serialization.MapCodec;
import net.eagle.ancientartifacts.block.ModBlocks;
import net.eagle.ancientartifacts.block.entity.DragonPedestalEntity;
import net.eagle.ancientartifacts.block.entity.ModBlockEntities;
import net.eagle.ancientartifacts.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class DragonPedestal extends BaseEntityBlock {

    // --- Properties (same names as Fabric) ---
    public static final BooleanProperty GILDED       = BooleanProperty.create("gilded");
    public static final BooleanProperty FOSSIL_HEAD  = BooleanProperty.create("fossil_head");
    public static final BooleanProperty HEART_SEA    = BooleanProperty.create("heart_sea");
    public static final BooleanProperty ORB_INFINIUM = BooleanProperty.create("orb_of_infinium");
    public static final BooleanProperty END_READY    = BooleanProperty.create("end_ready");

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<DoubleBlockHalf> HALF =
            BlockStateProperties.DOUBLE_BLOCK_HALF;

    // --- Shapes (same as Fabric) ---
    protected static final VoxelShape SHAPE_UPPER;
    protected static final VoxelShape SHAPE_UPPER_F;
    protected static final VoxelShape SHAPE_LOWER;
    protected static final VoxelShape SHAPE_LOWER_G;

    static {
        // TOP
        VoxelShape su1   = Block.box(5.5, 0,   5.5, 10.5, 1,  10.5);
        VoxelShape su2   = Block.box(3.5, 1,   3.5, 12.5, 4,  12.5);
        VoxelShape su3_f = Block.box(1,   3,  3.25, 15.5, 11.7, 12.75);
        VoxelShape su3   = Block.box(3.5, 3,  3.25, 12.5, 6,  12.75);

        // BOTTOM
        VoxelShape sl1_g = Block.box(1,   0,   1,   15,   1,  15);
        VoxelShape sl1   = Block.box(2,   0,   2,   14,   1,  14);
        VoxelShape sl2   = Block.box(3,   1,   3,   13,   3,  13);
        VoxelShape sl3   = Block.box(5.5, 3,  5.5, 10.5, 12, 10.5);

        SHAPE_UPPER   = Shapes.or(su1, su2, su3).optimize();
        SHAPE_UPPER_F = Shapes.or(su1, su2, su3_f).optimize();
        SHAPE_LOWER   = Shapes.or(sl1, sl2, sl3).optimize();
        SHAPE_LOWER_G = Shapes.or(sl1_g, sl2, sl3).optimize();
    }

    public DragonPedestal(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(FACING, Direction.NORTH)
                .setValue(GILDED, false)
                .setValue(FOSSIL_HEAD, false)
                .setValue(HEART_SEA, false)
                .setValue(ORB_INFINIUM, false)
                .setValue(END_READY, false)
        );
    }

    public static final MapCodec<DragonPedestal> CODEC = simpleCodec(DragonPedestal::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    // ---------- Shapes ----------
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext ctx) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            if (state.getValue(GILDED)) {
                return state.getValue(FOSSIL_HEAD) ? SHAPE_UPPER_F : SHAPE_LOWER_G;
            }
            return SHAPE_LOWER;
        }
        return SHAPE_UPPER;
    }

    // ---------- Blockstate/Placement ----------
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        if (pos.getY() >= level.getMaxBuildHeight() - 1) return null;
        if (!level.getBlockState(pos.above()).canBeReplaced(ctx)) return null;

        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(HALF, DoubleBlockHalf.LOWER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockPos up = pos.above();
            if (level.getBlockState(up).canBeReplaced()) {
                level.setBlock(up, state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        } else {
            BlockState above = level.getBlockState(pos.above());
            return above.is(this) && above.getValue(HALF) == DoubleBlockHalf.UPPER;
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (dir == Direction.UP && half == DoubleBlockHalf.LOWER) {
            if (!neighbor.is(this) || neighbor.getValue(HALF) != DoubleBlockHalf.UPPER) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        if (dir == Direction.DOWN && half == DoubleBlockHalf.UPPER) {
            if (!neighbor.is(this) || neighbor.getValue(HALF) != DoubleBlockHalf.LOWER) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    // ---------- Gilded plate auto-merge (your onBlockAdded) ----------
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving) {
        super.onPlace(state, level, pos, old, moving);
        if (level.isClientSide) return;
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) return;

        Block down = level.getBlockState(pos.below()).getBlock();
        if (down == ModBlocks.GILDED_PLATE.get() && !state.getValue(GILDED)) {
            // move LOWER to the plate, then set UPPER at current pos
            level.removeBlock(pos, false);
            level.setBlock(pos.below(), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(GILDED, true), Block.UPDATE_ALL);
            level.setBlock(pos,        state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
        }
    }


    // ---------- Use (item interactions) ----------
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        final String id = String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem()));

        switch (id) {
            case "ancientartifacts:end_staff" -> {
                if (!state.getValue(END_READY) && state.getValue(ORB_INFINIUM)) {
                    level.setBlock(pos, state.setValue(END_READY, true).setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
                    level.playSound(null, pos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 0.2f, 0.9f);
                    level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP,    SoundSource.NEUTRAL, 0.2f, 1.0f);
                    if (!player.isCreative()) {
                        player.displayClientMessage(net.minecraft.network.chat.Component.literal("End Gateway is now Unlocked!"), true);
                    }
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }

            case "ancientartifacts:orb_infinium" -> {
                if (!state.getValue(ORB_INFINIUM) && state.getValue(HEART_SEA)) {
                    level.setBlock(pos, state.setValue(ORB_INFINIUM, true).setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.AMBIENT, 1.0f, 0.6f);
                    if (!player.isCreative()) stack.shrink(1);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; // let vanilla try
            }

            case "minecraft:heart_of_the_sea" -> {
                if (!state.getValue(HEART_SEA) && state.getValue(FOSSIL_HEAD)) {
                    level.setBlock(pos, state.setValue(HEART_SEA, true).setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
                    level.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 1.0f, 0.4f);
                    if (!player.isCreative()) stack.shrink(1);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            case "ancientartifacts:dragon_fossil" -> {
                if (!state.getValue(FOSSIL_HEAD)
                        && state.getValue(GILDED)
                        && !state.getValue(HEART_SEA)) {
                    level.setBlock(pos.above(), state.setValue(FOSSIL_HEAD, true).setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
                    level.playSound(null, pos, SoundEvents.BONE_BLOCK_PLACE, SoundSource.BLOCKS, 0.8f, 0.3f);
                    if (!player.isCreative()) stack.shrink(1);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            default -> {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        // you don't have empty-hand behavior; just pass
        return InteractionResult.PASS;
    }


    // ---------- Blockstate definition ----------
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, GILDED, FOSSIL_HEAD, HEART_SEA, ORB_INFINIUM, END_READY);
    }

    // ---------- Rendering ----------
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // ---------- Removal / drops ----------
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DragonPedestalEntity) {
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos topPos = (state.getValue(HALF) == DoubleBlockHalf.UPPER) ? pos : pos.above();
        BlockPos botPos = (state.getValue(HALF) == DoubleBlockHalf.UPPER) ? pos.below() : pos;

        level.removeBlock(topPos, false);
        level.removeBlock(botPos, false);
        level.updateNeighborsAt(topPos, Blocks.AIR);

        super.playerWillDestroy(level, pos, state, player);

        if (!level.isClientSide && !player.isCreative()) {
            // base pedestal
            popResource(level, pos, new ItemStack(ModBlocks.DRAGON_PEDESTAL.get()));
            // extras based on state
            if (state.getValue(GILDED)) {
                popResource(level, pos, new ItemStack(ModBlocks.GILDED_PLATE.get()));
                if (state.getValue(FOSSIL_HEAD)) {
                    popResource(level, pos, new ItemStack(ModItems.DRAGON_FOSSIL.get()));
                    if (state.getValue(HEART_SEA)) {
                        popResource(level, pos, new ItemStack(Items.HEART_OF_THE_SEA));
                        if (state.getValue(ORB_INFINIUM)) {
                            popResource(level, pos, new ItemStack(ModItems.ORB_INFINIUM.get()));
                        }
                    }
                }
            }
        }
        return state;
    }

    // ---------- Block entity ----------
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return (state.getValue(HALF) == DoubleBlockHalf.UPPER) ? null : ModBlockEntities.DRAGON_PEDESTAL.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? null :
                createTickerHelper(type, ModBlockEntities.DRAGON_PEDESTAL.get(), DragonPedestalEntity::tick);
    }

    // ---------- Ritual pattern ----------
    @Nullable
    private BlockPattern ritualPattern;

    public BlockPattern getRitualPattern() {
        if (this.ritualPattern == null) {
            this.ritualPattern = BlockPatternBuilder.start()
                    .aisle(" LD", "P~N")
                    .where('N', BlockInWorld.hasState(bs -> bs.is(ModBlocks.NENDER_BRICK.get())))
                    .where('P', BlockInWorld.hasState(bs -> bs.is(ModBlocks.DRAGON_PEDESTAL.get())))
                    .where('~', BlockInWorld.hasState(bs -> bs.isAir()))
                    .where('L', BlockInWorld.hasState(bs -> bs.is(ModBlocks.ETHER_LEVER.get())))
                    .where('D', BlockInWorld.hasState(bs -> bs.is(Blocks.DIRT)))
                    .build();
        }
        return this.ritualPattern;
    }

}
