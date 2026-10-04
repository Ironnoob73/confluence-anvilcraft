package dev.hail.confluence_anvil_craft.init;

import dev.anvilcraft.lib.v2.registrum.util.entry.RecipeEntry;
import dev.dubhe.anvilcraft.recipe.component.HasCauldronSimple;
import dev.hail.confluence_anvil_craft.recipe.ShimmerTransmutationRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import static dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft.REGISTRUM;

public class CACRecipes {
    /**
     * 汇流来世的微光流体标签（<code>data/confluence/tags/fluid/shimmer.json</code>）。
     * <p>By Deepseek: 该标签同时包含 <code>confluence:shimmer</code> 与 <code>confluence:flowing_shimmer</code>，
     * 汇流来世自身没有为该标签提供 <code>TagKey</code> 常量，因此这里自行构造。</p>
     */
    public static final TagKey<Fluid> SHIMMER_FLUID =
            TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("confluence", "shimmer"));

    /**
     * 微光只作为催化剂：流体谓词要求锅内有 {@code #confluence:shimmer}，<code>consume</code> 为 0，
     * 且没有 <code>transform</code>，因此 {@code HasCauldron#accept} 会直接返回，流体永远不会被抽走。
     */
    public static final HasCauldronSimple SHIMMER_CAULDRON =
            HasCauldronSimple.fluid(SHIMMER_FLUID).consume(0).build();

    public static final RecipeEntry<ShimmerTransmutationRecipe> SHIMMER_TRANSMUTATION =
            REGISTRUM.recipe("shimmer_transmutation",
                    ShimmerTransmutationRecipe.CODEC, ShimmerTransmutationRecipe.STREAM_CODEC);

    public static void register() {
    }
}
