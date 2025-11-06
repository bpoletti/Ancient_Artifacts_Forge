package net.eagle.ancientartifacts.mixin;

import net.eagle.ancientartifacts.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShovelItem.class)
public abstract class ShovelItemMixin {

    @Inject(
            method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ancientartifacts$customDigDrops(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = ctx.getItemInHand();
        Player player = ctx.getPlayer(); // can be null on automation—null-check

        // --- MYCELIUM -> chance for MYCELIUM_DUST else DIRT item; set block to DIRT
        if (state.is(Blocks.MYCELIUM)) {
            double r = level.random.nextDouble();
            ItemStack drop = (r < 0.10)
                    ? new ItemStack(ModItems.MYCELIUM_DUST.get())
                    : new ItemStack(Blocks.DIRT.asItem());

            BlockPos dropPos = pos.above();
            level.addFreshEntity(new ItemEntity(level,
                    pos.getX() + 0.5, dropPos.getY(), pos.getZ() + 0.5,
                    drop));

            // damage shovel if player exists & not creative
            if (player != null && !player.getAbilities().instabuild) {
                damageHeldTool(stack, player, ctx.getHand());
            }

            // replace block with DIRT
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);

            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
            cir.cancel();
            return;
        }

        // --- RED_SAND -> chance for RED_ICE else DEAD_BUSH item; set block to SAND
        if (state.is(Blocks.RED_SAND)) {
            double r = level.random.nextDouble();
            ItemStack drop = (r < 0.05)
                    ? new ItemStack(ModItems.RED_ICE.get())
                    : new ItemStack(Items.DEAD_BUSH);

            BlockPos dropPos = pos.above();
            level.addFreshEntity(new ItemEntity(level,
                    pos.getX() + 0.5, dropPos.getY(), pos.getZ() + 0.5,
                    drop));

            if (player != null && !player.getAbilities().instabuild) {
                damageHeldTool(stack, player, ctx.getHand());
            }

            level.setBlock(pos, Blocks.SAND.defaultBlockState(), 3);

            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
            cir.cancel();
        }
    }

    private static void damageHeldTool(ItemStack stack, Player player, InteractionHand hand) {
        // Convert hand → slot for break animation
        EquipmentSlot slot = (hand == InteractionHand.MAIN_HAND)
                ? EquipmentSlot.MAINHAND
                : EquipmentSlot.OFFHAND;

        // Damage 1, play break animation if it breaks
        stack.hurtAndBreak(1, player, slot);
    }
}
