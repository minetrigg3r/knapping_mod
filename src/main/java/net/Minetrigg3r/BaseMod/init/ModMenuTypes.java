package net.Minetrigg3r.BaseMod.init;

import net.Minetrigg3r.BaseMod.BaseMod;
import net.Minetrigg3r.BaseMod.world.inventory.KnappingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry for menu types.
 */
public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BaseMod.MOD_ID);
    
    /**
     * Generic knapping menu type.
     */
    public static final RegistryObject<MenuType<KnappingMenu>> KNAPPING = REGISTRY.register(
        "knapping",
        () -> IForgeMenuType.create(KnappingMenu::new)
    );
} 
 
 
 