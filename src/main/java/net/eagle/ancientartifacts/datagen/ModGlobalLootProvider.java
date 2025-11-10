package net.eagle.ancientartifacts.datagen;

import net.eagle.ancientartifacts.AncientArtifacts;
import net.eagle.ancientartifacts.block.ModBlocks;
import net.eagle.ancientartifacts.item.ModItems;
import net.eagle.ancientartifacts.loot.AddItemModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

/**
 * Datagen: writes global loot modifiers JSON into data/<modid>/forge/loot_modifiers/*.json
 */
public class ModGlobalLootProvider extends GlobalLootModifierProvider {

    public ModGlobalLootProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, AncientArtifacts.MOD_ID, lookup);
    }

    private static LootItemCondition[] only(String lootTableId) {
        return new LootItemCondition[] {
                LootTableIdCondition.builder(ResourceLocation.parse(lootTableId)).build()
        };
    }

    @Override
    protected void start(HolderLookup.Provider registries) {
        // ==== MOBS ====
        add("warden_heart",
                AddItemModifier.of(only("minecraft:entities/warden"),
                        ModItems.WARDEN_HEART.get(), 1.0f, 1, 1));

        add("evoker_key",
                AddItemModifier.of(only("minecraft:entities/evoker"),
                        ModItems.EVOKER_KEY.get(), 1.0f, 1, 1));

        add("elder_guardian_scales",
                AddItemModifier.of(only("minecraft:entities/elder_guardian"),
                        ModItems.ELDER_GUARDIAN_SCALES.get(), 1.0f, 0, 1));

        // ==== STRUCTURE CHESTS ====
        add("igloo_black_ice",
                AddItemModifier.of(only("minecraft:chests/igloo_chest"),
                        ModItems.BLACK_ICE.get(), 0.90f, 1, 1));

        add("mineshaft_dragon_fossil",
                AddItemModifier.of(only("minecraft:chests/abandoned_mineshaft"),
                        ModItems.DRAGON_FOSSIL.get(), 0.40f, 1, 1));

        add("bastion_plate_other",
                AddItemModifier.of(only("minecraft:chests/bastion_other"),
                        ModBlocks.GILDED_PLATE.get(), 0.40f, 1, 1));

        add("bastion_plate_stable",
                AddItemModifier.of(only("minecraft:chests/bastion_hoglin_stable"),
                        ModBlocks.GILDED_PLATE.get(), 0.40f, 1, 1));

        add("bastion_plate_treasure",
                AddItemModifier.of(only("minecraft:chests/bastion_treasure"),
                        ModBlocks.GILDED_PLATE.get(), 0.70f, 1, 1));

        add("desert_pyramid_ankh",
                AddItemModifier.of(only("minecraft:chests/desert_pyramid"),
                        ModItems.ANKH_PENDANT.get(), 0.40f, 1, 1));

        add("jungle_temple_idol",
                AddItemModifier.of(only("minecraft:chests/jungle_temple"),
                        ModBlocks.CHACHAPOYAN_IDOL.get(), 0.70f, 1, 1));

        add("nether_bridge_nender_brick",
                AddItemModifier.of(only("minecraft:chests/nether_bridge"),
                        ModBlocks.NENDER_BRICK.get(), 0.85f, 3, 7));

        add("pillager_outpost_chaos_totem",
                AddItemModifier.of(only("minecraft:chests/pillager_outpost"),
                        ModBlocks.TOTEM_OF_CHAOS.get(), 0.65f, 1, 1));

        add("ruined_portal_nether_grass",
                AddItemModifier.of(only("minecraft:chests/ruined_portal"),
                        ModItems.NETHER_GRASS.get(), 0.40f, 1, 1));

        add("dungeon_dragon_pedestal",
                AddItemModifier.of(only("minecraft:chests/simple_dungeon"),
                        ModBlocks.DRAGON_PEDESTAL.get(), 0.30f, 1, 1));

        add("stronghold_corridor_ender_rod",
                AddItemModifier.of(only("minecraft:chests/stronghold_corridor"),
                        ModItems.ENDER_ROD.get(), 0.80f, 1, 1));

        add("woodland_mansion_evoker_key",
                AddItemModifier.of(only("minecraft:chests/woodland_mansion"),
                        ModItems.EVOKER_KEY.get(), 0.60f, 1, 1));

        // ==== VILLAGES ====
        add("village_armorer_order",
                AddItemModifier.of(only("minecraft:chests/village/village_armorer"),
                        ModBlocks.TOTEM_OF_ORDER.get(), 0.30f, 1, 1));

        add("village_house_order_desert",
                AddItemModifier.of(only("minecraft:chests/village/village_desert_house"),
                        ModBlocks.TOTEM_OF_ORDER.get(), 0.35f, 1, 1));
        add("village_house_order_plains",
                AddItemModifier.of(only("minecraft:chests/village/village_plains_house"),
                        ModBlocks.TOTEM_OF_ORDER.get(), 0.35f, 1, 1));
        add("village_house_order_savanna",
                AddItemModifier.of(only("minecraft:chests/village/village_savanna_house"),
                        ModBlocks.TOTEM_OF_ORDER.get(), 0.35f, 1, 1));
        add("village_house_order_snowy",
                AddItemModifier.of(only("minecraft:chests/village/village_snowy_house"),
                        ModBlocks.TOTEM_OF_ORDER.get(), 0.35f, 1, 1));
        add("village_house_order_taiga",
                AddItemModifier.of(only("minecraft:chests/village/village_taiga_house"),
                        ModBlocks.TOTEM_OF_ORDER.get(), 0.35f, 1, 1));

        add("village_mason_order",
                AddItemModifier.of(only("minecraft:chests/village/village_mason"),
                        ModBlocks.TOTEM_OF_ORDER.get(), 0.30f, 1, 1));

        add("village_temple_order",
                AddItemModifier.of(only("minecraft:chests/village/village_temple"),
                        ModBlocks.TOTEM_OF_ORDER.get(), 0.50f, 1, 1));

        add("village_toolsmith_order",
                AddItemModifier.of(only("minecraft:chests/village/village_toolsmith"),
                        ModBlocks.TOTEM_OF_ORDER.get(), 0.20f, 1, 1));
        add("village_weaponsmith_order",
                AddItemModifier.of(only("minecraft:chests/village/village_weaponsmith"),
                        ModBlocks.TOTEM_OF_ORDER.get(), 0.20f, 1, 1));
    }
}
