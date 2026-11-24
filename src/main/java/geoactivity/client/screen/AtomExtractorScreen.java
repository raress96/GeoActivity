package geoactivity.client.screen;

import geoactivity.machine.extractor.AtomExtractorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AtomExtractorScreen extends MachineScreen<AtomExtractorMenu> {
    public AtomExtractorScreen(AtomExtractorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "atom_extractor");
    }

    @Override
    protected void drawBars(GuiGraphicsExtractor graphics) {
        sprite(graphics, 42, 30, 176, 14, menu.progress(35) + 1, 16);
    }

    @Override
    protected boolean showInventoryLabel() {
        return false;
    }
}
