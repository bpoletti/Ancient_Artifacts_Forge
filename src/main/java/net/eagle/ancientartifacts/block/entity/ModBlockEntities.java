package net.eagle.ancientartifacts.block.entity;

import net.eagle.ancientartifacts.AncientArtifacts; // your MOD_ID holder
import net.eagle.ancientartifacts.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AncientArtifacts.MOD_ID);

    public static final RegistryObject<BlockEntityType<DragonPedestalEntity>> DRAGON_PEDESTAL =
            BLOCK_ENTITIES.register("dragon_pedestal",
                    () -> BlockEntityType.Builder.of(
                            DragonPedestalEntity::new,
                            ModBlocks.DRAGON_PEDESTAL.get()   // the block(s) this BE attaches to
                    ).build(null)
            );
}
