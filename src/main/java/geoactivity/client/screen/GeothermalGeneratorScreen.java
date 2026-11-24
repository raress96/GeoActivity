package geoactivity.client.screen;

import geoactivity.GeoActivity;
import geoactivity.machine.energy.GeothermalGeneratorBlockEntity;
import geoactivity.machine.energy.GeothermalGeneratorMenu;
import geoactivity.util.EnergyFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class GeothermalGeneratorScreen extends AbstractContainerScreen<GeothermalGeneratorMenu> {
    private static final Identifier TEXTURE = GeoActivity.id("textures/gui/container/geothermal_generator.png");
    private static final int TEXT = 0x404040 | 0xFF000000;

    public GeothermalGeneratorScreen(GeothermalGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 34, 5, TEXT, false);
        graphics.text(font, Component.translatable("gui.geoactivity.lava_blocks", menu.lavaBlocks()), 25, 20, TEXT, false);
        graphics.text(font, Component.translatable("gui.geoactivity.efficiency", menu.efficiency()), 25, 30, TEXT, false);
        graphics.text(font, Component.translatable("gui.geoactivity.fe_per_tick", menu.perTick()), 25, 40, TEXT, false);
        graphics.text(font, Component.translatable("gui.geoactivity.energy"), 25, 55, TEXT, false);
        graphics.text(font, EnergyFormat.units(menu.energy()) + "/" + EnergyFormat.units(GeothermalGeneratorBlockEntity.CAPACITY) + " FE",
                25, 65, TEXT, false);
        graphics.text(font, Component.translatable("gui.geoactivity.update_in"), 114, 40, TEXT, false);
        graphics.text(font, menu.secondsToScan() + " s", 124, 50, TEXT, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT, false);
    }
}
