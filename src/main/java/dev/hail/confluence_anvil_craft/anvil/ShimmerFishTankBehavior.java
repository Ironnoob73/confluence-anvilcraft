package dev.hail.confluence_anvil_craft.anvil;

import dev.dubhe.anvilcraft.api.anvil.IAnvilBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import dev.dubhe.anvilcraft.block.entity.FishTankBlockEntity;
import dev.hail.confluence_anvil_craft.compat.ConfluenceShimmer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * 让「铁砧砸鱼缸」按整组处理微光反应。
 *
 * <p>By Deepseek: 铁砧工艺的加工配方一次只消耗配方声明的数量
 * （{@code HasItemIngredient#snapshot} 调用 {@code ICacheInput.shrink(count)}），
 * 即「一次铁砧落地 = 一次加工」；而汇流来世的微光是整组处理
 * （{@code times = 数量 / shrink}，产出乘以 times）。</p>
 *
 * <p>因此这里在加工配方之后再补一次整组结算：行为在 {@code handleNeoAnvilRecipe} 之后执行，
 * 此时配方已经消耗掉一组里的 {@code shrink} 个，本行为把剩下的整组按同样规则结算完，
 * 于是「一次铁砧落地 = 整组处理」的结果与汇流来世本体一致。
 * 若某天铁砧工艺改为整组消耗，这里算出的 times 自然为 0，不会重复处理。</p>
 *
 * <p>微光分解没有配方数据（本体是运行时逆向扫描合成表），无法用铁砧加工配方表达，
 * 所以整条路径都在这里实现，并且只在鱼缸上生效。</p>
 */
public class ShimmerFishTankBehavior implements IAnvilBehavior {
    public static final ShimmerFishTankBehavior INSTANCE = new ShimmerFishTankBehavior();

    @Override
    public boolean handle(Level level, BlockPos pos, BlockState state, float fallDistance, AnvilEvent.OnLand event) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        if (!(serverLevel.getBlockEntity(pos) instanceof FishTankBlockEntity tank)) return false;
        if (!ConfluenceShimmer.isShimmer(tank.getFluidHandler().getFluid())) return false;

        IItemHandler input = tank.getInputHandler();
        for (int slot = 0; slot < input.getSlots(); slot++) {
            ItemStack stack = input.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            // null = 黑名单 / 未开启微光分解 / 没有匹配的配方，保持原样
            ConfluenceShimmer.Result result = ConfluenceShimmer.resolve(serverLevel, stack);
            if (result == null || result.consume() <= 0 || stack.getCount() < result.consume()) continue;

            input.extractItem(slot, result.consume(), false);
            tank.beginRecipeProcessing();
            try {
                for (ItemStack output : result.outputs()) {
                    ItemStack leftover = tank.insertRecipeOutput(output);
                    if (!leftover.isEmpty()) {
                        Block.popResource(serverLevel, pos.above(), leftover);
                    }
                }
            } finally {
                tank.finishRecipeProcessing();
            }
            tank.setChanged();
            // 一次铁砧落地处理一组
            return true;
        }
        return false;
    }
}
