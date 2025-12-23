package net.eagle.ancientartifacts.datagen;

import net.eagle.ancientartifacts.AncientArtifacts;
import net.eagle.ancientartifacts.block.ModBlocks;
import net.eagle.ancientartifacts.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.COPPER_WIRE.get())
                .pattern(" C ")
                .pattern(" R ")
                .pattern(" C ")
                .define('C', Items.COPPER_INGOT)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .define('R', Items.LIGHTNING_ROD)
                .unlockedBy(getHasName(Items.LIGHTNING_ROD), has(Items.LIGHTNING_ROD))
                .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.END_STAFF.get())
                .group("end_staff")
                .pattern("  I")
                .pattern(" S ")
                .pattern("E  ")
                .define('I', Items.ENDER_EYE)
                .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                .define('S', Items.NETHER_STAR)
                .unlockedBy(getHasName(Items.NETHER_STAR), has(Items.NETHER_STAR))
                .define('E', ModItems.ENDER_ROD.get())
                .unlockedBy(getHasName(ModItems.ENDER_ROD.get()), has(ModItems.ENDER_ROD.get()))
                .save(pRecipeOutput, AncientArtifacts.MOD_ID + ":end_staff_right");

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.END_STAFF.get())
                .group("end_staff")
                .pattern("I  ")
                .pattern(" S ")
                .pattern("  E")
                .define('I', Items.ENDER_EYE)
                .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                .define('S', Items.NETHER_STAR)
                .unlockedBy(getHasName(Items.NETHER_STAR), has(Items.NETHER_STAR))
                .define('E', ModItems.ENDER_ROD.get())
                .unlockedBy(getHasName(ModItems.ENDER_ROD.get()), has(ModItems.ENDER_ROD.get()))
                .save(pRecipeOutput, AncientArtifacts.MOD_ID + ":end_staff_left");

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.ETHER_LEVER.get())
                .pattern(" W ")
                .pattern(" B ")
                .pattern(" E ")
                .define('W', Items.WITHER_SKELETON_SKULL)
                .unlockedBy(getHasName(Items.WITHER_SKELETON_SKULL), has(Items.WITHER_SKELETON_SKULL))
                .define('B', Items.BLAZE_ROD)
                .unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
                .define('E', Items.ECHO_SHARD)
                .unlockedBy(getHasName(Items.ECHO_SHARD), has(Items.ECHO_SHARD))
                .save(pRecipeOutput);

        smelt(pRecipeOutput, Items.OCHRE_FROGLIGHT,      ModItems.OCHRE_FIREFLY_BUD.get(),      "ochre_firefly_bud_from_ochre_froglight");
        smelt(pRecipeOutput, Items.PEARLESCENT_FROGLIGHT,ModItems.PEARLESCENT_FIREFLY_BUD.get(),"pearlescent_firefly_bud_from_pearlescent_froglight");
        smelt(pRecipeOutput, Items.VERDANT_FROGLIGHT,    ModItems.VERDANT_FIREFLY_BUD.get(),    "verdant_firefly_bud_from_verdant_froglight");

        fireflyOrbPermutations(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.NENDER_BRICK.get())
                .pattern("NW ")
                .pattern("WN ")
                .pattern("   ")
                .define('N', Items.NETHER_BRICK)
                .unlockedBy(getHasName(Items.NETHER_BRICK), has(Items.NETHER_BRICK))
                .define('W', Items.WARPED_FUNGUS)
                .unlockedBy(getHasName(Items.WARPED_FUNGUS), has(Items.WARPED_FUNGUS))
                .save(pRecipeOutput);

    }

    private void smelt(RecipeOutput out, ItemLike input, ItemLike outputItem, String fileName) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, outputItem, 0.1f, 160)
                .group("firefly_buds")
                .unlockedBy(getHasName(input), has(input))
                .save(out, id(fileName));
    }

    private void fireflyOrbPermutations(RecipeOutput out) {
        final ItemLike G = net.minecraft.world.level.block.Blocks.GLASS.asItem();
        final ItemLike[] buds = new ItemLike[] {
                ModItems.OCHRE_FIREFLY_BUD.get(),      // O
                ModItems.VERDANT_FIREFLY_BUD.get(),    // V
                ModItems.PEARLESCENT_FIREFLY_BUD.get() // P
        };

        int[][] perms = {
                {0,1,2},{0,2,1},{1,0,2},
                {1,2,0},{2,0,1},{2,1,0}
        };

        for (int[] p : perms) {
            int a = p[0], b = p[1], c = p[2];

            ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.FIREFLY_ORB.get())
                    .group("firefly_orb")
                    .pattern("GGG")
                    .pattern("OVP")
                    .pattern("GGG")
                    .define('G', G)
                    .define('O', buds[a])
                    .define('V', buds[b])
                    .define('P', buds[c])
                    .unlockedBy(getHasName(G), has(G))
                    .unlockedBy(getHasName(buds[0]), has(buds[0]))
                    .unlockedBy(getHasName(buds[1]), has(buds[1]))
                    .unlockedBy(getHasName(buds[2]), has(buds[2]))
                    .save(out, id("firefly_orb_" + a + b + c));
        }
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AncientArtifacts.MOD_ID, path);
    }
}
