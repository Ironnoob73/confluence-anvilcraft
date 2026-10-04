package dev.hail.confluence_anvil_craft.integration.jei;

import dev.dubhe.anvilcraft.client.support.RenderSupport;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.integration.jei.category.anvil.liquid.AbstractLiquidCategory;
import dev.dubhe.anvilcraft.integration.jei.drawable.DrawableBlockStateIcon;
import dev.dubhe.anvilcraft.integration.jei.util.JeiRecipeUtil;
import dev.dubhe.anvilcraft.integration.jei.util.JeiRenderHelper;
import dev.dubhe.anvilcraft.integration.jei.util.JeiSlotUtil;
import dev.dubhe.anvilcraft.recipe.component.HasCauldronSimple;
import dev.hail.confluence_anvil_craft.init.CACRecipes;
import dev.hail.confluence_anvil_craft.recipe.ShimmerTransmutationRecipe;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.IntFunction;

/**
 * 微光处理配方的 JEI 分类。
 *
 * <p>By Deepseek: 直接复用铁砧工艺 {@code super_heating}/{@code time_warp} 共用的
 * {@link AbstractLiquidCategory}，因此配方布局、流体槽与概率提示都与原版铁砧加工一致。</p>
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShimmerTransmutationCategory extends AbstractLiquidCategory<ShimmerTransmutationRecipe> {
    public ShimmerTransmutationCategory(IGuiHelper helper) {
        super(helper, new DrawableBlockStateIcon(Blocks.ANVIL.defaultBlockState(), ModBlocks.FISH_TANK.getDefaultState()),
                Component.translatable("gui.confluence_anvil_craft.category.shimmer_transmutation"));
    }

    @Override
    public RecipeType<RecipeHolder<ShimmerTransmutationRecipe>> getRecipeType() {
        return JeiPlugin.SHIMMER_TRANSMUTATION;
    }

    @Override
    public void draw(RecipeHolder<ShimmerTransmutationRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        float anvilYOffset = JeiRenderHelper.getAnvilAnimationOffset(this.timer);
        RenderSupport.renderBlock(guiGraphics, Blocks.ANVIL.defaultBlockState(), 81.0F, 12.0F + anvilYOffset, 20.0F, 12.0F, RenderSupport.SINGLE_BLOCK);
        RenderSupport.renderBlock(guiGraphics, ModBlocks.FISH_TANK.getDefaultState(), 81.0F, 30.0F, 10.0F, 12.0F, RenderSupport.SINGLE_BLOCK);
        this.arrowIn.draw(guiGraphics, 54, 22);
        this.arrowOut.draw(guiGraphics, 92, 22);
        ShimmerTransmutationRecipe recipe = recipeHolder.value();
        HasCauldronSimple cauldron = recipe.getHasCauldron();
        boolean hasInputItems = !recipe.getInputItems().isEmpty();
        boolean hasOutputItems = !recipe.getResultItems().isEmpty();
        boolean hasInputFluid = cauldron.hasFluid();
        boolean hasOutputFluid = !cauldron.transforms().isEmpty();
        boolean inputMixed = hasInputItems && hasInputFluid;
        boolean outputMixed = hasOutputItems && hasOutputFluid;
        if (hasInputItems) {
            if (inputMixed) {
                JeiSlotUtil.drawItemInputSlots(guiGraphics, this.slotDefault, recipe.getInputItems().size());
            } else {
                JeiSlotUtil.drawDefaultInputSlots(guiGraphics, this.slotDefault, recipe.getInputItems().size());
            }
        }

        IntFunction<IDrawable> slot = JeiRecipeUtil.outputSlotFor(recipe.getResultItems(), this.slotDefault, this.slotProbability);
        if (hasOutputItems) {
            if (outputMixed) {
                JeiSlotUtil.drawItemOutputSlots(guiGraphics, slot, recipe.getResultItems().size());
            } else {
                JeiSlotUtil.drawDefaultOutputSlots(guiGraphics, slot, recipe.getResultItems().size());
            }
        }

        if (hasInputFluid) {
            if (inputMixed) {
                JeiSlotUtil.drawFluidInputSlots(guiGraphics, this.slotDefault, 1);
            } else {
                JeiSlotUtil.drawDefaultInputSlots(guiGraphics, this.slotDefault, 1);
            }
        }

        if (hasOutputFluid) {
            IDrawable fluidSlot = cauldron.chance() < 1.0F ? this.slotProbability : this.slotDefault;
            if (outputMixed) {
                JeiSlotUtil.drawFluidOutputSlots(guiGraphics, fluidSlot, cauldron.transforms().size());
            } else {
                JeiSlotUtil.drawDefaultOutputSlots(guiGraphics, fluidSlot, cauldron.transforms().size());
            }
        }

        if (cauldron.ignited()) {
            Component text = Component.translatable("gui.anvilcraft.category.cauldron.need_ignite");
            int textWidth = Minecraft.getInstance().font.width(text);
            guiGraphics.drawString(Minecraft.getInstance().font, text, 81 - textWidth / 2, 55, -16777216, false);
        }

    }

public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(JeiPlugin.SHIMMER_TRANSMUTATION,
                JeiRecipeUtil.getRecipeHoldersFromType(CACRecipes.SHIMMER_TRANSMUTATION.type().get()));
    }
}
