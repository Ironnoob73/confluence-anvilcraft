package dev.hail.confluence_anvil_craft.init;

import dev.anvilcraft.lib.v2.registrum.util.entry.EntityEntry;
import dev.hail.confluence_anvil_craft.entity.AnvilBoulderEntity;
import net.minecraft.world.entity.MobCategory;

import static dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft.REGISTRUM;

public class CACEntities {
    public static final EntityEntry<? extends AnvilBoulderEntity> ANVIL_BOULDER =
            REGISTRUM.<AnvilBoulderEntity>entity("anvil_boulder", AnvilBoulderEntity::new, MobCategory.MISC).register();

    public static void register() {
    }
}
