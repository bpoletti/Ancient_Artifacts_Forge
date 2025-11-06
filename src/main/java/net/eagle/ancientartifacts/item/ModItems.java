package net.eagle.ancientartifacts.item;

import net.eagle.ancientartifacts.AncientArtifacts;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AncientArtifacts.MOD_ID);

    public static final RegistryObject<Item> WARDEN_HEART = ITEMS.register("warden_heart",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ENDER_ROD = ITEMS.register("ender_rod",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MYCELIUM_DUST = ITEMS.register("mycelium_dust",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BLACK_ICE = ITEMS.register("black_ice",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RED_ICE = ITEMS.register("red_ice",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> NETHER_GRASS = ITEMS.register("nether_grass",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> OCHRE_FIREFLY_BUD = ITEMS.register("ochre_firefly_bud",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> VERDANT_FIREFLY_BUD = ITEMS.register("verdant_firefly_bud",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PEARLESCENT_FIREFLY_BUD = ITEMS.register("pearlescent_firefly_bud",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FIREFLY_ORB = ITEMS.register("firefly_orb",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> EVOKER_KEY = ITEMS.register("evoker_key",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ELDER_GUARDIAN_SCALES = ITEMS.register("elder_guardian_scales",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> END_STAFF = ITEMS.register("end_staff",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ANKH_PENDANT = ITEMS.register("ankh_pendant",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ORB_INFINIUM = ITEMS.register("orb_infinium",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> DRAGON_FOSSIL = ITEMS.register("dragon_fossil",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
