package net.Minetrigg3r.BaseMod.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipe;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Arrays;

public class BaseModKubeJSPlugin extends KubeJSPlugin {
    // Define recipe keys for our knapping recipe
    public static final RecipeKey<Integer[]> PATTERN = NumberComponent.INT.asArray().key("pattern");
    public static final RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key("result");
    public static final RecipeKey<Integer> MATERIAL_COST = NumberComponent.INT.key("material_cost").optional(1);
    public static final RecipeKey<Integer> COUNT = NumberComponent.INT.key("count").optional(1);
    public static final RecipeKey<String> KNAPPING_TYPE = StringComponent.ID.key("knapping_type").optional("clay");
    
    // Create the recipe schema
    public static final RecipeSchema KNAPPING_SCHEMA = new RecipeSchema(
        KnappingRecipeJS.class, 
        KnappingRecipeJS::new, 
        PATTERN, RESULT, MATERIAL_COST, COUNT, KNAPPING_TYPE
    );
    
    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.register(
            new ResourceLocation("basemod", "knapping"),
            KNAPPING_SCHEMA
        );
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        if (event.getType() == ScriptType.SERVER) {
            // We'll add server-side bindings here later
        }
    }

    public void registerRecipes(RegisterRecipeSchemasEvent event) {
        event.register(
            new ResourceLocation("basemod", "knapping"),
            KNAPPING_SCHEMA
        );
    }
} 