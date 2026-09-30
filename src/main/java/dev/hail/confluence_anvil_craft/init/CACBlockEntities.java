package dev.hail.confluence_anvil_craft.init;

import com.mojang.datafixers.DSL;
import dev.anvilcraft.lib.v2.registrum.util.entry.BlockEntry;
import dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.common.block.functional.AbstractMechanicalBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class CACBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ConfluenceAnvilCraft.MOD_ID);
    static List<BlockEntry<? extends Block>> MECHANICAL_BLOCKS = new ArrayList<>();

    public static final Supplier<BlockEntityType<AbstractMechanicalBlock.BEntity>> MECHANICAL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("mechanical_block_entity", () -> {
                MECHANICAL_BLOCKS.add(CACBlocks.ANVIL_BOULDER);
                MECHANICAL_BLOCKS.add(CACBlocks.LEAD_ANVIL_BOULDER);
                Block[] validBlocks = MECHANICAL_BLOCKS.stream().map(BlockEntry::get).toArray(Block[]::new);
                MECHANICAL_BLOCKS = null;
                return BlockEntityType.Builder.of(AbstractMechanicalBlock.BEntity::new, validBlocks).build(DSL.remainderType());
            });

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
