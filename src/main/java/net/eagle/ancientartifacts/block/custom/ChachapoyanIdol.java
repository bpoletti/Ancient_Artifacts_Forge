package net.eagle.ancientartifacts.block.custom;

import com.mojang.serialization.MapCodec;
import net.eagle.ancientartifacts.block.ModBlocks;
import net.eagle.ancientartifacts.item.ModItems;
import net.eagle.ancientartifacts.potion.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class ChachapoyanIdol extends HorizontalDirectionalBlock {

    // ---- Codec (1.21.x requires this) ----
    public static final MapCodec<ChachapoyanIdol> CODEC = simpleCodec(ChachapoyanIdol::new);

    // ---- Properties ----
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty KEY               = BooleanProperty.create("key");
    public static final BooleanProperty PENDANT           = BooleanProperty.create("pendant");
    public static final BooleanProperty SCALES            = BooleanProperty.create("scales");
    public static final BooleanProperty ELDERIAN_MONUMENT = BooleanProperty.create("elderian_monument");

    // ---- Shape ----
    private static final VoxelShape SHAPE = Block.box(1, 0, 2, 14, 15.5, 14);

    // ---- Pattern cache ----
    @Nullable
    private BlockPattern elderianMonumentPattern;

    public ChachapoyanIdol(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, net.minecraft.core.Direction.NORTH)
                .setValue(KEY, false)
                .setValue(PENDANT, false)
                .setValue(SCALES, false)
                .setValue(ELDERIAN_MONUMENT, false)
        );
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    // ----- Shapes -----
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    // ----- Placement / rotation -----
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    // ----- Sounds + “creeper trap” on placement -----
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        // anvil “thunk” twice
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.2f, 0.4f);
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.4f, 0.4f);

        if (!level.isClientSide) {
            int radius = 16;
            boolean any = false;
            for (Creeper creeper : level.getEntitiesOfClass(Creeper.class,
                    new AABB(pos).inflate(radius))) {
                creeper.ignite(); // start fuse
                any = true;
            }
            if (any) {
                level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.TRIDENT_THUNDER, SoundSource.NEUTRAL, 1.0f, 0.3f);
                level.destroyBlock(pos, false);
            }
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    // ----- Interactions (1.21.1 splits into useItemOn / useWithoutItem) -----
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Require the full monument pattern first
        BlockPattern.BlockPatternMatch match = this.getMonumentPattern().find(level, pos);
        if (match == null) {
            if (!level.isClientSide) {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("Full Monument needs to be built first"), true);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        final String id = String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem()));

        switch (id) {
            // Potion bottle: check for ELIXIR_OF_DRAKE contents
            case "minecraft:potion" -> {
                PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
                Potion held = (contents != null && contents.potion().isPresent())
                        ? contents.potion().get().value()
                        : null;

                if (held != null && held == ModPotions.ELIXIR_OF_DRAKE.get()) {
                    if (!state.getValue(ELDERIAN_MONUMENT) && state.getValue(SCALES)) {
                        level.setBlock(pos, state.setValue(ELDERIAN_MONUMENT, true), Block.UPDATE_ALL);
                        if (!player.isCreative()) {
                            stack.shrink(1);
                            player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                        }

                        // drop ORB_INFINIUM one block above
                        Block.popResource(level, pos.above(), new ItemStack(ModItems.ORB_INFINIUM.get()));

                        level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.7f, 1.0f);
                        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                        return ItemInteractionResult.CONSUME;
                    }
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            // elder_guardian_scales
            case "ancientartifacts:elder_guardian_scales" -> {
                if (!state.getValue(SCALES) && state.getValue(PENDANT)) {
                    level.setBlock(pos, state.setValue(SCALES, true), Block.UPDATE_ALL);
                    if (!player.isCreative()) stack.shrink(1);
                    level.playSound(null, pos, SoundEvents.FLOWERING_AZALEA_PLACE, SoundSource.NEUTRAL, 0.7f, 0.2f);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    return ItemInteractionResult.CONSUME;
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            // ankh_pendant
            case "ancientartifacts:ankh_pendant" -> {
                if (!state.getValue(PENDANT) && state.getValue(KEY)) {
                    level.setBlock(pos, state.setValue(PENDANT, true), Block.UPDATE_ALL);
                    if (!player.isCreative()) stack.shrink(1);
                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.NEUTRAL, 0.8f, 0.3f);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    return ItemInteractionResult.CONSUME;
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            // evoker_key
            case "ancientartifacts:evoker_key" -> {
                if (!state.getValue(KEY)) {
                    level.setBlock(pos, state.setValue(KEY, true), Block.UPDATE_ALL);
                    level.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.NEUTRAL, 0.7f, 0.45f);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    return ItemInteractionResult.CONSUME;
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            default -> {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // no empty-hand behavior
        return InteractionResult.PASS;
    }

    // ----- Break → return components you installed -----
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && !player.isCreative()) {
            if (state.getValue(PENDANT)) {
                Block.popResource(level, pos.above(), new ItemStack(ModItems.ANKH_PENDANT.get()));
                if (state.getValue(SCALES)) {
                    Block.popResource(level, pos.above(), new ItemStack(ModItems.ELDER_GUARDIAN_SCALES.get()));
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    // ----- State container -----
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, KEY, PENDANT, SCALES, ELDERIAN_MONUMENT);
    }

    // ----- Monument pattern -----
    public BlockPattern getMonumentPattern() {
        if (this.elderianMonumentPattern == null) {
            this.elderianMonumentPattern = BlockPatternBuilder.start()
                    .aisle("OIC", "NDN", "~N~")
                    .where('O', BlockInWorld.hasState(bs -> bs.is(ModBlocks.TOTEM_OF_ORDER.get())))
                    .where('I', BlockInWorld.hasState(bs -> bs.is(ModBlocks.CHACHAPOYAN_IDOL.get())))
                    .where('C', BlockInWorld.hasState(bs -> bs.is(ModBlocks.TOTEM_OF_CHAOS.get())))
                    .where('N', BlockInWorld.hasState(bs -> bs.is(ModBlocks.NENDER_BRICK.get())))
                    .where('D', BlockInWorld.hasState(bs -> bs.is(Blocks.DIRT)))
                    .where('~', BlockInWorld.hasState(BlockState::isAir))
                    .build();
        }
        return this.elderianMonumentPattern;
    }
}
