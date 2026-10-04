package dev.hail.confluence_anvil_craft.mixin;

import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.block.MagnetBlock;
import dev.dubhe.anvilcraft.entity.AnimateAscendingBlockEntity;
import dev.dubhe.anvilcraft.init.block.ModBlockTags;
import dev.dubhe.anvilcraft.util.TriggerUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.crafting.HardmodeAnvilBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.ParametersAreNonnullByDefault;

import static dev.dubhe.anvilcraft.block.MagnetBlock.LIT;

@Mixin(HardmodeAnvilBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
abstract class HardmodeAnvilBlockMixin extends FallingBlock {
    public HardmodeAnvilBlockMixin(Properties properties) {
        super(properties);
    }

    // 照抄的砧艺原本对AnvilBlock的Mixin方法
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.is(ModBlockTags.NON_MAGNETIC)) {
            super.tick(state, level, pos, random);
            return;
        }
        if (confluenceanvilcraft$isAttracts(level.getBlockState(pos.above()))) {
            return;
        }
        super.tick(state, level, pos, random);
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block neighborBlock,
            BlockPos neighborPos,
            boolean movedByPiston
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (state.is(ModBlockTags.NON_MAGNETIC)) return;
        this.confluenceanvilcraft$wasAttracted(state, level, pos);
    }

    @Unique
    private boolean confluenceanvilcraft$isAttracts(BlockState state) {
        return state.is(ModBlockTags.MAGNET) && !state.getValue(LIT);
    }

    @Override
    public void onPlace(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean movedByPiston
    ) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (state.is(ModBlockTags.NON_MAGNETIC)) return;
        BlockState state1 = level.getBlockState(pos.above());
        if (!this.confluenceanvilcraft$isAttracts(state1)) this.confluenceanvilcraft$wasAttracted(state, level, pos);
    }

    @Unique
    private void confluenceanvilcraft$wasAttracted(BlockState state, Level level, BlockPos anvil) {
        BlockPos magnet = anvil;
        BlockState aboveState = level.getBlockState(anvil.above());
        if (aboveState.is(ModBlockTags.MAGNET) || aboveState.getBlock() instanceof MagnetBlock) return;
        int distance = AnvilCraft.CONFIG.magnetAttractsDistance;
        for (int i = 0; i < distance; i++) {
            magnet = magnet.above();
            BlockState state1 = level.getBlockState(magnet);
            if (!(state1.getBlock() instanceof MagnetBlock) || state1.getValue(LIT)) {
                if (state1.isAir() || state1.getBlock() instanceof LiquidBlock) {
                    continue;
                } else {
                    return;
                }
            }
            level.destroyBlock(magnet.below(), true);
            level.setBlockAndUpdate(magnet.below(), state);
            level.setBlockAndUpdate(anvil, Blocks.AIR.defaultBlockState());
            AnimateAscendingBlockEntity.animate(level, anvil, state, magnet.below());
            TriggerUtil.liftingAnvil(level, magnet.below());
            return;
        }
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
