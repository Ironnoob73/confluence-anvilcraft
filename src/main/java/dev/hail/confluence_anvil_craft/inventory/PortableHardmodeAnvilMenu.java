package dev.hail.confluence_anvil_craft.inventory;

import dev.dubhe.anvilcraft.inventory.HammerOpenedAnvilMenu;
import dev.dubhe.anvilcraft.inventory.HammerOpenedAnvilMenuHelper;
import dev.dubhe.anvilcraft.inventory.OpenedHammerSource;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import org.confluence.mod.common.menu.HardmodeAnvilMenu;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortableHardmodeAnvilMenu extends HardmodeAnvilMenu implements HammerOpenedAnvilMenu {
    private final DataSlot openedHammerSlot;
    private final @Nullable OpenedHammerSource openedHammerSource;
    private boolean closingForHammerMove;

    public PortableHardmodeAnvilMenu(int containerId, Inventory inventory, ContainerLevelAccess access, @Nullable OpenedHammerSource source) {
        super(containerId, inventory, access);
        this.openedHammerSlot = DataSlot.standalone();
        this.openedHammerSource = source;
        this.openedHammerSlot.set(source == null ? -1 : source.clientInventorySlot());
    }

    @Override
    public int anvilcraft$getOpenedHammerSlot() {
        return this.openedHammerSlot.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.access.evaluate((level, pos) -> true, true);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        this.closeIfOpenedHammerMoved();
    }

    private void closeIfOpenedHammerMoved() {
        if (this.openedHammerSource != null && !this.closingForHammerMove) {
            if (!this.openedHammerSource.stillInPlace()) {
                this.closingForHammerMove = true;
                HammerOpenedAnvilMenuHelper.closeOnServer(this.player);
            }
        }
    }
}
