package net.Minetrigg3r.BaseMod.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class KnappingRecipeSerializer implements RecipeSerializer<KnappingRecipe> {
    public static final KnappingRecipeSerializer INSTANCE = new KnappingRecipeSerializer();

    @Override
    public KnappingRecipe fromJson(ResourceLocation id, JsonObject json) {
        try {
            List<Integer> pattern = new ArrayList<>();
            json.getAsJsonArray("pattern").forEach(element -> 
                pattern.add(element.getAsInt())
            );
            
            ItemStack output = new ItemStack(ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(json.get("result").getAsString())
            ));
            
            int materialCost = json.has("material_cost") ? json.get("material_cost").getAsInt() : 1;
            int count = json.has("count") ? json.get("count").getAsInt() : 1;
            String type = json.has("knapping_type") ? json.get("knapping_type").getAsString() : "clay";
            
            // Get material item based on type
            Item materialItem;
            String requiredToolType;
            
            switch (type) {
                case "clay":
                    materialItem = Items.CLAY_BALL;
                    requiredToolType = "hand";
                    break;
                case "leather":
                    materialItem = Items.LEATHER;
                    requiredToolType = "cutting";
                    break;
                default:
                    throw new JsonSyntaxException("Unknown knapping type: " + type);
            }
            
            return new KnappingRecipe(pattern, output, materialCost, count, type, materialItem, requiredToolType);
        } catch (Exception e) {
            throw new JsonSyntaxException("Invalid knapping recipe: " + e.getMessage());
        }
    }

    @Override
    public KnappingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        List<Integer> pattern = new ArrayList<>();
        int patternSize = buffer.readInt();
        for (int i = 0; i < patternSize; i++) {
            pattern.add(buffer.readInt());
        }
        
        ItemStack output = buffer.readItem();
        int materialCost = buffer.readInt();
        int count = buffer.readInt();
        String type = buffer.readUtf();
        
        // Get material item based on type
        Item materialItem;
        String requiredToolType;
        
        switch (type) {
            case "clay":
                materialItem = Items.CLAY_BALL;
                requiredToolType = "hand";
                break;
            case "leather":
                materialItem = Items.LEATHER;
                requiredToolType = "cutting";
                break;
            default:
                throw new IllegalStateException("Unknown knapping type: " + type);
        }
        
        return new KnappingRecipe(pattern, output, materialCost, count, type, materialItem, requiredToolType);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, KnappingRecipe recipe) {
        buffer.writeInt(recipe.getButtonPattern().size());
        recipe.getButtonPattern().forEach(buffer::writeInt);
        buffer.writeItem(recipe.getOutput());
        buffer.writeInt(recipe.getMaterialCost());
        buffer.writeInt(recipe.getCount());
        buffer.writeUtf(recipe.getKnappingType());
    }
} 