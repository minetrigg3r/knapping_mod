package net.Minetrigg3r.BaseMod.world.inventory;

import net.Minetrigg3r.BaseMod.BaseMod;
import net.Minetrigg3r.BaseMod.init.ModMenuTypes;
import net.Minetrigg3r.BaseMod.network.KnappingSlotMessage;
import net.Minetrigg3r.BaseMod.network.NetworkHandler;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipe;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic container menu for knapping recipes.
 */
public class KnappingMenu extends AbstractContainerMenu {
    private static final Logger LOGGER = LoggerFactory.getLogger(KnappingMenu.class);
    
    private final Level world;
    private final Player player;
    private final IItemHandler playerInventory;
    private final ItemStackHandler outputHandler;
    private final BlockPos pos;
    private final List<Integer> pressedButtons = new ArrayList<>();
    private int currentMaterialCost = 0;
    private final String toolType;
    private final Item materialItem;
    private final String knappingType;
    private final List<KnappingRecipe> recipes;
    private KnappingRecipe currentRecipe;
    
    /**
     * Creates a new knapping menu from extra data.
     */
    public KnappingMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(
            containerId, 
            playerInventory, 
            extraData != null ? extraData.readBlockPos() : BlockPos.ZERO,
            extraData != null ? extraData.readUtf() : "",
            extraData != null ? Item.byId(extraData.readInt()) : null,
            extraData != null ? extraData.readUtf() : "",
            extraData != null ? extraData.readUtf() : "",
            ModMenuTypes.KNAPPING.get()
        );
    }
    
    /**
     * Creates a new knapping menu.
     *
     * @param containerId The container ID
     * @param playerInventory The player's inventory
     * @param pos The block position
     * @param toolType The tool type required for this knapping (e.g., "cutting", "hand", "carving")
     * @param materialItem The material item used for this knapping
     * @param knappingType The type of knapping (e.g., "leather", "clay")
     * @param recipesClass The class name of the recipes to use (unused now, kept for compatibility)
     * @param menuType The menu type
     */
    public KnappingMenu(int containerId, Inventory playerInventory, BlockPos pos, 
                         String toolType, Item materialItem, String knappingType, 
                         String recipesClass, MenuType<? extends KnappingMenu> menuType) {
        super(menuType, containerId);
        
        this.world = playerInventory.player.level();
        this.player = playerInventory.player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.outputHandler = new ItemStackHandler(1);
        this.pos = pos;
        this.toolType = toolType;
        this.materialItem = materialItem;
        this.knappingType = knappingType;
        this.recipes = new ArrayList<>();
        
        // Add output slot
        this.addSlot(new SlotItemHandler(outputHandler, 0, 133, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
            
            @Override
            public void onTake(Player thePlayer, ItemStack stack) {
                super.onTake(thePlayer, stack);
                // Send a network message to handle material consumption and tool usage
                NetworkHandler.sendToServer(new KnappingSlotMessage(0, stack, currentMaterialCost, toolType, materialItem));
            }
        });
        
        // Add player inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 7 + col * 18, 84 + row * 18 + 25));
            }
        }
        
        // Add player hotbar slots
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 7 + col * 18, 142 + 25));
        }
        
        // Load recipes
        loadRecipes();
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index == 0) {
                // If taking from output slot
                if (!this.moveItemStackTo(stack, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onTake(player, stack);
            } else if (!this.moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }
    
    public void setOutput(ItemStack stack) {
        this.outputHandler.setStackInSlot(0, stack);
    }
    
    public ItemStack getOutput() {
        return this.outputHandler.getStackInSlot(0);
    }
    
    public void setPressedButtons(List<Integer> buttons) {
        this.pressedButtons.clear();
        this.pressedButtons.addAll(buttons);
        // Check recipe when buttons are pressed
        checkRecipe();
    }
    
    private void checkRecipe() {
        LOGGER.info("Checking recipes. Pressed buttons: {}, Knapping type: {}, Material: {}", 
            pressedButtons, knappingType, materialItem);
            
        // First check recipes in the menu
        for (KnappingRecipe recipe : recipes) {
            if (recipe.getKnappingType().equals(knappingType) && recipe.matches(pressedButtons)) {
                currentRecipe = recipe;
                currentMaterialCost = recipe.getMaterialCost();
                // Set output to the recipe result
                outputHandler.setStackInSlot(0, recipe.getOutput());
                LOGGER.info("Recipe found in menu! Setting output to: {}", recipe.getOutput().getItem());
                return;
            }
        }
        
        // If no match found in menu, check KnappingRecipeManager
        KnappingRecipe managerRecipe = KnappingRecipeManager.findMatchingRecipe(pressedButtons, knappingType);
        if (managerRecipe != null) {
            currentRecipe = managerRecipe;
            currentMaterialCost = managerRecipe.getMaterialCost();
            // Set output to the recipe result
            outputHandler.setStackInSlot(0, managerRecipe.getOutput());
            LOGGER.info("Recipe found in manager! Setting output to: {}", managerRecipe.getOutput().getItem());
            return;
        }
        
        // No matching recipe found
        outputHandler.setStackInSlot(0, ItemStack.EMPTY);
        currentMaterialCost = 0;
        currentRecipe = null;
        LOGGER.info("No matching recipe found.");
    }
    
    public String getToolType() {
        return toolType;
    }
    
    public Item getMaterialItem() {
        return materialItem;
    }
    
    public String getKnappingType() {
        return knappingType;
    }
    
    /**
     * Creates a knapping menu title based on the knapping type.
     */
    public static Component createTitle(String knappingType) {
        return Component.translatable("container." + knappingType + "_knapping");
    }
    
    private void loadRecipes() {
        // Get recipes from the recipe manager
        List<KnappingRecipe> allRecipes = world.getRecipeManager().getAllRecipesFor(net.Minetrigg3r.BaseMod.recipe.KnappingRecipeType.INSTANCE);
        for (KnappingRecipe recipe : allRecipes) {
            if (recipe.getKnappingType().equals(knappingType)) {
                recipes.add(recipe);
            }
        }
        
        // Get recipes from KnappingRecipeManager
        List<KnappingRecipe> managerRecipes = KnappingRecipeManager.getRecipesByType(knappingType);
        for (KnappingRecipe recipe : managerRecipes) {
            if (!recipes.contains(recipe)) {
                recipes.add(recipe);
            }
        }
        
        LOGGER.info("Loaded {} recipes for knapping type {}", recipes.size(), knappingType);
    }
    
    public KnappingRecipe getCurrentRecipe() {
        return currentRecipe;
    }
    
    public int getMaterialCost() {
        return currentMaterialCost;
    }
} 
 
 
 