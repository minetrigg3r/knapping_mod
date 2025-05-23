package net.Minetrigg3r.BaseMod.network;

import net.Minetrigg3r.BaseMod.BaseMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    public static void init() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(BaseMod.MOD_ID, "main"),
                () -> "1.0",
                s -> true,
                s -> true
        );

        INSTANCE.registerMessage(packetId++, TestMenuSlotMessage.class,
                TestMenuSlotMessage::toBytes,
                TestMenuSlotMessage::new,
                TestMenuSlotMessage::handle);
                
        INSTANCE.registerMessage(packetId++, LeatherKnappingSlotMessage.class,
                LeatherKnappingSlotMessage::toBytes,
                LeatherKnappingSlotMessage::new,
                LeatherKnappingSlotMessage::handle);
                
        // Register the new knapping messages
        INSTANCE.registerMessage(packetId++, KnappingButtonMessage.class,
                KnappingButtonMessage::toBytes,
                KnappingButtonMessage::new,
                KnappingButtonMessage::handle);
                
        INSTANCE.registerMessage(packetId++, KnappingSlotMessage.class,
                KnappingSlotMessage::toBytes,
                KnappingSlotMessage::new,
                KnappingSlotMessage::handle);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
} 