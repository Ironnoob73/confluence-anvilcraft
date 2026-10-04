package dev.hail.confluence_anvil_craft.mixin;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.crafting.HardmodeAnvilBlock;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(HardmodeAnvilBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
abstract class HardmodeAnvilBlockMixin extends FallingBlock {
    public HardmodeAnvilBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected void falling(FallingBlockEntity fallingEntity) {
        fallingEntity.setHurtsEntities(2.5F, 120);
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
        if (!fallingBlock.isSilent()) {
            level.levelEvent(1031, pos, 0);
        }
    }

    @Override
    public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity fallingBlock) {
        if (!fallingBlock.isSilent()) {
            level.levelEvent(1029, pos, 0);
        }
    }

    @Override
    public DamageSource getFallDamageSource(Entity entity) {
        return entity.damageSources().anvil(entity);
    }
}
