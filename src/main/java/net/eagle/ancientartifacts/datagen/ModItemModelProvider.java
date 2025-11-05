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
    }
}
