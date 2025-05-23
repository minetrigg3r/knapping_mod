package net.Minetrigg3r.BaseMod.item;

import net.Minetrigg3r.BaseMod.BaseMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers all mod items.
 */
public class items {
    // Register items under BaseMod namespace
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BaseMod.MOD_ID);

    // Clay knapping items
    public static final RegistryObject<Item> UNFIRED_CLAY_BRICK = ITEMS.register("unfired_clay_brick",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> UNFIRED_CLAY_POT = ITEMS.register("unfired_clay_pot",
            () -> new Item(new Item.Properties()));

    // Decorative items
    public static final RegistryObject<Item> DECORATIVE_POT = ITEMS.register("decorative_pot",
            () -> new Item(new Item.Properties()));

    // Cut Leather item
    public static final RegistryObject<Item> CUT_LEATHER = ITEMS.register("cut_leather",
            () -> new Item(new Item.Properties()));

    // Register the items
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}