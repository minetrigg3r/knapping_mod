package net.Minetrigg3r.BaseMod.world.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.systems.RenderSystem;

import net.Minetrigg3r.BaseMod.BaseMod;
import net.Minetrigg3r.BaseMod.network.KnappingButtonMessage;
import net.Minetrigg3r.BaseMod.network.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Generic screen for knapping interfaces.
 */
public class KnappingScreen extends AbstractContainerScreen<KnappingMenu> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KnappingScreen.class);
    
    // Static resource locations for different knapping types
    private static final Map<String, ResourceLocation> TEXTURES = new HashMap<>();
    private static final Map<String, ResourceLocation> BUTTON_TEXTURES = new HashMap<>();
    
    static {
        // Register textures for different knapping types
        TEXTURES.put("leather", new ResourceLocation(BaseMod.MOD_ID, "textures/screens/leather_knapping.png"));
        TEXTURES.put("clay", new ResourceLocation(BaseMod.MOD_ID, "textures/screens/clay_knapping.png"));
        
        // Register button textures for different knapping types
        BUTTON_TEXTURES.put("leather", new ResourceLocation(BaseMod.MOD_ID, "textures/screens/leather_button.png"));
        BUTTON_TEXTURES.put("clay", new ResourceLocation(BaseMod.MOD_ID, "textures/screens/clay_button.png"));
    }
    
    private final ResourceLocation texture;
    private final ResourceLocation buttonTexture;
    private final List<ImageButton> buttons = new ArrayList<>();
    private final Set<Integer> pressedButtons = new HashSet<>();
    private boolean rightMouseDown = false;
    private boolean leftMouseDown = false;
    
    /**
     * Creates a new knapping screen.
     */
    public KnappingScreen(KnappingMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        
        // Set texture based on knapping type
        String knappingType = container.getKnappingType();
        this.texture = TEXTURES.getOrDefault(knappingType, TEXTURES.get("leather"));
        this.buttonTexture = BUTTON_TEXTURES.getOrDefault(knappingType, BUTTON_TEXTURES.get("leather"));
        
        // Set dimensions
        this.imageWidth = 174;
        this.imageHeight = 191;
        this.inventoryLabelY = this.imageHeight - 94;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Clear existing buttons
        buttons.clear();
        this.clearWidgets();
        
        // Create 5x5 grid of knapping buttons
        int rows = 5;
        int cols = 5;
        int buttonSize = 16;
        int startX = this.leftPos + 15;
        int startY = this.topPos + 14;
        int spacing = 16;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int buttonNumber = row * cols + col + 1;
                int posX = startX + (col * spacing);
                int posY = startY + (row * spacing);
                
                // Only create button if it hasn't been pressed
                if (!pressedButtons.contains(buttonNumber)) {
                    ImageButton button = new ImageButton(posX, posY, buttonSize, buttonSize, 0, 0, 16,
                            buttonTexture, 16, 16, e -> onButtonClick(buttonNumber));
                    button.setAlpha(1.0F);
                    buttons.add(button);
                    this.addRenderableWidget(button);
                }
            }
        }
    }
    
    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) { // ESC key to close
            this.minecraft.player.closeContainer();
            pressedButtons.clear();
            return true;
        }
        return super.keyPressed(key, b, c);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        
        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            rightMouseDown = true;
            handleDragClick(mouseX, mouseY);
        } else if (button == 0) {
            leftMouseDown = true;
            handleDragClick(mouseX, mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 1) {
            rightMouseDown = false;
        } else if (button == 0) {
            leftMouseDown = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if ((rightMouseDown && button == 1) || (leftMouseDown && button == 0)) {
            handleDragClick(mouseX, mouseY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void handleDragClick(double mouseX, double mouseY) {
        int rows = 5;
        int cols = 5;
        int buttonSize = 16;
        int startX = this.leftPos + 15;
        int startY = this.topPos + 14;
        int spacing = 16;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int buttonNumber = row * cols + col + 1;
                if (!pressedButtons.contains(buttonNumber)) {
                    int posX = startX + (col * spacing);
                    int posY = startY + (row * spacing);
                    if (mouseX >= posX && mouseX < posX + buttonSize && mouseY >= posY && mouseY < posY + buttonSize) {
                        onButtonClick(buttonNumber);
                        return;
                    }
                }
            }
        }
    }
    
    private void onButtonClick(int buttonNumber) {
        if (!pressedButtons.contains(buttonNumber)) {
            pressedButtons.add(buttonNumber);
            // Remove the clicked button from the screen
            this.clearWidgets();
            init(); // Reinitialize the screen to update the grid
            
            // Send message to server with all pressed buttons
            NetworkHandler.sendToServer(new KnappingButtonMessage(new ArrayList<>(pressedButtons)));
            
            // Update container
            menu.setPressedButtons(new ArrayList<>(pressedButtons));
        }
    }
} 
 
 
 