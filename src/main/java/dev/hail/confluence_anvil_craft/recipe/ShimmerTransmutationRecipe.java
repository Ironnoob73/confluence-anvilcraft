package dev.hail.confluence_anvil_craft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.anvilcraft.lib.v2.util.predicate.BlockStatePredicate;
import dev.anvilcraft.lib.v2.util.predicate.ChanceItemStack;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.AbstractProcessRecipe;
import dev.hail.confluence_anvil_craft.init.CACRecipes;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 微光处理配方：铁砧砸在盛有汇流来世微光（{@code #confluence:shimmer}）的<b>鱼缸</b>上，
 * 把其中的物品转化为产物。微光是催化剂，不会被消耗。
 *
 * <p>By Deepseek: 几何参数（物品输入/输出偏移、输入范围、炼药锅偏移）与 {@code anvilcraft:solid_liquid}
 * 的反汇编结果完全一致，保证物品的搜索与产出位置和原版固液反应一致。</p>
 *
 * <p>环境额外要求铁砧正下方是 {@code anvilcraft:fish_tank}：汇流来世本身已经改造过炼药锅
 * （物品丢入即执行微光配方），所以这里刻意只接管「铁砧砸鱼缸」这一条路径。</p>
 *
 * <p>大型炼药锅由 {@code ShimmerReactionBehavior} 走行为路径处理，刻意<b>不</b>匹配本配方类型，
 * 避免它在自己的 {@code handleGiantAnvilImpact} 里再处理一遍。</p>
 *
 * <p>JSON 只暴露 <code>ingredients</code> 与 <code>results</code>：环境条件（微光流体 + 鱼缸）
 * 由配方类型本身固定，避免数据包误用为其它流体反应。</p>
 */
public class ShimmerTransmutationRecipe extends AbstractProcessRecipe<ShimmerTransmutationRecipe> {
    public static final MapCodec<ShimmerTransmutationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemIngredientPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of())
                    .forGetter(AbstractProcessRecipe::getInputItems),
            ChanceItemStack.CODEC.listOf().optionalFieldOf("results", List.of())
                    .forGetter(AbstractProcessRecipe::getResultItems)
    ).apply(instance, ShimmerTransmutationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShimmerTransmutationRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ItemIngredientPredicate.STREAM_CODEC.apply(ByteBufCodecs.list()), AbstractProcessRecipe::getInputItems,
                    ChanceItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), AbstractProcessRecipe::getResultItems,
                    ShimmerTransmutationRecipe::new);

    public ShimmerTransmutationRecipe(List<ItemIngredientPredicate> ingredients, List<ChanceItemStack> results) {
        this(ingredients, results, Integer.MAX_VALUE);
    }

    private ShimmerTransmutationRecipe(List<ItemIngredientPredicate> ingredients, List<ChanceItemStack> results, int maxEfficiency) {
        super(createProperty(ingredients, results), maxEfficiency);
    }

    private static Property createProperty(List<ItemIngredientPredicate> ingredients, List<ChanceItemStack> results) {
        // By Deepseek: 必须在这里（配方反序列化时）才解析方块，不能在静态字段里取 DeferredHolder，
        // 否则类加载发生在模组构造阶段，此时注册表尚未填充。
        BlockStatePredicate fishTank = BlockStatePredicate.builder().of(ModBlocks.FISH_TANK.get()).build();
        return new Property()
                .setItemInputOffset(new Vec3(0.0, -0.375, 0.0))
                .setItemInputRange(new Vec3(0.75, 0.75, 0.75))
                .setInputItems(ingredients)
                .setItemOutputOffset(new Vec3(0.0, -0.75, 0.0))
                .setResultItems(results)
                .setCauldronOffset(new Vec3i(0, -1, 0))
                .setHasCauldron(CACRecipes.SHIMMER_CAULDRON)
                .setBlockInputOffset(new Vec3i(0, -1, 0))
                .setInputBlocks(fishTank)
                .setConsumeInputBlocks(false);
    }

    @Override
    public RecipeSerializer<ShimmerTransmutationRecipe> getSerializer() {
        return CACRecipes.SHIMMER_TRANSMUTATION.serializer().get();
    }

    @Override
    public RecipeType<ShimmerTransmutationRecipe> getType() {
        return CACRecipes.SHIMMER_TRANSMUTATION.type().get();
    }
}
