package dev.hail.confluence_anvil_craft.network;

import dev.anvilcraft.lib.v2.codec.StreamCodecUtil;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft;
import dev.hail.confluence_anvil_craft.item.HardmodeAnvilHammerItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record SwitchHardmodeAnvilHammerModePacket(InteractionHand hand, int mode) implements IServerboundPacket {
    private static final int CYCLE_MODE = -1;
    public static final Type<SwitchHardmodeAnvilHammerModePacket> TYPE = IPacket.type(ConfluenceAnvilCraft.of("switch_hardmode_anvil_hammer_mode"));
    public static final StreamCodec<ByteBuf, SwitchHardmodeAnvilHammerModePacket> STREAM_CODEC = StreamCodec.composite(
            StreamCodecUtil.enumStreamCodec(InteractionHand.class),
            SwitchHardmodeAnvilHammerModePacket::hand,
            ByteBufCodecs.VAR_INT,
            SwitchHardmodeAnvilHammerModePacket::mode,
            SwitchHardmodeAnvilHammerModePacket::new
    );

    public SwitchHardmodeAnvilHammerModePacket(InteractionHand hand) {
        this(hand, CYCLE_MODE);
    }

    @Override
    public Type<SwitchHardmodeAnvilHammerModePacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(Player player) {
        int targetMode = this.mode;
        if (targetMode == CYCLE_MODE) {
            int currentMode = HardmodeAnvilHammerItem.getMode(player.getItemInHand(this.hand));
            targetMode = currentMode <= HardmodeAnvilHammerItem.ANVIL_HAMMER_MODE ? HardmodeAnvilHammerItem.PICKAXE_MODE : currentMode - 1;
        }
        HardmodeAnvilHammerItem.setMode(player, this.hand, targetMode);
    }
}
