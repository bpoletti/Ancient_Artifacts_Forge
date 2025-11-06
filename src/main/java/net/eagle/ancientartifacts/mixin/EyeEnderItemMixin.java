package net.eagle.ancientartifacts.mixin;

import net.eagle.ancientartifacts.block.ModBlocks;
import net.eagle.ancientartifacts.block.custom.ChachapoyanIdol;
import net.eagle.ancientartifacts.block.custom.DragonPedestal;
import net.eagle.ancientartifacts.block.custom.EtherLever;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public abstract class EyeEnderItemMixin {

    @Inject(
            method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ancientartifacts$gateEyeUse(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);

        boolean foundIdol = findNearbyWithProperty(
                level, pos, ModBlocks.CHACHAPOYAN_IDOL.get(), ChachapoyanIdol.ELDERIAN_MONUMENT, true, 12, 7, 12
        );
        boolean foundLever = findNearbyWithProperty(
                level, pos, ModBlocks.ETHER_LEVER.get(), EtherLever.POWERED, true, 12, 7, 12
        );
        boolean foundPedestal = findNearbyWithProperty(
                level, pos, ModBlocks.DRAGON_PEDESTAL.get(), DragonPedestal.END_READY, true, 12, 7, 12
        );

        // If frame was clicked but the monument sequence isn't complete, block the Eye usage here.
        if (state.is(Blocks.END_PORTAL_FRAME) && !(foundIdol && foundLever && foundPedestal)) {
            cir.setReturnValue(InteractionResult.PASS); // don't place the Eye / consume item
            cir.cancel();
        }
    }

    private static boolean findNearbyWithProperty(
            Level level, BlockPos center, Block target,
            BooleanProperty prop, boolean requiredValue,
            int rxz, int ryDown, int ryUp
    ) {
        for (int dx = -rxz; dx <= rxz; dx++) {
            for (int dy = -ryDown; dy <= ryUp; dy++) {
                for (int dz = -rxz; dz <= rxz; dz++) {
                    BlockPos p = center.offset(dx, dy, dz);
                    BlockState s = level.getBlockState(p);
                    if (s.is(target) && s.hasProperty(prop) && s.getValue(prop) == requiredValue) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
