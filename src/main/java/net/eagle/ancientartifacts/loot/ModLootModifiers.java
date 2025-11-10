package net.eagle.ancientartifacts.loot;

import com.mojang.serialization.MapCodec;
import net.eagle.ancientartifacts.AncientArtifacts;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.loot.IGlobalLootModifier;

public final class ModLootModifiers {
    private ModLootModifiers() {}

    // NOTE: MapCodec here (Forge 1.21.1)
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AncientArtifacts.MOD_ID);

    public static final RegistryObject<MapCodec<AddItemModifier>> ADD_ITEM =
            SERIALIZERS.register("add_item", () -> AddItemModifier.CODEC);

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
    }
}
