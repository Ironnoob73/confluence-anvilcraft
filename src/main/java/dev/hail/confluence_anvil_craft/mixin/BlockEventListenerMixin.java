package dev.hail.confluence_anvil_craft.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.dubhe.anvilcraft.event.BlockEventListener;
import dev.hail.confluence_anvil_craft.item.HardmodeAnvilHammerItem;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEventListener.class)
public class BlockEventListenerMixin {
    @Inject(method = "anvilHammerAttack(Lnet/neoforged/neoforge/event/entity/player/PlayerInteractEvent$LeftClickBlock;)V",
            at = @At("HEAD"), cancellable = true)
    private static void injectedAnvilHammerAttack(CallbackInfo ci, @Local(name = "event") PlayerInteractEvent.LeftClickBlock event) {
        InteractionHand hand = event.getHand();
        if (event.getEntity().getItemInHand(hand).getItem() instanceof HardmodeAnvilHammerItem
                && !HardmodeAnvilHammerItem.isAnvilHammerMode(event.getEntity().getItemInHand(hand))) {
            ci.cancel();
        }
    }
}
