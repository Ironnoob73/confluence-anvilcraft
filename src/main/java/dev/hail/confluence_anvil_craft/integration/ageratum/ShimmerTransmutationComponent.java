package dev.hail.confluence_anvil_craft.integration.ageratum;

import dev.anvilcraft.lib.v2.util.predicate.ChanceItemStack;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.dubhe.anvilcraft.client.markdown.recipe.anvil.MDBaseAnvilRecipeComponent;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.hail.confluence_anvil_craft.recipe.ShimmerTransmutationRecipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShimmerTransmutationComponent extends MDBaseAnvilRecipeComponent {
    private final List<ItemIngredientPredicate> ingredients;
    private final List<ChanceItemStack> resultItems;
    private final List<BlockState> inputBlockStates;

    public ShimmerTransmutationComponent(ShimmerTransmutationRecipe recipe, boolean enableAlignCenter) {
        super(enableAlignCenter);
        ingredients = recipe.getInputItems();
        resultItems = recipe.getResultItems();
        inputBlockStates = List.of(
                ModBlocks.FISH_TANK.getDefaultState(),
                ModBlocks.LARGE_CAULDRON.getDefaultState()
        );
    }

    @Override
    public @NotNull List<ItemIngredientPredicate> getIngredients() {
        return ingredients;
    }

    @Override
    public @NotNull List<ChanceItemStack> getResultItems() {
        return resultItems;
    }

    @Override
    public @NotNull List<BlockState> getInputBlockStates() {
        return inputBlockStates;
    }
}