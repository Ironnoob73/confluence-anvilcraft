package dev.hail.confluence_anvil_craft;

import dev.anvilcraft.lib.v2.registrum.util.entry.ItemEntry;
import dev.hail.confluence_anvil_craft.item.HardmodeAnvilHammerItem;
import dev.hail.confluence_anvil_craft.item.LeadAnvilHammerItem;
import org.confluence.mod.common.init.ModTiers;

import static dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft.REGISTRATE;

@SuppressWarnings("unused")
public class CACItems {
    public static final ItemEntry<LeadAnvilHammerItem> LEAD_ANVIL_HAMMER = REGISTRATE.item("lead_anvil_hammer", LeadAnvilHammerItem::new)
            .properties(properties -> properties.durability(43))
            .register();
    public static final ItemEntry<HardmodeAnvilHammerItem> MYTHRIL_ANVIL_HAMMER = REGISTRATE.item("mythril_anvil_hammer",
                    properties -> new HardmodeAnvilHammerItem(properties, ModTiers.MYTHRIL)).register();
    public static final ItemEntry<HardmodeAnvilHammerItem> ORICHALCUM_ANVIL_HAMMER = REGISTRATE.item("orichalcum_anvil_hammer",
                    properties -> new HardmodeAnvilHammerItem(properties, ModTiers.ORICHALCUM)).register();

    public static void register() {
    }
}
