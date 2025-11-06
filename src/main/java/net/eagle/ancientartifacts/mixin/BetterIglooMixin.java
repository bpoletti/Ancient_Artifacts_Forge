package net.eagle.ancientartifacts.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IglooPieces.class)
public class BetterIglooMixin {

    private static final ResourceLocation TOP    = ResourceLocation.withDefaultNamespace("igloo/top");
    private static final ResourceLocation MIDDLE = ResourceLocation.withDefaultNamespace("igloo/middle");
    private static final ResourceLocation BOTTOM = ResourceLocation.withDefaultNamespace("igloo/bottom");

    @Inject(method = "addPieces", at = @At("HEAD"), cancellable = true)
    private static void addPieces(StructureTemplateManager templateManager,
                                  BlockPos pos,
                                  Rotation rotation,
                                  StructurePieceAccessor accessor,
                                  RandomSource random,
                                  CallbackInfo ci) {
        // ~90% chance to generate a basement with random height (4..11 segments)
        if (random.nextDouble() < 0.9) {
            int i = random.nextInt(8) + 4; // 4..11
            accessor.addPiece(new IglooPieces.IglooPiece(templateManager, BOTTOM, pos, rotation, i * 3));
            for (int j = 0; j < i - 1; ++j) {
                accessor.addPiece(new IglooPieces.IglooPiece(templateManager, MIDDLE, pos, rotation, j * 3));
            }
        }

        // Always add the top at offset 0
        accessor.addPiece(new IglooPieces.IglooPiece(templateManager, TOP, pos, rotation, 0));

        ci.cancel();
    }
}
