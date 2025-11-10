package net.eagle.ancientartifacts.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

public class AddItemModifier extends LootModifier {

    // Forge 1.21.1 expects a MapCodec for GLM serializers
    public static final MapCodec<AddItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst)
                    .and(ItemStack.CODEC.fieldOf("stack").forGetter(m -> m.stack))
                    .and(com.mojang.serialization.Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
                    .and(com.mojang.serialization.Codec.INT.fieldOf("min").forGetter(m -> m.min))
                    .and(com.mojang.serialization.Codec.INT.fieldOf("max").forGetter(m -> m.max))
                    .apply(inst, AddItemModifier::new)
    );

    private final ItemStack stack;
    private final float chance;
    private final int min;
    private final int max;

    public AddItemModifier(LootItemCondition[] conditions, ItemStack stack, float chance, int min, int max) {
        super(conditions);
        this.stack = stack.copy();
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    /** Convenience */
    public static AddItemModifier of(LootItemCondition[] conditions, ItemLike item, float chance, int min, int max) {
        return new AddItemModifier(conditions, new ItemStack(item), chance, min, max);
    }

    @Override
    public com.mojang.serialization.MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }


    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext ctx) {
        if (ctx.getRandom().nextFloat() <= this.chance) {
            int count = (min == max) ? min : (min + ctx.getRandom().nextInt((max - min) + 1));
            if (count > 0) {
                ItemStack drop = this.stack.copy();
                drop.setCount(count);
                generatedLoot.add(drop);
            }
        }
        return generatedLoot;
    }
}
