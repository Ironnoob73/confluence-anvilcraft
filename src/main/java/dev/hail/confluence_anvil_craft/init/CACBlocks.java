package dev.hail.confluence_anvil_craft.init;

import dev.anvilcraft.lib.v2.registrum.util.entry.BlockEntry;
import dev.hail.confluence_anvil_craft.block.AnvilBoulderBlock;

import static dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft.REGISTRUM;

public class CACBlocks {
    public static final BlockEntry<AnvilBoulderBlock> ANVIL_BOULDER = REGISTRUM.block("anvil_boulder", properties -> new AnvilBoulderBlock())
            .item().build().register();

    public static void register() {
    }
}
