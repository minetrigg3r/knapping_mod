package net.Minetrigg3r.BaseMod.recipe;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.Minetrigg3r.BaseMod.BaseMod;

public class KnappingRecipeType implements RecipeType<KnappingRecipe> {
    private static final ResourceLocation ID = new ResourceLocation(BaseMod.MOD_ID, "knapping");
    public static final KnappingRecipeType INSTANCE = new KnappingRecipeType();

    private KnappingRecipeType() {}

    @Override
    public String toString() {
        return ID.toString();
    }
} 