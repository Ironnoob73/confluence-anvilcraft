package dev.hail.confluence_anvil_craft.mixin;

import dev.dubhe.anvilcraft.block.FishTankBlock;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;

@Mixin(FishTankBlock.class)
public class FishTankBlockMixin {
    @Redirect(method = "useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/ItemInteractionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/cauldron/CauldronInteraction$InteractionMap;map()Ljava/util/Map;"))
    private Map<Item, CauldronInteraction> redirectInteraction(CauldronInteraction.InteractionMap interactionMap, ItemStack stack) {
        return new HashMap<>();
    }
}
