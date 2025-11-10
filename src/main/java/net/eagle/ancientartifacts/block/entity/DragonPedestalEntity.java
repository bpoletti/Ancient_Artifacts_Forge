package net.eagle.ancientartifacts.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DragonPedestalEntity extends BlockEntity {

    public DragonPedestalEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRAGON_PEDESTAL.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DragonPedestalEntity be) {
        if (level.isClientSide) return;
    }
}