package net.eagle.ancientartifacts.mixin;

import net.eagle.ancientartifacts.block.ModBlocks;
import net.eagle.ancientartifacts.block.custom.CopperWire;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
public abstract class CopperToRedstoneWire {

    /**
     * Forge/Mojang names (1.21.1):
     *  - Class: net.minecraft.world.level.block.RedStoneWireBlock
     *  - Method: shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z
     *
     * Inject at HEAD; if the neighbor is our CopperWire, we decide connectivity and short-circuit.
     * Otherwise, let vanilla continue.
     */
    @Inject(
            method = "shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void ancientartifacts$connectsToCopper(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        if (dir != null && state.is(ModBlocks.COPPER_WIRE.get())) {
            // Only connect if the rod’s facing matches the queried side.
            // (Adjust this condition if your CopperWire has a different connectivity rule.)
            boolean connect = state.getValue(CopperWire.FACING) == dir;
            cir.setReturnValue(connect);
        }
    }
}
