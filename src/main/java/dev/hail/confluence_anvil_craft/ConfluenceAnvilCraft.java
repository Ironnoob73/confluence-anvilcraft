package dev.hail.confluence_anvil_craft;

import dev.anvilcraft.lib.v2.network.register.NetworkRegistrar;
import dev.anvilcraft.lib.v2.registrum.Registrum;
import dev.dubhe.anvilcraft.block.PiezoelectricCrystalBlock;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import java.util.List;


@Mod(ConfluenceAnvilCraft.MOD_ID)
public class ConfluenceAnvilCraft {
    public static final String MOD_ID = "confluence_anvil_craft";
    public static final String NETWORK_VERSION = "1";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Registrum REGISTRATE = Registrum.create(MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public ConfluenceAnvilCraft(IEventBus modEventBus, ModContainer modContainer) {
        CACCreativeTab.register(modEventBus);
        CACItems.register();

        modEventBus.addListener(this::registerPayload);
        modEventBus.addListener(this::onCommonSetup);
    }

    private void registerPayload(RegisterPayloadHandlersEvent event) {
        // By Deepseek: 注册 dev.hail.confluence_anvil_craft.network 包下所有网络包（与 AnvilCraft 的注册方式一致），
        // 缺少注册会导致发包时抛出 "Payload ... may not be sent to the server!" 并崩溃
        NetworkRegistrar.register(event.registrar(NETWORK_VERSION), MOD_ID);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ConfluenceAnvilCraft::registerAnvilCompat);
    }

    private static void registerAnvilCompat() {
        // By Deepseek: 参数含义与 AnvilCraft 原版铁砧一致：按下落距离取发电量（大小不足时取最后一项）
        registerAnvil(FunctionalBlocks.LEAD_ANVIL.get(), List.of(1, 3, 5, 9));
        registerAnvil(FunctionalBlocks.CHIPPED_LEAD_ANVIL.get(), List.of(1, 3, 5, 9));
        registerAnvil(FunctionalBlocks.DAMAGED_LEAD_ANVIL.get(), List.of(1, 3, 5, 9));

        registerAnvil(FunctionalBlocks.MYTHRIL_ANVIL.get(), List.of(2, 4, 8, 14));
        registerAnvil(FunctionalBlocks.ORICHALCUM_ANVIL.get(), List.of(3, 5, 9, 15));
    }

    private static void registerAnvil(Block anvil, List<Integer> chargeNums) {
        PiezoelectricCrystalBlock.ANVIL_TYPES.put(anvil, chargeNums);
        LOGGER.debug("[{}] Registered piezoelectric crystal anvil: {}", MOD_ID, anvil);
    }

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
