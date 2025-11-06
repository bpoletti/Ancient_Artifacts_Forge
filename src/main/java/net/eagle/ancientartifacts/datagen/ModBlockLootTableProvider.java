package net.eagle.ancientartifacts.datagen;

import net.eagle.ancientartifacts.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider pRegistries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), pRegistries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.NENDER_BRICK.get());
        dropSelf(ModBlocks.CHACHAPOYAN_IDOL.get());
        dropSelf(ModBlocks.COPPER_WIRE.get());
        dropSelf(ModBlocks.DRAGON_PEDESTAL.get());
        dropSelf(ModBlocks.ETHER_LEVER.get());
        dropSelf(ModBlocks.GILDED_PLATE.get());
        dropSelf(ModBlocks.TOTEM_OF_CHAOS.get());
        dropSelf(ModBlocks.TOTEM_OF_ORDER.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
