package net.Minetrigg3r.BaseMod.jei;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.Minetrigg3r.BaseMod.BaseMod;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipe;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipeManager;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class BaseModJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = new ResourceLocation(BaseMod.MOD_ID, "plugin");
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseModJeiPlugin.class);
    private KnappingRecipeCategory clayKnappingCategory;
    private KnappingRecipeCategory leatherKnappingCategory;

    @Override
    public ResourceLocation getPluginUid() {
        LOGGER.info("Getting plugin UID: {}", PLUGIN_ID);
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        LOGGER.info("Registering JEI categories");
        try {
            clayKnappingCategory = new KnappingRecipeCategory(registration.getJeiHelpers().getGuiHelper(), "clay");
            leatherKnappingCategory = new KnappingRecipeCategory(registration.getJeiHelpers().getGuiHelper(), "leather");
            registration.addRecipeCategories(clayKnappingCategory, leatherKnappingCategory);
            LOGGER.info("JEI categories registered successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to register JEI categories", e);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        LOGGER.info("Registering JEI recipes");
        try {
            // Get recipes from KnappingRecipeManager
            List<KnappingRecipe> managerRecipes = KnappingRecipeManager.getAllRecipes();
            LOGGER.info("Found {} recipes from KnappingRecipeManager", managerRecipes.size());

            // Get recipes from Minecraft recipe manager (includes KubeJS recipes)
            List<KnappingRecipe> minecraftRecipes = Minecraft.getInstance().level.getRecipeManager()
                .getAllRecipesFor(KnappingRecipeType.INSTANCE);
            LOGGER.info("Found {} recipes from Minecraft recipe manager", minecraftRecipes.size());

            // Combine both lists, avoiding duplicates
            List<KnappingRecipe> allRecipes = managerRecipes;
            for (KnappingRecipe recipe : minecraftRecipes) {
                if (!allRecipes.stream().anyMatch(r -> r.getId().equals(recipe.getId()))) {
                    allRecipes.add(recipe);
                }
            }
            LOGGER.info("Total unique recipes: {}", allRecipes.size());

            // Split recipes by type
            List<KnappingRecipe> clayRecipes = allRecipes.stream()
                .filter(recipe -> recipe.getKnappingType().equals("clay"))
                .collect(Collectors.toList());
            List<KnappingRecipe> leatherRecipes = allRecipes.stream()
                .filter(recipe -> recipe.getKnappingType().equals("leather"))
                .collect(Collectors.toList());

            // Register recipes to their respective categories
            registration.addRecipes(clayKnappingCategory.getRecipeType(), clayRecipes);
            registration.addRecipes(leatherKnappingCategory.getRecipeType(), leatherRecipes);
            
            LOGGER.info("JEI recipes registered successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to register JEI recipes", e);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        LOGGER.info("Registering JEI recipe catalysts");
        try {
            registration.addRecipeCatalyst(new ItemStack(Items.CLAY_BALL), clayKnappingCategory.getRecipeType());
            registration.addRecipeCatalyst(new ItemStack(Items.LEATHER), leatherKnappingCategory.getRecipeType());
            LOGGER.info("JEI recipe catalysts registered successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to register JEI recipe catalysts", e);
        }
    }
} 