package dev.hail.confluence_anvil_craft.init;

import dev.anvilcraft.lib.v2.registrum.util.entry.BlockEntry;
import dev.hail.confluence_anvil_craft.block.AnvilBoulderBlock;
import dev.hail.confluence_anvil_craft.entity.AnvilBoulderEntity;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;

import static dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft.REGISTRUM;

public class CACBlocks {
    public static final BlockEntry<AnvilBoulderBlock> ANVIL_BOULDER = REGISTRUM.block("anvil_boulder",
                    properties -> new AnvilBoulderBlock(AnvilBoulderEntity::new, Blocks.ANVIL))
            .item().build().register();
    public static final BlockEntry<AnvilBoulderBlock> LEAD_ANVIL_BOULDER = REGISTRUM.block("lead_anvil_boulder",
                    properties -> new AnvilBoulderBlock(AnvilBoulderEntity::new, FunctionalBlocks.LEAD_ANVIL.get()))
            .item().build().register();

    public static void register() {
    }
}
