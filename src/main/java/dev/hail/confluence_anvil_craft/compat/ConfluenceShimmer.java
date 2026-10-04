package dev.hail.confluence_anvil_craft.compat;

import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.dubhe.anvilcraft.block.entity.LargeCauldronBlockEntity;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.hail.confluence_anvil_craft.init.CACRecipes;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.recipe.ItemTransmutationRecipe;
import org.confluence.mod.mixed.IMinecraftServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 汇流来世「微光」相关的桥接逻辑。
 *
 * <p>By Deepseek: 汇流来世把微光物品交互全部实现在
 * {@code org.confluence.mod.mixin.world.entity.item.ItemEntityMixin#confluence$initTargets} 这个
 * <b>私有静态</b>方法里，没有可复用的公开 API，因此这里按字节码逐条复刻它的判定顺序：</p>
 * <ol>
 *   <li>先查 {@code confluence:item_transmutation}（微光转化）；空 target 的配方是黑名单，
 *       会同时阻止转化与分解；配方自带的世界阶段限制由 {@code matches} 处理；</li>
 *   <li>否则在 {@code CommonConfigs.SHIMMER_DECOMPOSE} 开启时逆向扫描所有合成配方（微光分解）。</li>
 * </ol>
 *
 * <p>数量语义与本体一致：一次处理<b>整组</b>物品，{@code times = 数量 / shrink}，
 * 产出乘以 {@code times} 并按最大堆叠拆分。</p>
 */
public final class ConfluenceShimmer {
    /**
     * 单次结算最多处理多少「组」。
     * <p>By Deepseek: 这是一个防御性上限，正常内容永远碰不到——鱼缸每格最多 64 个，
     * 大型炼药锅每个输入格最多 9 组（576 个）。保留它是为了防止某个容器实现返回超大的堆叠
     * 时，一次结算产出成千上万个 ItemStack。</p>
     */
    public static final int MAX_CRAFTS_PER_PASS = 576;

    private ConfluenceShimmer() {
    }

    public static boolean isShimmer(FluidStack stack) {
        return !stack.isEmpty() && stack.is(CACRecipes.SHIMMER_FLUID);
    }

    /** 大型炼药锅是分层流体容器，任意一层是微光即可。 */
    public static boolean hasShimmer(LargeCauldronBlockEntity cauldron) {
        for (FluidStack stack : cauldron.getFluids().copyFluids()) {
            if (isShimmer(stack)) return true;
        }
        return false;
    }

    /**
     * 解析大型炼药锅主体。
     * <p>普通铁砧落地时 {@code AnvilEventListener} 已经把多方块坐标归一到主体，
     * 但巨型铁砧分支在归一之前就调用了行为，所以这里再兜一层 {@link LargeCauldronBlockEntity#getMain}。</p>
     */
    public static LargeCauldronBlockEntity largeCauldron(ServerLevel level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof LargeCauldronBlockEntity cauldron) {
            LargeCauldronBlockEntity main = cauldron.getMainPart();
            return main == null ? cauldron : main;
        }
        if (state.is(ModBlocks.LARGE_CAULDRON.get())) {
            return LargeCauldronBlockEntity.getMain(level, pos, state);
        }
        return null;
    }

    /** 世界阶段是否已达到配方要求（对应 {@code ItemTransmutationRecipe#matches} 里的判断）。 */
    public static boolean isPhaseReached(GamePhase phase) {
        return KillBoard.INSTANCE.getGamePhase().isAtLeast(phase);
    }

    /** 一次处理消耗的输入数量，以及随之产出的物品。 */
    public record Result(int consume, List<ItemStack> outputs) {
    }

    /**
     * 复刻 {@code ItemEntityMixin#confluence$initTargets}：整组解析该物品的微光反应。
     *
     * @return {@code null} 表示什么也不该发生（黑名单、未开启分解、或没有匹配的配方）
     */
    public static Result resolve(ServerLevel level, ItemStack stack) {
        if (stack.isEmpty()) return null;

        // 1) 微光转化。只要命中一条 item_transmutation 就由它接管：
        //    黑名单（空 target）命中时转化与分解都不做，与本体 return 的语义一致。
        List<RecipeHolder<ItemTransmutationRecipe>> found = level.getRecipeManager()
                .getRecipesFor(ModRecipes.ITEM_TRANSMUTATION_TYPE.get(), new SingleRecipeInput(stack), level);
        for (RecipeHolder<ItemTransmutationRecipe> holder : found) {
            ItemTransmutationRecipe recipe = holder.value();
            if (!recipe.isValid()) return null;
            int times = Math.min(stack.getCount() / recipe.shrink(), MAX_CRAFTS_PER_PASS);
            if (times <= 0) return null;
            List<ItemStack> outputs = new ArrayList<>();
            for (ItemStack target : recipe.target()) {
                addSplit(outputs, target, target.getCount() * times);
            }
            return outputs.isEmpty() ? null : new Result(recipe.shrink() * times, List.copyOf(outputs));
        }

        // 2) 微光分解
        return decompose(level, stack);
    }

    /**
     * 复刻汇流来世的「微光分解」：扫描全部配方，找到「产物正好是输入物品」的那条，
     * 把它的每个原料槽各随机取一个物品作为产物，整组处理。
     *
     * @return 没有匹配到配方（或未开启微光分解）时返回 {@code null}
     */
    private static Result decompose(ServerLevel level, ItemStack stack) {
        if (stack.getDamageValue() != 0) return null;
        if (!CommonConfigs.SHIMMER_DECOMPOSE.get()) return null;
        MinecraftServer server = level.getServer();
        if (server == null) return null;

        HolderLookup.Provider registries = level.registryAccess();
        boolean hardmode = IMinecraftServer.isHardmode(server);

        for (RecipeHolder<?> holder : server.getRecipeManager().getRecipes()) {
            Recipe<?> recipe = holder.value();
            if (recipe.isSpecial() || recipe.isIncomplete()) continue;
            if (recipe instanceof AbstractCookingRecipe) continue;
            ItemStack result = recipe.getResultItem(registries);
            if (result.isEmpty()) continue;
            if (stack.getCount() < result.getCount()) continue;
            if (!ItemStack.isSameItem(stack, result)) continue;

            int times = Math.min(stack.getCount() / result.getCount(), MAX_CRAFTS_PER_PASS);
            List<ItemStack> outputs = new ArrayList<>();
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.isEmpty()) continue;
                ItemStack[] candidates = ingredient.getItems();
                if (candidates.length == 0) continue;
                if (!hardmode && Arrays.stream(candidates).allMatch(candidate -> candidate.is(ModTags.Items.HARDMODE))) {
                    continue;
                }
                ItemStack chosen = Util.getRandom(candidates, level.random);
                if (!hardmode && chosen.is(ModTags.Items.HARDMODE)) {
                    int i = 0;
                    while (i < candidates.length && chosen.is(ModTags.Items.HARDMODE)) {
                        chosen = candidates[i];
                        i++;
                    }
                    if (chosen.is(ModTags.Items.HARDMODE)) continue;
                }
                if (chosen.getItem().hasCraftingRemainingItem(chosen)) continue;
                addSplit(outputs, chosen, chosen.getCount() * times);
            }
            if (outputs.isEmpty()) continue;
            return new Result(result.getCount() * times, List.copyOf(outputs));
        }
        return null;
    }

    /** 按最大堆叠数量拆分产出，与汇流来世本体一致。 */
    private static void addSplit(List<ItemStack> outputs, ItemStack template, int amount) {
        int max = template.getMaxStackSize();
        while (amount > max) {
            outputs.add(template.copyWithCount(max));
            amount -= max;
        }
        if (amount > 0) {
            outputs.add(template.copyWithCount(amount));
        }
    }

    /**
     * 把原版 {@link Ingredient} 转换成铁砧工艺的 {@link ItemIngredientPredicate}，尽量保留标签语义
     * （单个 {@code TagValue} 直接转成标签谓词，避免在配方加载期展开标签）。
     */
    public static ItemIngredientPredicate toIngredient(Ingredient ingredient, int count) {
        Ingredient.Value[] values = ingredient.getValues();
        if (values.length == 1) {
            if (values[0] instanceof Ingredient.TagValue tagValue) {
                return ItemIngredientPredicate.of(tagValue.tag()).withCount(count).build();
            }
            if (values[0] instanceof Ingredient.ItemValue itemValue) {
                return ItemIngredientPredicate.of(itemValue.item().getItem()).withCount(count).build();
            }
        }
        return ItemIngredientPredicate.of(
                        Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).toArray(Item[]::new))
                .withCount(count).build();
    }
}
