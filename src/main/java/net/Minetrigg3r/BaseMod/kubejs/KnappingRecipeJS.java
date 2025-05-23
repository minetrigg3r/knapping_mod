package net.Minetrigg3r.BaseMod.kubejs;

import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.item.Items;

public class KnappingRecipeJS extends RecipeJS {
    @Info(value = "Sets the pattern for the knapping recipe")
    public KnappingRecipeJS pattern(Integer[] pattern) {
        setValue(BaseModKubeJSPlugin.PATTERN, pattern);
        return this;
    }

    @Info(value = "Sets the result of the knapping recipe")
    public KnappingRecipeJS result(OutputItem result) {
        setValue(BaseModKubeJSPlugin.RESULT, result);
        return this;
    }

    @Info(value = "Sets the material cost for the knapping recipe")
    public KnappingRecipeJS materialCost(int cost) {
        setValue(BaseModKubeJSPlugin.MATERIAL_COST, cost);
        return this;
    }

    @Info(value = "Sets the count of the result for the knapping recipe")
    public KnappingRecipeJS count(int count) {
        setValue(BaseModKubeJSPlugin.COUNT, count);
        return this;
    }

    @Info(value = "Sets the type of knapping for the recipe (clay or leather)")
    public KnappingRecipeJS knappingType(String type) {
        if (!type.equals("clay") && !type.equals("leather")) {
            throw new IllegalArgumentException("Knapping type must be either 'clay' or 'leather'");
        }
        setValue(BaseModKubeJSPlugin.KNAPPING_TYPE, type);
        return this;
    }
} 