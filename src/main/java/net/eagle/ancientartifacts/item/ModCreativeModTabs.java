package net.eagle.ancientartifacts.item;

import net.eagle.ancientartifacts.AncientArtifacts;
import net.eagle.ancientartifacts.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AncientArtifacts.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ANCIENT_ARTIFACTS_TAB =
            CREATIVE_MODE_TABS.register("ancient_artifacts_tab",
                    () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.DRAGON_PEDESTAL.get()))
                    .title(Component.translatable("creativetab.ancientartifacts.ancient_artifacts"))
                    .displayItems((itemDisplayParameters, output) -> {

                        //mod items and blocks
                        output.accept(ModItems.WARDEN_HEART.get());
                        output.accept(ModItems.ANKH_PENDANT.get());
                        output.accept(ModItems.EVOKER_KEY.get());
                        output.accept(ModItems.BLACK_ICE.get());
                        output.accept(ModItems.BLACK_ICE.get());
                        output.accept(ModItems.RED_ICE.get());
                        output.accept(ModItems.DRAGON_FOSSIL.get());
                        output.accept(ModBlocks.DRAGON_PEDESTAL.get());
                        output.accept(ModItems.END_STAFF.get());
                        output.accept(ModItems.ENDER_ROD.get());
                        output.accept(ModItems.PEARLESCENT_FIREFLY_BUD.get());
                        output.accept(ModItems.OCHRE_FIREFLY_BUD.get());
                        output.accept(ModItems.VERDANT_FIREFLY_BUD.get());
                        output.accept(ModItems.MYCELIUM_DUST.get());
                        output.accept(ModItems.NETHER_GRASS.get());
                        output.accept(ModBlocks.CHACHAPOYAN_IDOL.get());
                        output.accept(ModBlocks.TOTEM_OF_ORDER.get());
                        output.accept(ModBlocks.TOTEM_OF_CHAOS.get());
                        output.accept(ModItems.ORB_INFINIUM.get());
                        output.accept(ModBlocks.NENDER_BRICK.get());
                        output.accept(ModBlocks.GILDED_PLATE.get());
                        output.accept(ModBlocks.ETHER_LEVER.get());
                        output.accept(ModBlocks.COPPER_WIRE.get());

                        output.accept(Items.ENDER_EYE);
                        output.accept(Items.HEART_OF_THE_SEA);
                        output.accept(Items.DIRT);
                        output.accept(Items.NETHER_STAR);
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
