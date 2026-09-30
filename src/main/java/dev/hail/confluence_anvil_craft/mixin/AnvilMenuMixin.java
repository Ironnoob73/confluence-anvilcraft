package dev.hail.confluence_anvil_craft.mixin;

import dev.hail.confluence_anvil_craft.block.AnvilBoulderBlock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
abstract class AnvilMenuMixin extends ItemCombinerMenu {
    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @Inject(method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V",
                    shift = At.Shift.AFTER))
    private void injectedOnTake(CallbackInfo ci){
        this.access.execute((level, pos) -> {
            BlockState blockstate = level.getBlockState(pos);
            if (blockstate.getBlock() instanceof AnvilBoulderBlock)
                level.removeBlock(pos, false);
        });
    }
}
