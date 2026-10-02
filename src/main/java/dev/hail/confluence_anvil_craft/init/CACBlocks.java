package dev.hail.confluence_anvil_craft.init;

import dev.anvilcraft.lib.v2.registrum.util.entry.BlockEntry;
import dev.hail.confluence_anvil_craft.block.AnvilBoulderBlock;
import dev.hail.confluence_anvil_craft.entity.AnvilBoulderEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;

import static dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft.REGISTRUM;

public class CACBlocks {
    public static final BlockEntry<AnvilBoulderBlock> ANVIL_BOULDER = REGISTRUM.block("anvil_boulder",
                    properties -> new AnvilBoulderBlock(AnvilBoulderEntity::new, Blocks.ANVIL))
            .item().build().register();
    public static final BlockEntry<AnvilBoulderBlock> LEAD_ANVIL_BOULDER = REGISTRUM.block("lead_anvil_boulder",
                    properties -> new AnvilBoulderBlock(AnvilBoulderEntity::new, FunctionalBlocks.LEAD_ANVIL.get()))
            .item().build().register();

    public static final BlockEntry<Block> SANCTIFICATION_COBALT_ORE = registerSancificationOre("sanctification_cobalt_ore");
    public static final BlockEntry<Block> SANCTIFICATION_PALLADIUM_ORE = registerSancificationOre("sanctification_palladium_ore");
    public static final BlockEntry<Block> SANCTIFICATION_MYTHRIL_ORE = registerSancificationOre("sanctification_mythril_ore");
    public static final BlockEntry<Block> SANCTIFICATION_ORICHALCUM_ORE = registerSancificationOre("sanctification_orichalcum_ore");
    public static final BlockEntry<Block> SANCTIFICATION_TITANIUM_ORE = registerSancificationOre("sanctification_titanium_ore");
    public static final BlockEntry<Block> SANCTIFICATION_ADAMANTITE_ORE = registerSancificationOre("sanctification_adamantite_ore");

    public static void register() {
    }

    public static BlockEntry<Block> registerSancificationOre(String id) {
        return REGISTRUM.block(id,
                        properties -> new Block(BlockBehaviour.Properties
                                .ofFullCopy(NatureBlocks.PEARLSTONE.get())
                                .strength(15.0F, ModBlocks.getObsidianBasedExplosionResistance(100.0F))))
                .item().build().register();
    }
}
