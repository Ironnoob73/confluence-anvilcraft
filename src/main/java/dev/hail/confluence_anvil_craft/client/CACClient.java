package dev.hail.confluence_anvil_craft.client;

import dev.hail.confluence_anvil_craft.ConfluenceAnvilCraft;
import dev.hail.confluence_anvil_craft.init.CACEntities;
import dev.hail.confluence_anvil_craft.integration.ageratum.ConfluenceCraftRecipeFactories;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.confluence.mod.client.renderer.entity.projectile.BoulderRenderer;

@Mod(value = ConfluenceAnvilCraft.MOD_ID, dist = Dist.CLIENT)
public class CACClient {

    public CACClient(IEventBus modEventBus, ModContainer container) {
        ConfluenceCraftRecipeFactories.RECIPE_COMPONENT_FACTORIES.register(modEventBus);
        // By Deepseek: 类上的静态 @SubscribeEvent 方法不会被 FML 自动注册（FML 只扫描 @EventBusSubscriber 注解的类），
        // 必须显式注册到模组事件总线。否则铁砧巨石实体没有渲染器，客户端渲染该实体时 entityrenderer 为 null 而崩溃。
        modEventBus.addListener(CACClient::registerEntityRenderers);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CACEntities.ANVIL_BOULDER.get(), BoulderRenderer::new);
    }
}
