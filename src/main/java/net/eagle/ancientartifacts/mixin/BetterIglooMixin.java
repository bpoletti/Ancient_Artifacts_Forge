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

    @Inject(
            method =
                    "addPieces(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;" +
                            "Lnet/minecraft/core/BlockPos;" +
                            "Lnet/minecraft/world/level/block/Rotation;" +
                            "Lnet/minecraft/world/level/levelgen/structure/StructurePieceAccessor;" +
                            "Lnet/minecraft/util/RandomSource;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void ancientartifacts$heavierBasements(StructureTemplateManager templates,
                                                          BlockPos pos,
                                                          Rotation rot,
                                                          StructurePieceAccessor pieces,
                                                          RandomSource rnd,
                                                          CallbackInfo ci) {
        // ~90% chance: add basement stack first
        if (rnd.nextDouble() < 0.9) {
            int sections = rnd.nextInt(8) + 4; // 4..11
            pieces.addPiece(new IglooPieces.IglooPiece(templates, BOTTOM, pos, rot, sections * 3));
            for (int i = 0; i < sections - 1; ++i) {
                pieces.addPiece(new IglooPieces.IglooPiece(templates, MIDDLE, pos, rot, i * 3));
            }
        }

        // Always add the top at offset 0
        pieces.addPiece(new IglooPieces.IglooPiece(templates, TOP, pos, rot, 0));

        ci.cancel();
    }
}
