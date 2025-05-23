package net.Minetrigg3r.BaseMod.item;

import net.Minetrigg3r.BaseMod.BaseMod;
import net.Minetrigg3r.BaseMod.block.blocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BaseMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> BASE_TAB = CREATIVE_MODE_TABS.register("base_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(items.UNFIRED_CLAY_POT.get()))
                    .title(Component.translatable("creativetab.base_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(items.UNFIRED_CLAY_BRICK.get());
                        output.accept(items.UNFIRED_CLAY_POT.get());
                        output.accept(blocks.UNFIRED_LARGE_CLAY_POT.get());
                        output.accept(blocks.POOPBLOCK.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);

    }
}
