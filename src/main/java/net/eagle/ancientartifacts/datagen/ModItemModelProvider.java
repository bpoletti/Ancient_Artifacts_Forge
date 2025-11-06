package net.eagle.ancientartifacts.datagen;

import net.eagle.ancientartifacts.AncientArtifacts;
import net.eagle.ancientartifacts.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AncientArtifacts.MOD_ID,existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.WARDEN_HEART.get());
        basicItem(ModItems.ANKH_PENDANT.get());
        basicItem(ModItems.BLACK_ICE.get());
        basicItem(ModItems.RED_ICE.get());
        basicItem(ModItems.MYCELIUM_DUST.get());
        basicItem(ModItems.ELDER_GUARDIAN_SCALES.get());
        basicItem(ModItems.EVOKER_KEY.get());
        basicItem(ModItems.FIREFLY_ORB.get());
        basicItem(ModItems.ENDER_ROD.get());
        basicItem(ModItems.NETHER_GRASS.get());
        basicItem(ModItems.OCHRE_FIREFLY_BUD.get());
        basicItem(ModItems.PEARLESCENT_FIREFLY_BUD.get());
        basicItem(ModItems.VERDANT_FIREFLY_BUD.get());
        basicItem(ModItems.ORB_INFINIUM.get());
    }
}
