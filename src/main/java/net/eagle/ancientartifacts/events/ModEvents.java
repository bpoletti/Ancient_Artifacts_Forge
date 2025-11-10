package net.eagle.ancientartifacts.events;

import net.eagle.ancientartifacts.AncientArtifacts;
import net.eagle.ancientartifacts.item.ModItems;
import net.eagle.ancientartifacts.potion.ModPotions;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.event.brewing.BrewingRecipeRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AncientArtifacts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onBrewingRecipeRegister(BrewingRecipeRegisterEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        //Elixir Base I
        builder.addMix(Potions.AWKWARD,
                ModItems.NETHER_GRASS.get(),
                ModPotions.ELIXIR_BASE_I.getHolder().get());
        //Elixir Base II
        builder.addMix(ModPotions.ELIXIR_BASE_I.getHolder().get(),
                ModItems.MYCELIUM_DUST.get(),
                ModPotions.ELIXIR_BASE_II.getHolder().get());
        //Elixir Base III
        builder.addMix(ModPotions.ELIXIR_BASE_II.getHolder().get(),
                ModItems.BLACK_ICE.get(),
                ModPotions.ELIXIR_BASE_III.getHolder().get());
        //Elixir Base IV
        builder.addMix(ModPotions.ELIXIR_BASE_III.getHolder().get(),
                ModItems.RED_ICE.get(),
                ModPotions.ELIXIR_BASE_IV.getHolder().get());
        //Elixir Base V
        builder.addMix(ModPotions.ELIXIR_BASE_IV.getHolder().get(),
                ModItems.FIREFLY_ORB.get(),
                ModPotions.ELIXIR_BASE_V.getHolder().get());
        //Elixir of Drake
        builder.addMix(ModPotions.ELIXIR_BASE_V.getHolder().get(),
                ModItems.WARDEN_HEART.get(),
                ModPotions.ELIXIR_OF_DRAKE.getHolder().get());
    }
}
