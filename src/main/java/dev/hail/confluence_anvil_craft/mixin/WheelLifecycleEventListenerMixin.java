package dev.hail.confluence_anvil_craft.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.anvilcraft.lib.v2.wheel.api.WheelMenuBuilder;
import dev.anvilcraft.lib.v2.wheel.api.WheelMenuModel;
import dev.anvilcraft.lib.v2.wheel.api.WheelSelectionEffect;
import dev.anvilcraft.lib.v2.wheel.client.input.WheelScreenController;
import dev.dubhe.anvilcraft.client.event.WheelLifecycleEventListener;
import dev.dubhe.anvilcraft.client.renderer.item.ItemSlotClipping;
import dev.hail.confluence_anvil_craft.item.HardmodeAnvilHammerItem;
import dev.hail.confluence_anvil_craft.network.SwitchHardmodeAnvilHammerModePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(WheelLifecycleEventListener.class)
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public class WheelLifecycleEventListenerMixin {
    @Unique
    private static final WheelScreenController confluenceanvilcraft$CONTROLLER = new WheelScreenController();
    @Unique
    private static long confluenceanvilcraft$hardmodeAnvilHammerKeyTime = -1L;
    @Unique
    private static boolean confluenceanvilcraft$hardmodeAnvilHammerKeyWasDown = false;
    @Unique
    @Nullable
    private static Optional<WheelMenuModel> confluenceanvilcraft$hardmodeAnvilHammerWheelCache = null;

    @Unique
    private static void confluenceanvilcraft$renderWheelItem(GuiGraphics graphics, ItemStack stack) {
        ItemSlotClipping.runWithoutClip(() -> graphics.renderItem(stack, -8, -8));
    }

    @Inject(method = "onClientTick(Lnet/neoforged/neoforge/client/event/ClientTickEvent$Post;)V", at = @At("TAIL"))
    private static void injectedClientTick(CallbackInfo ci, @Local(name = "level") ClientLevel level) {
        if (level != null) {
            long gameTime = level.getGameTime();
            confluenceanvilcraft$openHardmodeAnvilHammerWheel(gameTime);
        }
    }

    @Unique
    private static void confluenceanvilcraft$openHardmodeAnvilHammerWheel(long gameTime) {
        if (confluenceanvilcraft$hardmodeAnvilHammerKeyTime >= 0L && gameTime - confluenceanvilcraft$hardmodeAnvilHammerKeyTime > 4L) {
            if (confluenceanvilcraft$hardmodeAnvilHammerWheelCache == null) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) {
                    return;
                }

                InteractionHand hand = InteractionHand.MAIN_HAND;
                ItemStack stack = player.getMainHandItem();
                if (!(stack.getItem() instanceof HardmodeAnvilHammerItem)) {
                    hand = InteractionHand.OFF_HAND;
                    stack = player.getOffhandItem();
                }

                if (!(stack.getItem() instanceof HardmodeAnvilHammerItem)) {
                    return;
                }

                confluenceanvilcraft$hardmodeAnvilHammerWheelCache = Optional.of(confluenceanvilcraft$getHardmodeAnvilHammerWheel(hand, stack));
            }

            if (confluenceanvilcraft$hardmodeAnvilHammerWheelCache.isEmpty()) {
                return;
            }

            confluenceanvilcraft$CONTROLLER.onHoldKeyPressed(confluenceanvilcraft$hardmodeAnvilHammerWheelCache.get());
            confluenceanvilcraft$hardmodeAnvilHammerKeyWasDown = true;
        }
    }

    @Unique
    private static WheelMenuModel confluenceanvilcraft$getHardmodeAnvilHammerWheel(InteractionHand hand, ItemStack holding) {
        return WheelMenuBuilder.create().selectionEffect(WheelSelectionEffect.ANNULAR_SECTOR).slotsPerPage(3)
                .action("anvil_hammer", Component.translatable("screen.confluence_anvil_craft.hardmode_anvil_hammer.anvil_hammer_mode"),
                        (graphics, pose, width, height) -> {
            ItemStack copied = holding.copy();
            copied.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(0));
            confluenceanvilcraft$renderWheelItem(graphics, copied);
        }, (ctx) -> PacketDistributor.sendToServer(new SwitchHardmodeAnvilHammerModePacket(hand, ctx.slotIndex())))
                .action("hammer", Component.translatable("screen.confluence_anvil_craft.hardmode_anvil_hammer.mining_hammer_mode"),
                        (graphics, pose, width, height) -> {
            ItemStack copied = holding.copy();
            copied.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
            confluenceanvilcraft$renderWheelItem(graphics, copied);
        }, (ctx) -> PacketDistributor.sendToServer(new SwitchHardmodeAnvilHammerModePacket(hand, ctx.slotIndex())))
                .action("pickaxe", Component.translatable("screen.confluence_anvil_craft.hardmode_anvil_hammer.pickaxe_mode"),
                        (graphics, pose, width, height) -> {
            ItemStack copied = holding.copy();
            copied.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(2));
            confluenceanvilcraft$renderWheelItem(graphics, copied);
        }, (ctx) -> PacketDistributor.sendToServer(new SwitchHardmodeAnvilHammerModePacket(hand, ctx.slotIndex()))).build();
    }

    @Inject(method = "onKeyInput(Lnet/neoforged/neoforge/client/event/InputEvent$Key;)V",
            at = @At(value = "INVOKE",
                    target = "Ldev/dubhe/anvilcraft/client/event/WheelLifecycleEventListener;processEquipmentPress(Lnet/minecraft/client/Minecraft;I)V",
                    shift = At.Shift.AFTER)
    )
    private static void injectedKeyInput(CallbackInfo ci,
                                         @Local(name = "client") Minecraft client,
                                         @Local(argsOnly = true) InputEvent.Key event) {
        confluenceanvilcraft$processHardmodeAnvilHammerPress(client, event.getAction());
    }

    @Inject(method = "onKeyInput(Lnet/neoforged/neoforge/client/event/InputEvent$MouseButton$Post;)V",
            at = @At(value = "INVOKE",
                    target = "Ldev/dubhe/anvilcraft/client/event/WheelLifecycleEventListener;processEquipmentPress(Lnet/minecraft/client/Minecraft;I)V",
                    shift = At.Shift.AFTER)
    )
    private static void injectedPostInput(CallbackInfo ci,
                                          @Local(name = "client") Minecraft client,
                                          @Local(argsOnly = true) InputEvent.MouseButton.Post event) {
        confluenceanvilcraft$processHardmodeAnvilHammerPress(client, event.getAction());
    }


    @Unique
    private static void confluenceanvilcraft$processHardmodeAnvilHammerPress(Minecraft client, int action) {
        if (client.level != null) {
            if (action != 0) {
                if (Minecraft.getInstance().screen == null) {
                    if (action == 1 && !confluenceanvilcraft$hardmodeAnvilHammerKeyWasDown) {
                        confluenceanvilcraft$hardmodeAnvilHammerKeyTime = client.level.getGameTime();
                    }

                }
            } else {
                if (confluenceanvilcraft$hardmodeAnvilHammerKeyWasDown) {
                    confluenceanvilcraft$CONTROLLER.onHoldKeyReleased();
                } else if (confluenceanvilcraft$hardmodeAnvilHammerKeyTime >= 0L
                        && client.level.getGameTime() - confluenceanvilcraft$hardmodeAnvilHammerKeyTime <= 4L && client.screen == null
                        && client.player != null) {
                    for(InteractionHand hand : InteractionHand.values()) {
                        if (client.player.getItemInHand(hand).getItem() instanceof HardmodeAnvilHammerItem) {
                            PacketDistributor.sendToServer(new SwitchHardmodeAnvilHammerModePacket(hand));
                            break;
                        }
                    }
                }

                confluenceanvilcraft$hardmodeAnvilHammerKeyWasDown = false;
                confluenceanvilcraft$hardmodeAnvilHammerKeyTime = -1L;
                confluenceanvilcraft$hardmodeAnvilHammerWheelCache = null;
            }
        }
    }
}
