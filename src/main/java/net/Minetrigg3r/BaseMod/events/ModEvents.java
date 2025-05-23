package net.Minetrigg3r.BaseMod.events;

import net.Minetrigg3r.BaseMod.BaseMod;
import net.Minetrigg3r.BaseMod.init.ModMenuTypes;
import net.Minetrigg3r.BaseMod.world.inventory.KnappingMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = BaseMod.MOD_ID)
public class ModEvents {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModEvents.class);

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        ItemStack offhandItem = player.getOffhandItem();
        
        // Handle leather knapping
        if (stack.getItem() == Items.LEATHER) {
            handleLeatherKnapping(player, stack, offhandItem, event);
        }
        // Handle clay knapping
        else if (stack.getItem() == Items.CLAY_BALL) {
            handleClayKnapping(player, stack, offhandItem, event);
        }
    }
    
    /**
     * Handles leather knapping.
     * Requires a cutting tool (sword or flint) in the off-hand.
     */
    private static void handleLeatherKnapping(Player player, ItemStack stack, ItemStack offhandItem, PlayerInteractEvent.RightClickItem event) {
        // Check if player has a sword or flint in their off-hand
        boolean hasCuttingTool = offhandItem.getItem() instanceof SwordItem || offhandItem.getItem() == Items.FLINT;
        
        if (!hasCuttingTool) {
            if (!player.level().isClientSide) {
                player.displayClientMessage(Component.translatable("message.basemod.need_cutting_tool"), true);
            }
            event.setCanceled(true);
            return;
        }
        
        event.setCanceled(true);
        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Open leather knapping menu
            openKnappingMenu(
                serverPlayer,
                "cutting",
                Items.LEATHER,
                "leather",
                "net.Minetrigg3r.BaseMod.recipe.LeatherKnappingRecipe"
            );
        }
    }
    
    /**
     * Handles clay knapping.
     * No tool required (can be shaped by hand).
     */
    private static void handleClayKnapping(Player player, ItemStack stack, ItemStack offhandItem, PlayerInteractEvent.RightClickItem event) {
        event.setCanceled(true);
        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Open clay knapping menu
            openKnappingMenu(
                serverPlayer,
                "hand",
                Items.CLAY_BALL,
                "clay",
                "net.Minetrigg3r.BaseMod.recipe.ClayKnappingRecipe"
            );
        }
    }
    
    /**
     * Opens a knapping menu for the given player.
     */
    private static void openKnappingMenu(ServerPlayer player, String toolType, 
                                         net.minecraft.world.item.Item materialItem,
                                         String knappingType, String recipesClass) {
        MenuProvider menuProvider = new SimpleMenuProvider(
            (id, inventory, playerIn) -> new KnappingMenu(
                id, inventory, player.blockPosition(), 
                toolType, materialItem, knappingType, recipesClass,
                ModMenuTypes.KNAPPING.get()
            ),
            KnappingMenu.createTitle(knappingType)
        );
        
        NetworkHooks.openScreen(player, menuProvider, buf -> {
            buf.writeBlockPos(player.blockPosition());
            buf.writeUtf(toolType);
            buf.writeInt(net.minecraft.world.item.Item.getId(materialItem));
            buf.writeUtf(knappingType);
            buf.writeUtf(recipesClass);
        });
    }
} 