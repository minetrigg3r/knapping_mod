package net.Minetrigg3r.BaseMod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

public class TestMenuSlotMessage {
    private final int slot;
    private final ItemStack stack;
    private final int clayCost;

    public TestMenuSlotMessage(int slot, ItemStack stack, int clayCost) {
        this.slot = slot;
        this.stack = stack;
        this.clayCost = clayCost;
    }

    public TestMenuSlotMessage(FriendlyByteBuf buffer) {
        this.slot = buffer.readInt();
        this.stack = buffer.readItem();
        this.clayCost = buffer.readInt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeItem(stack);
        buffer.writeInt(clayCost);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player player = context.getSender();
            if (player != null) {
                // Update the held item (clay balls)
                if (clayCost > 0) {
                    ItemStack heldItem = player.getMainHandItem();
                    if (heldItem.getItem() == net.minecraft.world.item.Items.CLAY_BALL) {
                        heldItem.shrink(clayCost);
                        if (heldItem.isEmpty()) {
                            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        } else {
                            player.setItemInHand(InteractionHand.MAIN_HAND, heldItem);
                        }
                    }
                }

                // Add the output item to the player's inventory
                if (!stack.isEmpty()) {
                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }
                }

                // Sync inventory changes
                player.inventoryMenu.broadcastChanges();
            }
        });
        context.setPacketHandled(true);
    }
} 