package net.Minetrigg3r.BaseMod.world.inventory;

import net.Minetrigg3r.BaseMod.init.ModMenuTypes;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipe;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipeManager;
import net.Minetrigg3r.BaseMod.network.NetworkHandler;
import net.Minetrigg3r.BaseMod.network.TestMenuSlotMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

public class TestMenu extends AbstractContainerMenu {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestMenu.class);
	public final static HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final BlockPos pos;
	public final Player entity;
	public final ItemStackHandler inventory;
	private final Set<Integer> pressedButtons = new HashSet<>();
	private int currentRecipeCost = 0; // Store the current recipe's clay cost

	public TestMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(ModMenuTypes.KNAPPING.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.pos = extraData.readBlockPos();
		this.inventory = new ItemStackHandler(1); // Changed to 1 slot for output only
		
		// Add output slot
		this.addSlot(new SlotItemHandler(inventory, 0, 133, 47) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false; // Output slot
			}

			@Override
			public void onTake(Player player, ItemStack stack) {
				super.onTake(player, stack);
				// Send network packet to sync changes
				NetworkHandler.sendToServer(new TestMenuSlotMessage(0, stack, currentRecipeCost));
				// Close the GUI
				player.closeContainer();
			}
		});
		this.addPlayerInventory(inv);
	}

	public TestMenu(int id, Inventory inv, BlockEntity entity) {
		super(ModMenuTypes.KNAPPING.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.pos = entity.getBlockPos();
		this.inventory = new ItemStackHandler(1); // Changed to 1 slot for output only
		
		// Add output slot
		this.addSlot(new SlotItemHandler(inventory, 0, 133, 47) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false; // Output slot
			}

			@Override
			public void onTake(Player player, ItemStack stack) {
				super.onTake(player, stack);
				// Send network packet to sync changes
				NetworkHandler.sendToServer(new TestMenuSlotMessage(0, stack, currentRecipeCost));
				// Close the GUI
				player.closeContainer();
			}
		});
		this.addPlayerInventory(inv);
	}

	public void setPressedButtons(Set<Integer> buttons) {
		this.pressedButtons.clear();
		this.pressedButtons.addAll(buttons);
		
		// Convert Set to List for recipe matching
		List<Integer> buttonList = new ArrayList<>(pressedButtons);
		
		KnappingRecipe recipe = KnappingRecipeManager.findMatchingRecipe(buttonList);
		
		if (recipe != null) {
			// Check if we have enough clay balls in hand
			ItemStack heldItem = entity.getMainHandItem();
			if (heldItem.getItem() == Items.CLAY_BALL && heldItem.getCount() >= recipe.getMaterialCost()) {
				// Store the clay cost for when the item is taken
				currentRecipeCost = recipe.getMaterialCost();
				// Set the output
				inventory.setStackInSlot(0, recipe.getOutput());
			} else {
				// Clear output if not enough clay balls
				inventory.setStackInSlot(0, ItemStack.EMPTY);
				currentRecipeCost = 0;
			}
		} else {
			// Clear output if no recipe matches
			inventory.setStackInSlot(0, ItemStack.EMPTY);
			currentRecipeCost = 0;
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return true; // Always allow the menu to stay open
	}

	private void addPlayerInventory(Inventory playerInventory) {
		for (int i = 0; i < 3; i++) {
			for (int l = 0; l < 9; l++) {
				this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 7 + l * 18, 109 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 7 + i * 18, 167));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 1) {
				// Handle output slot
				// Send network packet to sync changes for output slot
				if (currentRecipeCost > 0) {
					NetworkHandler.sendToServer(new TestMenuSlotMessage(0, itemstack1, currentRecipeCost));
					// Add the item directly to the player's inventory
					if (!playerIn.getInventory().add(itemstack1)) {
						playerIn.drop(itemstack1, false);
					}
					slot.setByPlayer(ItemStack.EMPTY);
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack1.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return itemstack;
	}
}
