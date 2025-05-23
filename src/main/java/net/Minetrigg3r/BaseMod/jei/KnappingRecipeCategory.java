package net.Minetrigg3r.BaseMod.jei;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.Minetrigg3r.BaseMod.BaseMod;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KnappingRecipeCategory implements IRecipeCategory<KnappingRecipe> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KnappingRecipeCategory.class);
    private static final ResourceLocation CLAY_TEXTURE = new ResourceLocation(BaseMod.MOD_ID, "textures/gui/jei/clay_knapping.png");
    private static final ResourceLocation CLAY_BUTTON = new ResourceLocation(BaseMod.MOD_ID, "textures/screens/clay_button.png");
    private static final ResourceLocation LEATHER_BUTTON = new ResourceLocation(BaseMod.MOD_ID, "textures/screens/leather_button.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final String knappingType;
    private final IDrawable buttonTexture;
    private final ItemStack indicatorItem;

    public KnappingRecipeCategory(IGuiHelper guiHelper, String knappingType) {
        this.knappingType = knappingType;
        this.background = guiHelper.drawableBuilder(
            CLAY_TEXTURE,
            0, 0, 174, 100)
            .setTextureSize(174, 100)
            .build();
        this.icon = guiHelper.createDrawableItemStack(
            new ItemStack(knappingType.equals("clay") ? Items.CLAY_BALL : Items.LEATHER));
        this.buttonTexture = guiHelper.drawableBuilder(
            knappingType.equals("clay") ? CLAY_BUTTON : LEATHER_BUTTON,
            0, 0, 16, 16)
            .setTextureSize(16, 16)
            .build();
        this.indicatorItem = new ItemStack(knappingType.equals("clay") ? Items.CLAY_BALL : Items.LEATHER);
    }

    @Override
    public RecipeType<KnappingRecipe> getRecipeType() {
        return new RecipeType<>(new ResourceLocation(BaseMod.MOD_ID, knappingType + "_knapping"), KnappingRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.basemod." + knappingType + "_knapping");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
    
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, KnappingRecipe recipe, IFocusGroup focuses) {
        LOGGER.info("Setting up recipe layout for: {}", recipe.getId());
        try {
            // Add output item
            builder.addSlot(RecipeIngredientRole.OUTPUT, 142, 42)
                .addItemStack(recipe.getResultItem(null));

            LOGGER.info("Recipe layout setup completed successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to set up recipe layout", e);
        }
    }
    
    @Override
    public void draw(KnappingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        try {
            // Draw the 5x5 grid of buttons
            int startX = 10;
            int startY = 7;
            int spacing = 16;
            List<Integer> pattern = recipe.getButtonPattern();
            
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    int buttonNumber = row * 5 + col + 1;
                    int x = startX + (col * spacing);
                    int y = startY + (row * spacing);
        
                    // Draw buttons that are NOT in the pattern (these are the ones that stay)
                    if (!pattern.contains(buttonNumber)) {
                        buttonTexture.draw(guiGraphics, x, y);
                    }
                }
            }

            // Draw cost counter
            ItemStack costItem = new ItemStack(knappingType.equals("clay") ? Items.CLAY_BALL : Items.LEATHER);
            int cost = recipe.getMaterialCost();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(141 + 20, 42 + 20, 0);
            guiGraphics.pose().scale(0.6f, 0.6f, 1.0f);
            guiGraphics.renderItem(costItem, -8, -8);
            guiGraphics.renderItemDecorations(Minecraft.getInstance().font, costItem, -8, -8, String.valueOf(cost));
            guiGraphics.pose().popPose();

            LOGGER.info("Recipe drawing completed successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to draw recipe", e);
        }
    }

    @Override
    public List<Component> getTooltipStrings(KnappingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        return List.of();
    }
} 