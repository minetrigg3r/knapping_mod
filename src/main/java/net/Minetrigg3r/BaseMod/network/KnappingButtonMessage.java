package net.Minetrigg3r.BaseMod.network;

import net.Minetrigg3r.BaseMod.world.inventory.KnappingMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Network message sent when a player clicks a button in a knapping interface.
 */
public class KnappingButtonMessage {
    private final List<Integer> pressedButtons;
    
    /**
     * Creates a new knapping button message.
     */
    public KnappingButtonMessage(List<Integer> pressedButtons) {
        this.pressedButtons = pressedButtons;
    }
    
    /**
     * Reads a knapping button message from a buffer.
     */
    public KnappingButtonMessage(FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        this.pressedButtons = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.pressedButtons.add(buffer.readVarInt());
        }
    }
    
    /**
     * Writes this message to a buffer.
     */
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeVarInt(pressedButtons.size());
        for (Integer button : pressedButtons) {
            buffer.writeVarInt(button);
        }
    }
    
    /**
     * Handles this message on the server.
     */
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                if (context.getSender().containerMenu instanceof KnappingMenu) {
                    KnappingMenu menu = (KnappingMenu) context.getSender().containerMenu;
                    menu.setPressedButtons(pressedButtons);
                }
            }
        });
        context.setPacketHandled(true);
    }
} 
 
 
 