package dev.hail.confluence_anvil_craft.client;

import dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft;
import dev.hail.confluence_anvil_craft.integration.ageratum.ConfluenceCraftRecipeFactories;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = ConfluenceAnvilCraft.MOD_ID, dist = Dist.CLIENT)
public class CACClient {

    public CACClient(IEventBus modEventBus, ModContainer container) {
        ConfluenceCraftRecipeFactories.RECIPE_COMPONENT_FACTORIES.register(modEventBus);
    }
}
