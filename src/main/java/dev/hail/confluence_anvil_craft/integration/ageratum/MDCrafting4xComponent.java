package dev.hail.confluence_anvil_craft.integration.ageratum;

import com.mojang.datafixers.util.Either;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.MDRenderContext;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.component.extend.MDRecipeComponent;
import dev.dubhe.anvilcraft.util.AgeratumUtil;
import dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.lib.common.recipe.EitherAmountRecipe4x;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MDCrafting4xComponent extends MDRecipeComponent {

    public static final ResourceLocation TEXTURE = ConfluenceAnvilCraft.of("textures/gui/ageratum/crafting_4x.png");
    private final EitherAmountRecipe4x<?> recipe;
    /**
     * By Deepseek:
     * 按槽位序号（0..15）排列的材料，未使用的槽位为 {@link Ingredient#EMPTY}。
     *
     * <p>{@link EitherAmountRecipe4x#getIngredients()} 对有序配方返回的是按行优先压缩后的材料列表，
     * 位置信息在压缩时丢失。这里按 {@link ShapedRecipePattern} 的宽高还原出原始槽位，
     * 否则材料会被挤到左上角、与实际配方摆放不一致。</p>
     */
    private final Ingredient[] slots;

    public MDCrafting4xComponent(EitherAmountRecipe4x<?> recipe, boolean enableAlignCenter) {
        super(TEXTURE, 160, 91, enableAlignCenter);
        this.recipe = recipe;
        this.slots = buildSlots(recipe);
    }

    private static Ingredient[] buildSlots(EitherAmountRecipe4x<?> recipe) {
        Ingredient[] result = new Ingredient[16];
        Arrays.fill(result, Ingredient.EMPTY);
        List<Ingredient> ingredients = recipe.getIngredients();
        Object left = recipe.either instanceof Either<?, ?> either ? either.left().orElse(null) : null;
        ShapedRecipePattern pattern = left instanceof ShapedRecipePattern shaped ? shaped : null;
        if (pattern == null) {
            // 无序配方：材料没有固定位置，按顺序铺入槽位即可
            for (int i = 0; i < Math.min(result.length, ingredients.size()); i++) {
                result[i] = ingredients.get(i);
            }
            return result;
        }
        int width = pattern.width();
        int height = pattern.height();
        int index = 0;
        for (int x = 0; x < height && x < 4; x++) {
            for (int y = 0; y < width && y < 4; y++) {
                if (index >= ingredients.size()) {
                    return result;
                }
                result[x + y * 4] = ingredients.get(index++);
            }
        }
        return result;
    }

    @Override
    protected void renderRecipe(@NotNull MDRenderContext context, float mouseX, float mouseY) {
        // 材料来自配方反序列化，是普通 Ingredient，不能强转成 ItemIngredientPredicate，
        // 否则渲染期会抛 ClassCastException 并让整个手册界面渲染失败。
        for (int i = 0; i < this.slots.length; i++) {
            Ingredient ingredient = this.slots[i];
            if (ingredient.isEmpty()) continue;
            int x = 9 + (i / 4) * 19;
            int y = 9 + (i % 4) * 19;
            AgeratumUtil.renderItemWithoutSlot(context, ingredient, mouseX, mouseY, x, y);
        }
        AgeratumUtil.renderItemWithoutSlot(context, recipe.result.copy(), mouseX, mouseY, 135, 37);

        Ingredient workStation;
        switch (this.recipe){
            case HeavyWorkBenchRecipe ignored -> workStation = Ingredient.of(FunctionalBlocks.HEAVY_WORK_BENCH.toStack());
            case HardmodeAnvilRecipe ignored -> workStation = Ingredient.of(FunctionalBlocks.MYTHRIL_ANVIL.toStack(), FunctionalBlocks.ORICHALCUM_ANVIL.toStack());
            case LoomRecipe ignored -> workStation = Ingredient.of(FunctionalBlocks.LOOM.toStack());
            case SawmillRecipe ignored -> workStation = Ingredient.of(FunctionalBlocks.SAWMILL.toStack());
            case SolidifierRecipe ignored -> workStation = Ingredient.of(FunctionalBlocks.SOLIDIFIER.toStack());
            default -> workStation = Ingredient.of(Items.BARRIER.getDefaultInstance());
        }
        AgeratumUtil.renderItemWithoutSlot(context, workStation, mouseX, mouseY, 97, 37);
    }
}
