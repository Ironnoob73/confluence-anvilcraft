package dev.hail.confluence_anvil_craft.init;

import dev.dubhe.anvilcraft.init.item.ModItemGroups;
import dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft.REGISTRUM;

public class CACCreativeTab {
    private static final DeferredRegister<CreativeModeTab> DEFERRED_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ConfluenceAnvilCraft.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = DEFERRED_REGISTER.register(
            "confluence_anvil_craft_tab",
            () -> CreativeModeTab.builder()
                    .icon(CACItems.MYTHRIL_ANVIL_HAMMER::asStack)
                    .title(REGISTRUM.addLang("item_group", ConfluenceAnvilCraft.of("items"), "Confluence: Anvil Craft"))
                    .displayItems(CACCreativeTab::addItems)
                    .withTabsBefore(ModItemGroups.ANVILCRAFT_BUILDING_BLOCKS.getId(), ModItemGroups.ANVILCRAFT_ITEMS.getId())
                    .build()
    );

    public static void register(IEventBus modEventBus) {
        DEFERRED_REGISTER.register(modEventBus);
    }

    public static void addItems(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(CACItems.LEAD_ANVIL_HAMMER);
        output.accept(CACItems.MYTHRIL_ANVIL_HAMMER);
        output.accept(CACItems.ORICHALCUM_ANVIL_HAMMER);
        output.accept(CACBlocks.ANVIL_BOULDER.asItem());
    }
}

