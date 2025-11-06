package net.eagle.ancientartifacts.potion;

import net.eagle.ancientartifacts.AncientArtifacts;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {

    // Forge registry (attach this to the MOD event bus in your main mod class)
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, AncientArtifacts.MOD_ID);

    // === Base elixirs (Nausea 5s @ amp 0; match your Fabric defaults) ===
    public static final RegistryObject<Potion> ELIXIR_BASE_I   = POTIONS.register(
            "elixir_base_i",   () -> new Potion(new MobEffectInstance(MobEffects.CONFUSION, 100, 0))
    );
    public static final RegistryObject<Potion> ELIXIR_BASE_II  = POTIONS.register(
            "elixir_base_ii",  () -> new Potion(new MobEffectInstance(MobEffects.CONFUSION, 100, 0))
    );
    public static final RegistryObject<Potion> ELIXIR_BASE_III = POTIONS.register(
            "elixir_base_iii", () -> new Potion(new MobEffectInstance(MobEffects.CONFUSION, 100, 0))
    );
    public static final RegistryObject<Potion> ELIXIR_BASE_IV  = POTIONS.register(
            "elixir_base_iv",  () -> new Potion(new MobEffectInstance(MobEffects.CONFUSION, 100, 0))
    );
    public static final RegistryObject<Potion> ELIXIR_BASE_V   = POTIONS.register(
            "elixir_base_v",   () -> new Potion(new MobEffectInstance(MobEffects.CONFUSION, 100, 0))
    );

    // === Elixir of Drake (Levitation 10s @ amp 0; match Fabric) ===
    public static final RegistryObject<Potion> ELIXIR_OF_DRAKE = POTIONS.register(
            "elixir_of_drake", () -> new Potion(new MobEffectInstance(MobEffects.LEVITATION, 200, 0))
    );

}
