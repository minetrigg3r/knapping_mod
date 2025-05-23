package net.Minetrigg3r.BaseMod;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.Minetrigg3r.BaseMod.block.blocks;
import net.Minetrigg3r.BaseMod.item.items;
import net.Minetrigg3r.BaseMod.network.NetworkHandler;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipe;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipeManager;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipeSerializer;
import net.Minetrigg3r.BaseMod.recipe.KnappingRecipeType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.Minetrigg3r.BaseMod.init.ModMenuTypes;
import net.Minetrigg3r.BaseMod.world.inventory.KnappingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.client.event.ScreenEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BaseMod.MOD_ID)
public class BaseMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "basemod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = 
        DeferredRegister.create(ForgeRegistries.Keys.RECIPE_SERIALIZERS, MOD_ID);

    public static final RegistryObject<RecipeSerializer<KnappingRecipe>> KNAPPING_SERIALIZER = 
        RECIPE_SERIALIZERS.register("knapping", KnappingRecipeSerializer::new);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = 
        DeferredRegister.create(ForgeRegistries.Keys.RECIPE_TYPES, MOD_ID);

    public static final RegistryObject<RecipeType<KnappingRecipe>> KNAPPING_TYPE = 
        RECIPE_TYPES.register("knapping", () -> KnappingRecipeType.INSTANCE);

    public BaseMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register items and blocks
        items.register(modEventBus);
        blocks.register(modEventBus);

        // Register the recipe serializer and type
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);

        // Register setup event
        modEventBus.addListener(this::setup);

        // Register menu types
        ModMenuTypes.REGISTRY.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register event handlers
        modEventBus.addListener(this::addCreative);

        // Register JEI plugin on client side only
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::registerJeiPlugin);
        }
    }

    private void registerJeiPlugin(FMLClientSetupEvent event) {
        LOGGER.info("Registering JEI plugin");
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Initialize recipes
        KnappingRecipeManager.init();
        // Initialize network handler
        NetworkHandler.init();
        
        LOGGER.info("Initialized knapping recipe system");
    }

    // Add items to the creative tabs
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(blocks.UNFIRED_LARGE_CLAY_POT);
            event.accept(items.UNFIRED_CLAY_BRICK);
            event.accept(items.UNFIRED_CLAY_POT);
        }
    }

    // Client-side setup for mod events
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("Setting up client for knapping system");
            
            // Register screen factories
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenuTypes.KNAPPING.get(), KnappingScreen::new);
            });
        }
    }

    // Client-side setup for Forge events
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents
    {
        @SubscribeEvent
        public static void onScreenInit(ScreenEvent.Init.Post event) {
            Screen screen = event.getScreen();
            if (screen instanceof AbstractContainerScreen<?> containerScreen) {
                if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
                    // Handle inventory screens
                }
            }
        }
    }
}
