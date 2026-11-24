package geoactivity.client.screen;

import geoactivity.machine.orerefiner.AdvancedOreRefinerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AdvancedOreRefinerScreen extends MachineScreen<AdvancedOreRefinerMenu> {
    public AdvancedOreRefinerScreen(AdvancedOreRefinerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "advanced_ore_refiner", AdvancedOreRefinerMenu.HEIGHT);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // the 1.10.2 texture has no room for labels
    }

    @Override
    protected void drawBars(GuiGraphicsExtractor graphics) {
        int arrow = menu.cookProgress(20);
        if (menu.laneWorking(0)) sprite(graphics, 59, 22, 176, 0, arrow + 1, 9);
        if (menu.laneWorking(2)) sprite(graphics, 59, 62, 176, 0, arrow + 1, 9);
        if (menu.laneWorking(1)) sprite(graphics, 122 - arrow, 22, 188 - arrow, 9, arrow, 9);
        if (menu.laneWorking(3)) sprite(graphics, 122 - arrow, 62, 188 - arrow, 9, arrow, 9);
        int heat = menu.heat(110);
        sprite(graphics, 154, 116 - heat, 189, 107 - heat, 14, heat + 2);
        sprite(graphics, 29, 105, 176, 109, menu.special() * 4, 4);
    }
}
