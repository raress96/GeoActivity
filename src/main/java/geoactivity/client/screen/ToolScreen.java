package geoactivity.client.screen;

import geoactivity.GeoActivity;
import geoactivity.tool.PerkTool;
import geoactivity.tool.ToolMenu;
import geoactivity.tool.ToolProfiles;
import geoactivity.util.EnergyFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** GUI of a held tool: the 1.10.2 texture for that tool; the RF miner also shows its charge. */
public class ToolScreen extends AbstractContainerScreen<ToolMenu> {
    private final Identifier texture;

    public ToolScreen(ToolMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.texture = GeoActivity.id("textures/gui/container/" + menu.profile().texture() + ".png");
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, -12566464, false);
        if (menu.tool().getItem() instanceof PerkTool tool && tool.isEnergyTool()) {
            graphics.text(font, EnergyFormat.units(PerkTool.energy(menu.tool())) + "/" + EnergyFormat.units(ToolProfiles.RF_CAPACITY) + " FE",
                    62, 40, -12566464, false);
        }
    }
}
