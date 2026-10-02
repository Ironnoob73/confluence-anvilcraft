package dev.hail.confluence_anvil_craft.mixin;

import dev.dubhe.anvilcraft.anvil.ImpactPileBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import dev.dubhe.anvilcraft.block.ImpactPileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.crafting.HardmodeAnvilBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ImpactPileBehavior.class)
public class ImpactPileBehaviorMixin {
    @Inject(method = "handle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;FLdev/dubhe/anvilcraft/api/event/AnvilEvent$OnLand;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void injectedHandle(Level level, BlockPos hitBlockPos, BlockState hitBlockState, float fallDistance, AnvilEvent.OnLand event, CallbackInfoReturnable<Boolean> cir){
        BlockState belowState = level.getBlockState(hitBlockPos.below());
        if (event.getEntity().getBlockState().getBlock() instanceof HardmodeAnvilBlock && level.getMinBuildHeight() <= hitBlockPos.getY() && hitBlockPos.getY() <= level.getMinBuildHeight() + 8 && (belowState.is(Blocks.DEEPSLATE) || belowState.is(Blocks.BEDROCK)))
            ImpactPileBlock.impact(level, hitBlockPos);
        cir.setReturnValue(true);
    }
}
