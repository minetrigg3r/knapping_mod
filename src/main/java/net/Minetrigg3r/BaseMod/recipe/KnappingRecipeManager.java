package net.Minetrigg3r.BaseMod.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.Minetrigg3r.BaseMod.block.blocks;
import net.Minetrigg3r.BaseMod.item.items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Manages all knapping recipes in the mod.
 * This provides a central registry for all knapping recipes.
 */
public class KnappingRecipeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(KnappingRecipeManager.class);
    private static final Map<String, List<KnappingRecipe>> RECIPES_BY_TYPE = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Initializes the recipe manager.
     * This should be called during mod initialization.
     */
    public static void init() {
        if (initialized) {
            LOGGER.warn("KnappingRecipeManager already initialized, skipping initialization");
            return;
        }
        
        LOGGER.info("Initializing KnappingRecipeManager");
        
        // Clear any existing recipes
        RECIPES_BY_TYPE.clear();
        
        // Register clay knapping recipes
        registerClayRecipes();
        
        // Register leather knapping recipes
        registerLeatherRecipes();
        
        LOGGER.info("KnappingRecipeManager initialized with {} recipe types", RECIPES_BY_TYPE.size());
        for (String type : RECIPES_BY_TYPE.keySet()) {
            LOGGER.info(" - {} recipes: {}", type, RECIPES_BY_TYPE.get(type).size());
        }
        
        initialized = true;
    }
    
    /**
     * Registers clay knapping recipes.
     */
    private static void registerClayRecipes() {
        List<KnappingRecipe> recipes = new ArrayList<>();
        
        // Clay pot pattern
        List<Integer> clayPotPattern = List.of(
            1, 3, 5,         // Top row
            6, 8, 10,       // Second row 
            11, 15,        // Middle row
            16, 17, 18, 19, 20,      // Fourth row
            21, 22, 23, 24, 25 // Bottom row
        );
        
        // Large clay pot pattern
        List<Integer> largeClayPotPattern = List.of(
            1, 3, 5,        // Top row
            7, 8, 9,         // Second row 
            12, 13, 14,        // Middle row
            17, 18, 19        // Fourth row
        );
        
        // Clay brick pattern
        List<Integer> clayBrickPattern = List.of(
            11, 12, 13, 14, 15 // Middle row
        );
        
        // Add unfired clay pot recipe (3 clay balls)
        recipes.add(new KnappingRecipe(clayPotPattern, new ItemStack(items.UNFIRED_CLAY_POT.get()), 3, 1, 
            "clay", Items.CLAY_BALL, "hand"));
        
        // Add unfired clay brick recipe (2 clay balls)
        recipes.add(new KnappingRecipe(clayBrickPattern, new ItemStack(items.UNFIRED_CLAY_BRICK.get()), 2, 2, 
            "clay", Items.CLAY_BALL, "hand"));
        
        // Add large clay pot recipe (8 clay balls)
        recipes.add(new KnappingRecipe(largeClayPotPattern, new ItemStack(blocks.UNFIRED_LARGE_CLAY_POT.get()), 8, 1, 
            "clay", Items.CLAY_BALL, "hand"));
        
        registerRecipesByType(recipes, "clay");
    }
    
    /**
     * Registers leather knapping recipes.
     */
    private static void registerLeatherRecipes() {
        List<KnappingRecipe> recipes = new ArrayList<>();
        
        // Boot pattern
        List<Integer> bootPattern = List.of(
            3,
            8,
            13,
            18,
            23
        );
        
        // Leggings pattern
        List<Integer> leggingsPattern = List.of(
            
            13,
            18,
            23
        );
        
        // Chestplate pattern
        List<Integer> chestplatePattern = List.of(
            2, 3, 4,
            8
            
        );
        
        // Helmet pattern
        List<Integer> helmetPattern = List.of(
            
            
            12, 13, 14,
            16, 17, 18, 19, 20,
            21, 22, 23, 24, 25
        );

        List<Integer> cutLeather = List.of(
            1, 2, 3, 4, 5,
            6, 10, 
            11, 15,
            16, 20,
            21, 22, 23, 24, 25
        );

        List<Integer> horseArmor = List.of(
            1, 2, 3,
             
            15,
            16, 20,
            21, 22, 24, 25
        );

        List<Integer> saddle = List.of(
            1, 2, 3, 4, 5,
            6, 10, 
            11, 15,
            16, 20,
            21, 22, 24, 25
        );
        
        // Add leather boots recipe (2 leather)
        recipes.add(new KnappingRecipe(bootPattern, new ItemStack(Items.LEATHER_BOOTS), 2, 1, 
            "leather", Items.LEATHER, "cutting"));
        
        // Add leather leggings recipe (3 leather)
        recipes.add(new KnappingRecipe(leggingsPattern, new ItemStack(Items.LEATHER_LEGGINGS), 3, 1, 
            "leather", Items.LEATHER, "cutting"));
        
        // Add leather chestplate recipe (4 leather)
        recipes.add(new KnappingRecipe(chestplatePattern, new ItemStack(Items.LEATHER_CHESTPLATE), 4, 1, 
            "leather", Items.LEATHER, "cutting"));
        
        // Add leather helmet recipe (1 leather)
        recipes.add(new KnappingRecipe(helmetPattern, new ItemStack(Items.LEATHER_HELMET), 1, 1, 
            "leather", Items.LEATHER, "cutting"));
            
        recipes.add(new KnappingRecipe(cutLeather, new ItemStack(items.CUT_LEATHER.get()), 1, 1, 
            "leather", Items.LEATHER, "cutting"));

        recipes.add(new KnappingRecipe(horseArmor, new ItemStack(Items.LEATHER_HORSE_ARMOR), 8, 1, 
            "leather", Items.LEATHER, "cutting"));

        recipes.add(new KnappingRecipe(saddle, new ItemStack(Items.SADDLE), 3, 1, 
            "leather", Items.LEATHER, "cutting"));
        
        registerRecipesByType(recipes, "leather");
    }
    
    /**
     * Registers a list of recipes by their type.
     */
    private static void registerRecipesByType(List<KnappingRecipe> recipes, String type) {
        List<KnappingRecipe> typeRecipes = RECIPES_BY_TYPE.computeIfAbsent(type, k -> new ArrayList<>());
        for (KnappingRecipe recipe : recipes) {
            typeRecipes.add(recipe);
            LOGGER.debug("Added {} recipe for {} with pattern: {}", 
                recipe.getKnappingType(), recipe.getOutput().getItem(), recipe.getButtonPattern());
        }
        
        LOGGER.info("Registered {} {} knapping recipes", typeRecipes.size(), type);
    }

    /**
     * Finds a matching recipe for the given pressed buttons, trying all recipe types.
     * 
     * @param pressedButtons The buttons that were pressed
     * @return The matching recipe, or null if no match was found
     */
    public static KnappingRecipe findMatchingRecipe(List<Integer> pressedButtons) {
        for (String type : RECIPES_BY_TYPE.keySet()) {
            KnappingRecipe recipe = findMatchingRecipe(pressedButtons, type);
            if (recipe != null) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Finds a matching recipe for the given pressed buttons and knapping type.
     * 
     * @param pressedButtons The buttons that were pressed
     * @param knappingType The type of knapping (e.g., "leather", "clay")
     * @return The matching recipe, or null if no match was found
     */
    public static KnappingRecipe findMatchingRecipe(List<Integer> pressedButtons, String knappingType) {
        List<KnappingRecipe> typeRecipes = RECIPES_BY_TYPE.get(knappingType);
        if (typeRecipes == null) {
            LOGGER.warn("No recipes found for knapping type: {}", knappingType);
            return null;
        }
        
        for (KnappingRecipe recipe : typeRecipes) {
            if (recipe.matches(pressedButtons)) {
                return recipe;
            }
        }
        
        return null;
    }

    /**
     * Gets all recipes for a specific knapping type.
     * 
     * @param knappingType The type of knapping (e.g., "leather", "clay")
     * @return The list of recipes for the specified type
     */
    public static List<KnappingRecipe> getRecipesByType(String knappingType) {
        return RECIPES_BY_TYPE.getOrDefault(knappingType, new ArrayList<>());
    }
    
    /**
     * Gets all registered knapping recipes.
     * 
     * @return A flat list of all knapping recipes
     */
    public static List<KnappingRecipe> getAllRecipes() {
        List<KnappingRecipe> allRecipes = new ArrayList<>();
        for (List<KnappingRecipe> typeRecipes : RECIPES_BY_TYPE.values()) {
            allRecipes.addAll(typeRecipes);
        }
        return allRecipes;
    }

    /**
     * Registers a single knapping recipe.
     * 
     * @param recipe The recipe to register
     */
    public static void registerRecipe(KnappingRecipe recipe) {
        String type = recipe.getKnappingType();
        List<KnappingRecipe> typeRecipes = RECIPES_BY_TYPE.computeIfAbsent(type, k -> new ArrayList<>());
        typeRecipes.add(recipe);
        LOGGER.info("Registered {} knapping recipe for {} with pattern: {}", 
            type, recipe.getOutput().getItem(), recipe.getButtonPattern());
    }
} 