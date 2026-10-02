package dev.hail.confluence_anvil_craft.item;

import dev.dubhe.anvilcraft.init.ModMenuTypes;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.inventory.*;
import dev.dubhe.anvilcraft.item.AnvilHammerItem;
import dev.hail.confluence_anvil_craft.inventory.PortableHardmodeAnvilMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static org.confluence.mod.common.item.common.BaseHammerItem.hammerMineBlock;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HardmodeAnvilHammerItem extends AnvilHammerItem {
    public static final int ANVIL_HAMMER_MODE = 0;
    public static final int MINING_HAMMER_MODE = 1;
    public static final int PICKAXE_MODE = 2;

    public final Tier tier;

    public HardmodeAnvilHammerItem(Properties properties, Tier tier) {
        super(properties.durability(10000)
                .component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE)
                .component(ConfluenceMagicLib.MOD_RARITY, ModRarity.RED)
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                                        BASE_ATTACK_DAMAGE_ID,
                                        5 + tier.getAttackDamageBonus(),
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build()));
        this.tier = tier;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide && livingEntity instanceof ServerPlayer player) {
            int slot = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
            if (player instanceof ServerPlayer serverPlayer) {
                OpenedHammerSource source = OpenedHammerSource.fromInventory(serverPlayer.getInventory(), slot);
                if (source != null) {
                    if (serverPlayer.containerMenu.getCarried().isEmpty()) {
                        if (serverPlayer.containerMenu != serverPlayer.inventoryMenu) {
                            serverPlayer.closeContainer();
                        }
                        MenuProvider provider = new SimpleMenuProvider((id, playerInventory, menuPlayer) ->
                                new PortableHardmodeAnvilMenu(id, playerInventory,
                                        ContainerLevelAccess.create(serverPlayer.level(), serverPlayer.getOnPos()), source),
                                stack.getHoverName());
                        ModMenuTypes.open(serverPlayer, provider);
                    }
                }
            }
        }
        return stack;
    }

    public static Tool createToolProperties(@Range(from = 0, to = 4) int mode, Tier tier) {
        return switch (mode) {
            case ANVIL_HAMMER_MODE, MINING_HAMMER_MODE -> new Tool(
                    List.of(Tool.Rule.minesAndDrops(ModTags.Blocks.MINEABLE_WITH_HAMMER, tier.getSpeed())), 1.0f, 1);
            case PICKAXE_MODE -> new Tool(
                    List.of(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, tier.getSpeed())), 1.0f, 1);
            default -> throw new IllegalStateException("Unexpected mode: " + mode);
        };
    }

    public static int getMode(ItemStack item) {
        return Math.clamp(item.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.DEFAULT).value(), 0, 8);
    }

    public static Component getModeName(ItemStack item){
        int mode = getMode(item);
        return switch (mode){
            case ANVIL_HAMMER_MODE -> Component.translatable("screen.confluence_anvil_craft.hardmode_anvil_hammer.anvil_hammer_mode");
            case MINING_HAMMER_MODE -> Component.translatable("screen.confluence_anvil_craft.hardmode_anvil_hammer.mining_hammer_mode");
            case PICKAXE_MODE -> Component.translatable("screen.confluence_anvil_craft.hardmode_anvil_hammer.pickaxe_mode");
            default -> throw new IllegalStateException("Unexpected mode: " + mode);
        };
    }

    public static void setMode(Player player, InteractionHand hand, @Range(from = 0, to = 2) int mode) {
        ItemStack anvilHammer = player.getItemInHand(hand);
        if (!anvilHammer.is(ModItemTags.ANVIL_HAMMER)) return;
        Item item = anvilHammer.getItem();
        if (item instanceof HardmodeAnvilHammerItem hardmodeAnvilHammerItem) {
            anvilHammer.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(mode));
            anvilHammer.set(DataComponents.TOOL, createToolProperties(mode, hardmodeAnvilHammerItem.tier));
        }
    }

    public static Boolean isAnvilHammerMode (ItemStack stack){
        return HardmodeAnvilHammerItem.getMode(stack) == HardmodeAnvilHammerItem.ANVIL_HAMMER_MODE;
    }

    @Override
    public Block getAnvil() {
        if (tier == ModTiers.ORICHALCUM) return FunctionalBlocks.ORICHALCUM_ANVIL.get();
        return FunctionalBlocks.MYTHRIL_ANVIL.get();
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !isAnvilHammerMode(player.getItemInHand(InteractionHand.MAIN_HAND));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (isAnvilHammerMode(stack)) return 0.0f;
        Tool tool = stack.get(DataComponents.TOOL);
        return tool != null ? tool.getMiningSpeed(state) : tier.getSpeed();
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (isAnvilHammerMode(stack)) return false;
        if (HardmodeAnvilHammerItem.getMode(stack) == MINING_HAMMER_MODE)
            hammerMineBlock(stack, level, state, pos, miningEntity);
        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        // Founded by Deepseek
        if (isAnvilHammerMode(stack)) return false;
        Tool tool = stack.get(DataComponents.TOOL);
        return tool != null && tool.isCorrectForDrops(state);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("message.confluence.toolmode.current").withStyle(ChatFormatting.GRAY)
                .append(getModeName(stack)).withStyle(ChatFormatting.WHITE));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
