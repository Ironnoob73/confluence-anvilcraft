package dev.hail.confluence_anvil_craft.mixin;

import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import dev.dubhe.anvilcraft.api.giantanvil.IShockFixedBlock;
import dev.dubhe.anvilcraft.event.giantanvil.shock.GiantAnvilShockEventListener;
import dev.dubhe.anvilcraft.event.giantanvil.shock.ShockContext;
import dev.dubhe.anvilcraft.init.block.ModBlockTags;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.hail.confluence_anvil_craft.block.AnvilBoulderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.crafting.HardmodeAnvilBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 巨型铁砧撼地“震起方块”的可震起方块表是写死在
 * {@link GiantAnvilShockEventListener} 静态初始化里的 lambda（{@code lambda$static$25}）中的
 * {@code instanceof AnvilBlock}，<b>既不能注入 {@code <clinit>} 去改，也不是数据包标签能扩展的</b>。
 * <p>
 * 所以这里改在 {@link GiantAnvilShockEventListener#onLand} 的 HEAD 上做一轮补充处理，
 * 触发条件与撼地原本的震起完全一致（落在重铁块上、且四周满足树脂冲击条件、且没有实现
 * {@link IShockFixedBlock} 拒绝被震起），只是把生效方块扩展到：
 * <ul>
 *     <li>{@link AnvilBoulderBlock}：直接被震碎，破坏时由 {@code BoulderBlock#onRemove} 生成滚走的巨石；</li>
 *     <li>{@link HardmodeAnvilBlock}：汇流把它写成了下落方块（{@code FallingBlock}）而不是铁砧，
 *     这里按原版铁砧的方式震起。</li>
 * </ul>
 * 放在 HEAD 还有个额外好处：它先于撼地自身的破坏流程执行，困难模式砧不会被同一轮震波顺手拆掉。
 */
@Mixin(GiantAnvilShockEventListener.class)
public class GiantAnvilShockEventListenerMixin {
    @Inject(method = "onLand(Ldev/dubhe/anvilcraft/api/event/AnvilEvent$GiantOnLand;)V", at = @At("HEAD"))
    private static void cac$shockExtraBlocks(AnvilEvent.GiantOnLand event, CallbackInfo ci) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel)) return;

        ShockContext context = ShockContext.inflate(event);
        if (!level.getBlockState(context.centerPos()).is(ModBlocks.HEAVY_IRON_BLOCK.get())) return;
        if (!context.testCorner(ModBlockTags.RESIN_SHOCK_COMPATIBLE)) return;
        if (!context.testBorder(ModBlockTags.RESIN_SHOCK_COMPATIBLE)) return;

        for (BlockPos pos : context.rangePosList()) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof IShockFixedBlock fixed && fixed.anvilcraft$isFixedDuringShockBounce(state)) continue;

            if (block instanceof AnvilBoulderBlock) {
                // 破坏铁砧巨石，BoulderBlock#onRemove 会把它变成滚走的巨石
                level.destroyBlock(pos, false);
            } else if (block instanceof HardmodeAnvilBlock) {
                confluenceanvilcraft$bounce(level, pos, state);
            }
        }
    }

    /**
     * 与 {@code GiantAnvilShockEventListener} 震起原版铁砧时完全一致的处理。
     */
    @Unique
    private static void confluenceanvilcraft$bounce(Level level, BlockPos pos, BlockState state) {
        FallingBlockEntity entity = FallingBlockEntity.fall(level, pos, state);
        entity.setDeltaMovement(0.0, ShockContext.bounceVelocityForHeight(1.0), 0.0);
    }
}
