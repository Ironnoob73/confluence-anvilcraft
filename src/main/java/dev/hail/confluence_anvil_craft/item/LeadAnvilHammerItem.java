package dev.hail.confluence_anvil_craft.item;

import dev.dubhe.anvilcraft.item.AnvilHammerItem;
import net.minecraft.world.level.block.Block;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.NotNull;

public class LeadAnvilHammerItem extends AnvilHammerItem {
    public LeadAnvilHammerItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Block getAnvil() {
        return FunctionalBlocks.LEAD_ANVIL.get();
    }
}
