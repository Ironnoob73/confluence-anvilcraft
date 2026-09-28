package dev.hail.confluence_anvil_craft.integration.ageratum;

import dev.anvilcraft.resource.ageratum.client.feat.markdown.component.extend.MDRecipeComponent;
import dev.anvilcraft.resource.ageratum.client.registries.AgeratumRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModRecipes;

@SuppressWarnings("unused")
public class ConfluenceCraftRecipeFactories {
    public static final DeferredRegister<MDRecipeComponent.RecipeComponentFactory<?>>
            RECIPE_COMPONENT_FACTORIES = DeferredRegister.create(AgeratumRegistries.RECIPE_COMPONENT_FACTORY_REGISTRY_KEY, Confluence.MODID);

    public static final DeferredHolder<MDRecipeComponent.RecipeComponentFactory<?>, MDRecipeComponent.RecipeComponentFactory<?>> CRAFTING_4X =
            RECIPE_COMPONENT_FACTORIES.register(
                    "heavy_work_bench", () -> MDRecipeComponent.RecipeComponentFactory.create(
                            MDCrafting4xComponent::new,
                            ModRecipes.HEAVY_WORK_BENCH_TYPE.get(),
                            ModRecipes.HARDMODE_ANVIL_TYPE.get(),
                            ModRecipes.LOOM_TYPE.get(),
                            ModRecipes.SAWMILL_TYPE.get(),
                            ModRecipes.SOLIDIFIER_TYPE.get()
                    )
            );

    private ConfluenceCraftRecipeFactories() {
    }
}
