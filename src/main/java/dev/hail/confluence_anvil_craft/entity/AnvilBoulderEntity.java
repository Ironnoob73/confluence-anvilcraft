package dev.hail.confluence_anvil_craft.entity;

import dev.hail.confluence_anvil_craft.block.AnvilBoulderBlock;
import dev.hail.confluence_anvil_craft.init.CACEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;
import org.jetbrains.annotations.NotNull;

public class AnvilBoulderEntity extends BoulderEntity {

    public AnvilBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public AnvilBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(CACEntities.ANVIL_BOULDER.get(), level, pos, blockState);
    }

    @Override
    protected void removeEffect(@NotNull ServerLevel serverLevel) {
        this.dropAnvil(serverLevel);
        super.removeEffect(serverLevel);
    }

    private void dropAnvil(ServerLevel serverLevel) {
        Block originalAnvil = Blocks.ANVIL;
        if (getBlockState().getBlock() instanceof AnvilBoulderBlock anvilBoulderBlock)
            originalAnvil = anvilBoulderBlock.originalAnvil;

        ItemStack anvil = originalAnvil.asItem().getDefaultInstance();
        BlockPos blockPos = this.blockPosition();
        BlockState blockState = serverLevel.getBlockState(blockPos);
        if (blockState.canBeReplaced()) {
            serverLevel.setBlock(blockPos, originalAnvil.defaultBlockState(), 3);
        } else {
            Block.popResource(serverLevel, blockPos, anvil);
        }
    }
}
