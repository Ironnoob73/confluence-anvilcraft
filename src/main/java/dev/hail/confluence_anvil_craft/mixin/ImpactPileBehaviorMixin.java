package dev.hail.confluence_anvil_craft.mixin;

import dev.dubhe.anvilcraft.anvil.ImpactPileBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ImpactPileBehavior.class)
public class ImpactPileBehaviorMixin {
    //@Inject(method = "handle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;FLdev/dubhe/anvilcraft/api/event/AnvilEvent$OnLand;)Z",
    //        at = @At("HEAD"), cancellable = true)
    //private static void injectedHandle(CallbackInfo ci){
//
    //}

}
