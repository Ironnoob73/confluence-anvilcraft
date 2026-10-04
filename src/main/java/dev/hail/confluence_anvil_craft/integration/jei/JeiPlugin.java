package dev.hail.confluence_anvil_craft.integration.jei;

import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft;
import dev.hail.confluence_anvil_craft.recipe.ShimmerTransmutationRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * 把 {@code confluence_anvil_craft:shimmer_transmutation} 显示在 JEI 里。
 */
@mezz.jei.api.JeiPlugin
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JeiPlugin implements IModPlugin {
    public static final RecipeType<RecipeHolder<ShimmerTransmutationRecipe>> SHIMMER_TRANSMUTATION =
            RecipeType.createRecipeHolderType(ConfluenceAnvilCraft.of("shimmer_transmutation"));

    @Override
    public ResourceLocation getPluginUid() {
        return ConfluenceAnvilCraft.of("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ShimmerTransmutationCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ShimmerTransmutationCategory.registerRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FISH_TANK.asItem()), SHIMMER_TRANSMUTATION);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.LARGE_CAULDRON.asItem()), SHIMMER_TRANSMUTATION);
        BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath("confluence", "bottomless_shimmer_bucket"))
                .ifPresent(item -> registration.addRecipeCatalyst(new ItemStack(item), SHIMMER_TRANSMUTATION));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        ConfluenceAnvilCraft.LOGGER.info("[{}] Shimmer transmutation JEI category exposed with {} recipes",
                ConfluenceAnvilCraft.MOD_ID,
                jeiRuntime.getRecipeManager().createRecipeLookup(SHIMMER_TRANSMUTATION).get().count());
    }
}
