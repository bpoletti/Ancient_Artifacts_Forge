package net.eagle.ancientartifacts.events;

import net.eagle.ancientartifacts.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ShovelUseHandler {

    private ShovelUseHandler() {}

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        final Player player = event.getEntity();
        final InteractionHand hand = event.getHand();
        final ItemStack stack = player.getItemInHand(hand);

        // Only care about shovels in the used hand
        if (!(stack.getItem() instanceof ShovelItem)) return;

        final Level level = event.getLevel();
        final BlockPos pos = event.getPos();
        final BlockState state = level.getBlockState(pos);

        // Mycelium → chance for MYCELIUM_DUST else DIRT, then turn block into DIRT
        if (state.is(Blocks.MYCELIUM)) {
            if (!level.isClientSide) {
                final ServerLevel server = (ServerLevel) level;
                final double r = server.random.nextDouble();

                final ItemStack drop = (r < 0.10)
                        ? new ItemStack(ModItems.MYCELIUM_DUST.get(), 1)
                        : new ItemStack(Blocks.DIRT.asItem(), 1);

                spawnCentered(server, pos.above(), drop);

                // Damage the shovel (1 point), respecting creative
                EquipmentSlot slot = (hand == InteractionHand.MAIN_HAND)
                        ? EquipmentSlot.MAINHAND
                        : EquipmentSlot.OFFHAND;

                if (!player.isCreative()) {
                    stack.hurtAndBreak(1, player, slot);
                }

                server.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
            }

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        // Red Sand → chance for RED_ICE else DEAD_BUSH, then turn block into SAND
        if (state.is(Blocks.RED_SAND)) {
            if (!level.isClientSide) {
                final ServerLevel server = (ServerLevel) level;
                final double r = server.random.nextDouble();

                final ItemStack drop = (r < 0.05)
                        ? new ItemStack(ModItems.RED_ICE.get(), 1)
                        : new ItemStack(Items.DEAD_BUSH, 1);

                spawnCentered(server, pos.above(), drop);
                EquipmentSlot slot = (hand == InteractionHand.MAIN_HAND)
                        ? EquipmentSlot.MAINHAND
                        : EquipmentSlot.OFFHAND;

                if (!player.isCreative()) {
                    stack.hurtAndBreak(1, player, slot);
                }

                server.setBlock(pos, Blocks.SAND.defaultBlockState(), 3);
            }

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    private static void spawnCentered(ServerLevel level, BlockPos pos, ItemStack stack) {
        ItemEntity entity = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                stack
        );
        level.addFreshEntity(entity);
    }
}
