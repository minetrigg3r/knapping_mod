package net.Minetrigg3r.BaseMod.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * Base class for all knapping recipes.
 * This provides the common functionality that all knapping recipes share.
 */
public class KnappingRecipe implements Recipe<Container> {
    private final List<Integer> buttonPattern;
    private final ItemStack output;
    private final int materialCost;
    private final int count;
    private final ResourceLocation id;
    private final String knappingType;
    private final Item materialItem;
    private final String requiredToolType;
    
    /**
     * Creates a new knapping recipe.
     * 
     * @param buttonPattern The pattern of buttons to press
     * @param output The output item
     * @param materialCost The cost in material items
     * @param count The count of output items
     * @param knappingType The type of knapping (e.g., "leather", "clay", "pumpkin")
     * @param materialItem The material item used for this knapping
     * @param requiredToolType The type of tool required (e.g., "hand", "cutting", "carving")
     */
    public KnappingRecipe(List<Integer> buttonPattern, ItemStack output, int materialCost, int count, 
                         String knappingType, Item materialItem, String requiredToolType) {
        this.buttonPattern = buttonPattern;
        this.output = output;
        this.materialCost = materialCost;
        this.count = count;
        this.knappingType = knappingType;
        this.materialItem = materialItem;
        this.requiredToolType = requiredToolType;
        this.id = new ResourceLocation("basemod", knappingType + "_knapping_" + 
                ForgeRegistries.ITEMS.getKey(output.getItem()).getPath());
    }
    
    /**
     * Checks if the pressed buttons match this recipe's pattern.
     * A match occurs when the pressed buttons exactly match the recipe's pattern.
     * No extra or missing buttons are allowed.
     */
    public boolean matches(List<Integer> pressedButtons) {
        // Check that the lists have the same size
        if (pressedButtons.size() != buttonPattern.size()) {
            return false;
        }
        
        // Check that all buttons match exactly
        return pressedButtons.containsAll(buttonPattern) && buttonPattern.containsAll(pressedButtons);
    }
    
    /**
     * Gets the output of this recipe.
     */
    public ItemStack getOutput() {
        ItemStack stack = output.copy();
        stack.setCount(count);
        return stack;
    }
    
    /**
     * Gets the cost in material items.
     */
    public int getMaterialCost() {
        return materialCost;
    }
    
    /**
     * Gets the button pattern.
     */
    public List<Integer> getButtonPattern() {
        return buttonPattern;
    }
    
    /**
     * Gets the count of output items.
     */
    public int getCount() {
        return count;
    }
    
    /**
     * Gets the knapping type.
     */
    public String getKnappingType() {
        return knappingType;
    }
    
    /**
     * Gets the material item for this knapping recipe.
     */
    public Item getMaterialItem() {
        return materialItem;
    }
    
    /**
     * Gets the tool type needed for this knapping recipe.
     */
    public String getRequiredToolType() {
        return requiredToolType;
    }
    
    @Override
    public ResourceLocation getId() {
        return id;
    }
    
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }
    
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return getOutput();
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return KnappingRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return KnappingRecipeType.INSTANCE;
    }
    
    @Override
    public boolean matches(Container container, Level level) {
        return false; // This is not used for knapping recipes
    }
    
    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return getOutput();
    }
} 