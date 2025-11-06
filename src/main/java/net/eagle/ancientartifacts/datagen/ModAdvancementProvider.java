package net.eagle.ancientartifacts.datagen;

import net.eagle.ancientartifacts.AncientArtifacts;
import net.eagle.ancientartifacts.block.ModBlocks;
import net.eagle.ancientartifacts.item.ModItems;
import net.eagle.ancientartifacts.potion.ModPotions;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, List.of(new ModAdvancements()));
    }

    private static class ModAdvancements implements AdvancementSubProvider {
        @Override
        public void generate(HolderLookup.Provider lookup, Consumer<AdvancementHolder> out) {

            // ---- ROOT ----
            ResourceLocation bg = ResourceLocation.fromNamespaceAndPath(
                    AncientArtifacts.MOD_ID, "textures/block/nender_brick.png");

            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(
                            ModBlocks.CHACHAPOYAN_IDOL.get().asItem(),
                            Component.literal("Not Today Dr. Jones!"),
                            Component.literal("Found the Chachapoyan Idol"),
                            bg,
                            AdvancementType.TASK,
                            true,   // show toast
                            true,   // announce
                            false   // hidden
                    )
                    .addCriterion("golden_head",
                            InventoryChangeTrigger.TriggerInstance.hasItems(
                                    ModBlocks.CHACHAPOYAN_IDOL.get().asItem()))
                    .save(out, AncientArtifacts.MOD_ID + "/root");

            // ---- EVOKER KEY ----
            AdvancementHolder keyToEverything = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            ModItems.EVOKER_KEY.get(),
                            Component.literal("The Key to Everything!"),
                            Component.literal("Found the Evoker's Key"),
                            null, AdvancementType.TASK, true, true, false
                    )
                    .addCriterion("key_nabbed",
                            InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.EVOKER_KEY.get()))
                    .save(out, AncientArtifacts.MOD_ID + "/evoker_key");

            // ---- FIREFLY ORB ----
            AdvancementHolder ballOfStars = Advancement.Builder.advancement()
                    .parent(keyToEverything)
                    .display(
                            ModItems.FIREFLY_ORB.get(),
                            Component.literal("Ball of Stars"),
                            Component.literal("Crafted the Firefly Orb"),
                            null, AdvancementType.TASK, true, true, false
                    )
                    .addCriterion("star_orb",
                            InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.FIREFLY_ORB.get()))
                    .save(out, AncientArtifacts.MOD_ID + "/firefly_orb");

            // ---- ELIXIR OF DRAKE (brewed) ----
            // icon only (nice touch; not used for predicate)
            PotionContents elixirContents = new PotionContents(ModPotions.ELIXIR_OF_DRAKE.getHolder().get());

            // 2. Create the display stack for the advancement icon
            ItemStack drakeStackIcon = new ItemStack(Items.POTION);
            drakeStackIcon.set(DataComponents.POTION_CONTENTS, elixirContents);

            AdvancementHolder drakesPotion = Advancement.Builder.advancement()
                    .parent(ballOfStars)
                    .display(
                            drakeStackIcon,
                            Component.literal("Taste Like Crap!"),
                            Component.literal("Brewed the Elixir of Drake"),
                            null, AdvancementType.TASK, true, true, false
                    )
                    .addCriterion(
                            "drake_potion",
                            InventoryChangeTrigger.TriggerInstance.hasItems(
                                    ItemPredicate.Builder.item()
                                            // Specify the base item type
                                            .of(Items.POTION)
                                            // Use hasComponents() to define the required data components
                                            .hasComponents(DataComponentPredicate.builder()
                                                    .expect(DataComponents.POTION_CONTENTS, elixirContents)
                                                    .build())
                                            .build()
                            )
                    )
                    .save(out, AncientArtifacts.MOD_ID + "/elixir_of_drake");

            // ---- END STAFF ----
            AdvancementHolder magicStaff = Advancement.Builder.advancement()
                    .parent(drakesPotion)
                    .display(
                            ModItems.END_STAFF.get(),
                            Component.literal("Wingardium Leviosa"),
                            Component.literal("Crafted the End Staff"),
                            null, AdvancementType.TASK, true, true, false
                    )
                    .addCriterion("end_staff",
                            InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.END_STAFF.get()))
                    .save(out, AncientArtifacts.MOD_ID + "/end_staff");

            // ---- MONUMENT OPENED (use staff on pedestal) + 500 XP ----
            // ItemUsedOnBlockTrigger -> renamed to ItemInteractWithBlockTrigger in 1.21.x
            AdvancementHolder monumentOpened = Advancement.Builder.advancement()
                    .parent(magicStaff)
                    .display(
                            ModItems.ORB_INFINIUM.get(),
                            Component.literal("§5The Beginning of the End?"),
                            Component.literal("The Elderian Monument was activated and the End Gate has opened"),
                            null, AdvancementType.GOAL, true, true, false
                    )
                    .addCriterion(
                            "pedestal_final",
                            ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                                    // location predicate: block == DRAGON_PEDESTAL
                                    LocationPredicate.Builder.location()
                                            .setBlock(BlockPredicate.Builder.block()
                                                    .of(ModBlocks.DRAGON_PEDESTAL.get())   // see #3 below re: .build()
                                            ),
                                    // item predicate: END_STAFF
                                    ItemPredicate.Builder.item().of(ModItems.END_STAFF.get())
                            )
                    )
                    .rewards(AdvancementRewards.Builder.experience(500))
                    .save(out, AncientArtifacts.MOD_ID + "/monument_opened");
        }
    }
}
