package geoactivity.client.screen;

import geoactivity.GeoActivity;
import geoactivity.machine.MachineMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** Draws the 1.10.2 GUI texture (256×256 sheet, 176×166 panel) plus machine-specific bars. */
public abstract class MachineScreen<M extends MachineMenu> extends AbstractContainerScreen<M> {
    private final Identifier texture;

    protected MachineScreen(M menu, Inventory inventory, Component title, String textureName) {
        this(menu, inventory, title, textureName, DEFAULT_IMAGE_HEIGHT);
    }

    protected MachineScreen(M menu, Inventory inventory, Component title, String textureName, int height) {
        super(menu, inventory, title, DEFAULT_IMAGE_WIDTH, height);
        this.texture = GeoActivity.id("textures/gui/container/" + textureName + ".png");
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
        drawBars(graphics);
    }

    /** Draws progress/fuel bars on top of the background. */
    protected void drawBars(GuiGraphicsExtractor graphics) {}

    /** Copies a {@code width × height} region from (u, v) of the GUI texture to (x, y) relative to the panel. */
    protected void sprite(GuiGraphicsExtractor graphics, int x, int y, int u, int v, int width, int height) {
        if (width > 0 && height > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos + x, topPos + y, u, v, width, height, 256, 256);
        }
    }
}
