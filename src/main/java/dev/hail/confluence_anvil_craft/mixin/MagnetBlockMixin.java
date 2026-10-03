package dev.hail.confluence_anvil_craft.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.dubhe.anvilcraft.block.MagnetBlock;
import dev.hail.confluence_anvil_craft.block.AnvilBoulderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagnetBlock.class)
public class MagnetBlockMixin {
    @Inject(method = "attract(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V",
        at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                shift = At.Shift.AFTER), cancellable = true)
    private static void injectedAttract(BlockState state, Level level, BlockPos magnetPos, CallbackInfo ci, @Local(name = "currentPos") BlockPos currentPos){
        if (level.getBlockState(currentPos).getBlock() instanceof AnvilBoulderBlock) {
            level.setBlockAndUpdate(currentPos, Blocks.AIR.defaultBlockState());
            ci.cancel();
        }
    }
}
