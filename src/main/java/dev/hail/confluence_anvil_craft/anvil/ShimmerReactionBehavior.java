package dev.hail.confluence_anvil_craft.anvil;

import dev.dubhe.anvilcraft.api.anvil.IAnvilBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import dev.dubhe.anvilcraft.block.entity.FishTankBlockEntity;
import dev.dubhe.anvilcraft.block.entity.LargeCauldronBlockEntity;
import dev.hail.confluence_anvil_craft.compat.ConfluenceShimmer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.UnaryOperator;

/**
 * 让「铁砧砸在盛有微光的容器上」按整组结算微光反应，支持鱼缸与大型炼药锅。
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
 * 所以整条路径都在这里实现。大型炼药锅自带配方循环（{@code serverTick} 里会调用
 * {@code InWorldRecipeContext#accept}），因此微光配方类型<b>不</b>匹配大型炼药锅，
 * 避免它在自己的循环里再处理一遍。</p>
 */
@ParametersAreNonnullByDefault
public class ShimmerReactionBehavior implements IAnvilBehavior {
    public static final ShimmerReactionBehavior INSTANCE = new ShimmerReactionBehavior();

    @Override
    public boolean handle(Level level, BlockPos pos, BlockState state, float fallDistance, AnvilEvent.OnLand event) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        if (serverLevel.getBlockEntity(pos) instanceof FishTankBlockEntity tank) {
            if (!ConfluenceShimmer.isShimmer(tank.getFluidHandler().getFluid())) return false;
            boolean settled;
            tank.beginRecipeProcessing();
            try {
                settled = settle(serverLevel, pos, tank.getInputHandler(), tank::insertRecipeOutput);
            } finally {
                tank.finishRecipeProcessing();
            }
            if (settled) tank.setChanged();
            return settled;
        }

        LargeCauldronBlockEntity cauldron = ConfluenceShimmer.largeCauldron(serverLevel, pos, state);
        if (cauldron == null || !ConfluenceShimmer.hasShimmer(cauldron)) return false;
        boolean settled = settle(serverLevel, pos, cauldron.getInputHandler(), cauldron::insertRecipeOutput);
        if (settled) cauldron.setChanged();
        return settled;
    }

    /** 从原料格里找出第一份可结算的物品，整组结算并把产物写回容器。 */
    private static boolean settle(ServerLevel level, BlockPos pos, IItemHandler input, UnaryOperator<ItemStack> output) {
        for (int slot = 0; slot < input.getSlots(); slot++) {
            ItemStack stack = input.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            // null = 黑名单 / 未开启微光分解 / 没有匹配的配方，保持原样
            ConfluenceShimmer.Result result = ConfluenceShimmer.resolve(level, stack);
            if (result == null || result.consume() <= 0 || stack.getCount() < result.consume()) continue;

            // LD_Anvil: extractItem方法似乎最多只能扣除一组物品的数量，防止产物翻倍故循环扣除。
            int consumeCount = result.consume();
            while (consumeCount >= stack.getMaxStackSize()) {
                input.extractItem(slot, stack.getMaxStackSize(), false);
                consumeCount -= stack.getMaxStackSize();
            }
            input.extractItem(slot, consumeCount, false);

            for (ItemStack produced : result.outputs()) {
                ItemStack leftover = output.apply(produced);
                if (!leftover.isEmpty()) {
                    Block.popResource(level, pos.above(), leftover);
                }
            }
            // 一次铁砧落地处理一组
            return true;
        }
        return false;
    }
}
