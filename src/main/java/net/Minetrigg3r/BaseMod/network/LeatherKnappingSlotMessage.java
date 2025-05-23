package net.Minetrigg3r.BaseMod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LeatherKnappingSlotMessage {
    private final int slotId;
    private final ItemStack itemStack;
    private final int leatherCost;
    private final boolean usingFlint;
    private final boolean usingSword;

    public LeatherKnappingSlotMessage(int slotId, ItemStack itemStack, int leatherCost, boolean usingFlint, boolean usingSword) {
        this.slotId = slotId;
        this.itemStack = itemStack.copy();
        this.leatherCost = leatherCost;
        this.usingFlint = usingFlint;
        this.usingSword = usingSword;
    }

    public LeatherKnappingSlotMessage(FriendlyByteBuf buffer) {
        this.slotId = buffer.readInt();
        this.itemStack = buffer.readItem();
        this.leatherCost = buffer.readInt();
        this.usingFlint = buffer.readBoolean();
        this.usingSword = buffer.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(slotId);
        buffer.writeItem(itemStack);
        buffer.writeInt(leatherCost);
        buffer.writeBoolean(usingFlint);
        buffer.writeBoolean(usingSword);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                // Only process tool usage and leather consumption if we're actually taking an item
                if (!itemStack.isEmpty()) {
                    // Consume leather from player's hand
                    ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                    if (heldItem.getItem() == Items.LEATHER && heldItem.getCount() >= leatherCost) {
                        heldItem.shrink(leatherCost);
                        if (heldItem.isEmpty()) {
                            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        }
                    }
                    
                    // Handle offhand tool usage
                    ItemStack offhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
                    
                    // Consume flint if using it 
                    if (usingFlint && offhandItem.getItem() == Items.FLINT) {
                        offhandItem.shrink(1);
                        if (offhandItem.isEmpty()) {
                            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                        }
                    }
                    
                    // Damage sword if using it
                    if (usingSword && offhandItem.getItem() instanceof SwordItem) {
                        offhandItem.hurtAndBreak(1, player, (p) -> {
                            p.broadcastBreakEvent(InteractionHand.OFF_HAND);
                        });
                    }
                    
                    // Add the crafted item to player's inventory if not already added
                    if (!player.getInventory().add(itemStack)) {
                        player.drop(itemStack, false);
                    }
                }
                
                // Close the container
                player.closeContainer();
            }
        });
        ctx.get().setPacketHandled(true);
    }
} 
 
 
 