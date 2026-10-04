package dev.hail.confluence_anvil_craft.integration.ageratum;

import dev.anvilcraft.resource.ageratum.client.feat.markdown.component.extend.MDRecipeComponent;
import dev.anvilcraft.resource.ageratum.client.registries.AgeratumRegistries;
import dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft;
import dev.hail.confluence_anvil_craft.init.CACRecipes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModRecipes;

@SuppressWarnings("unused")
public class CACRecipeFactories {
    public static final DeferredRegister<MDRecipeComponent.RecipeComponentFactory<?>>
            CONFLUENCE_RECIPE_COMPONENT_FACTORIES = DeferredRegister.create(AgeratumRegistries.RECIPE_COMPONENT_FACTORY_REGISTRY_KEY, Confluence.MODID);
    public static final DeferredRegister<MDRecipeComponent.RecipeComponentFactory<?>>
            RECIPE_COMPONENT_FACTORIES = DeferredRegister.create(AgeratumRegistries.RECIPE_COMPONENT_FACTORY_REGISTRY_KEY, ConfluenceAnvilCraft.MOD_ID);

    public static final DeferredHolder<MDRecipeComponent.RecipeComponentFactory<?>, MDRecipeComponent.RecipeComponentFactory<?>> CRAFTING_4X =
            CONFLUENCE_RECIPE_COMPONENT_FACTORIES.register(
                    "heavy_work_bench", () -> MDRecipeComponent.RecipeComponentFactory.create(
                            MDCrafting4xComponent::new,
                            ModRecipes.HEAVY_WORK_BENCH_TYPE.get(),
                            ModRecipes.HARDMODE_ANVIL_TYPE.get(),
                            ModRecipes.LOOM_TYPE.get(),
                            ModRecipes.SAWMILL_TYPE.get(),
                            ModRecipes.SOLIDIFIER_TYPE.get()
                    )
            );

    public static final DeferredHolder<MDRecipeComponent.RecipeComponentFactory<?>, MDRecipeComponent.RecipeComponentFactory<?>> SHIMMER_TRANSMUTATION =
            RECIPE_COMPONENT_FACTORIES.register(
                    "shimmer_transmutation", () -> MDRecipeComponent.RecipeComponentFactory.create(
                            CACRecipes.SHIMMER_TRANSMUTATION.type().get(),
                            ShimmerTransmutationComponent::new
                    )
            );

    private CACRecipeFactories() {
    }
}
