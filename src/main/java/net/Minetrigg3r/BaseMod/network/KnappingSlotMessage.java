package net.Minetrigg3r.BaseMod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * Network message sent when a player takes an item from a knapping interface.
 */
public class KnappingSlotMessage {
    private final int slotId;
    private final ItemStack itemStack;
    private final int materialCost;
    private final String toolType;
    private final Item materialItem;
    
    /**
     * Creates a new knapping slot message.
     */
    public KnappingSlotMessage(int slotId, ItemStack itemStack, int materialCost, String toolType, Item materialItem) {
        this.slotId = slotId;
        this.itemStack = itemStack.copy();
        this.materialCost = materialCost;
        this.toolType = toolType;
        this.materialItem = materialItem;
    }
    
    /**
     * Reads a knapping slot message from a buffer.
     */
    public KnappingSlotMessage(FriendlyByteBuf buffer) {
        this.slotId = buffer.readInt();
        this.itemStack = buffer.readItem();
        this.materialCost = buffer.readInt();
        this.toolType = buffer.readUtf();
        this.materialItem = Item.byId(buffer.readInt());
    }
    
    /**
     * Writes this message to a buffer.
     */
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(slotId);
        buffer.writeItem(itemStack);
        buffer.writeInt(materialCost);
        buffer.writeUtf(toolType);
        buffer.writeInt(Item.getId(materialItem));
    }
    
    /**
     * Handles this message on the server.
     */
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            
            // Only process if we're actually taking an item
            if (itemStack.isEmpty()) {
                player.closeContainer();
                return;
            }
            
            // Consume material from player's hand
            ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (heldItem.getItem() == materialItem && heldItem.getCount() >= materialCost) {
                heldItem.shrink(materialCost);
                if (heldItem.isEmpty()) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
            }
            
            // Handle tool usage based on tool type
            ItemStack offhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
            
            // Check what kind of tool is required
            if (toolType.equals("cutting")) {
                // For leather - handle knife-like tools or flint
                boolean isCuttingTool = offhandItem.getItem() instanceof SwordItem || offhandItem.getItem() == Items.FLINT;
                if (isCuttingTool) {
                    // Damage the tool
                    offhandItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.OFF_HAND));
                } else {
                    // Wrong tool type
                    player.displayClientMessage(Component.translatable("message.basemod.need_cutting_tool"), true);
                    return;
                }
            } else if (toolType.equals("hand")) {
                // Clay knapping doesn't require a tool
            }
            
            // Add the crafted item to player's inventory
            if (!player.getInventory().add(itemStack)) {
                player.drop(itemStack, false);
            }
            
            // Close the container
            player.closeContainer();
        });
        ctx.get().setPacketHandled(true);
    }
} 
 
 
 