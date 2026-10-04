package dev.hail.confluence_anvil_craft.event;

import dev.anvilcraft.lib.v2.recipe.InWorldRecipe;
import dev.anvilcraft.lib.v2.recipe.event.InWorldRecipeManagerEvent;
import dev.anvilcraft.lib.v2.recipe.injection.IRecipeManagerExtension;
import dev.anvilcraft.lib.v2.util.predicate.ChanceItemStack;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.dubhe.anvilcraft.api.event.AnvilBehaviorRegisterEvent;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft;
import dev.hail.confluence_anvil_craft.anvil.ShimmerFishTankBehavior;
import dev.hail.confluence_anvil_craft.compat.ConfluenceShimmer;
import dev.hail.confluence_anvil_craft.recipe.ShimmerTransmutationRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.recipe.ItemTransmutationRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * 把汇流来世的微光配方接入铁砧工艺。
 *
 * <p>By Deepseek: 铁砧工艺自己就是用 {@code InWorldRecipeManagerEvent.Init} +
 * {@code RecipeManager#anvillib$addRecipes} 把原版合成/熔炼配方包装成铁砧加工配方的
 * （见 {@code dev.dubhe.anvilcraft.event.InWorldRecipeEventListener} 与 {@code VanillaRecipesWrap}），
 * 这里沿用同一扩展点，把 {@code confluence:item_transmutation} 自动转换成
 * {@code confluence_anvil_craft:shimmer_transmutation}，因此不需要重复维护配方数据。</p>
 */
@EventBusSubscriber(modid = ConfluenceAnvilCraft.MOD_ID)
public final class ShimmerEventHandler {
    private ShimmerEventHandler() {
    }

    @SubscribeEvent
    public static void onInWorldRecipeInit(InWorldRecipeManagerEvent.Init event) {
        RecipeManager recipeManager = event.getRecipeManager();
        if (!(recipeManager instanceof IRecipeManagerExtension extension)) return;

        List<RecipeHolder<ItemTransmutationRecipe>> sources =
                recipeManager.getAllRecipesFor(ModRecipes.ITEM_TRANSMUTATION_TYPE.get());

        List<RecipeHolder<InWorldRecipe>> generated = new ArrayList<>();
        for (RecipeHolder<ItemTransmutationRecipe> holder : sources) {
            ItemTransmutationRecipe recipe = holder.value();
            // 空 target 的条目是黑名单：既不生成配方，也会在行为里阻止分解
            if (!recipe.isValid()) continue;
            // 铁砧加工配方没有世界阶段谓词，只能在生成时过滤；换阶段后 /reload 即可刷新
            if (!ConfluenceShimmer.isPhaseReached(recipe.gamePhase())) continue;

            List<ChanceItemStack> results = recipe.target().stream().map(ChanceItemStack::of).toList();
            if (results.isEmpty()) continue;

            List<ItemIngredientPredicate> ingredients = List.of(
                    ConfluenceShimmer.toIngredient(recipe.source(), recipe.shrink())
            );
            ResourceLocation id = ConfluenceAnvilCraft.of(
                    "shimmer_transmutation/" + holder.id().getNamespace() + "/" + holder.id().getPath());
            generated.add(new RecipeHolder<>(id, new ShimmerTransmutationRecipe(ingredients, results)));
        }

        if (generated.isEmpty()) return;
        extension.anvillib$addRecipes(generated);
        ConfluenceAnvilCraft.LOGGER.info("[{}] Bridged {} shimmer transmutation recipes from {} confluence:item_transmutation recipes",
                ConfluenceAnvilCraft.MOD_ID, generated.size(), sources.size());
    }

    @SubscribeEvent
    public static void onRegisterAnvilBehavior(AnvilBehaviorRegisterEvent event) {
        event.registerBehavior(ModBlocks.FISH_TANK.get(), ShimmerFishTankBehavior.INSTANCE);
        ConfluenceAnvilCraft.LOGGER.info("[{}] Registered shimmer behavior for anvilcraft:fish_tank",
                ConfluenceAnvilCraft.MOD_ID);
    }
}
