package geoactivity.client.screen;

import geoactivity.machine.refiner.AdvancedCoalRefinerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AdvancedCoalRefinerScreen extends MachineScreen<AdvancedCoalRefinerMenu> {
    public AdvancedCoalRefinerScreen(AdvancedCoalRefinerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "advanced_coal_refiner");
    }

    /** 1.10.2 drew only the machine name, where the inventory label usually goes (the texture has perks top-left). */
    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 8, imageHeight - 94, -12566464, false);
    }

    @Override
    protected void drawBars(GuiGraphicsExtractor graphics) {
        if (menu.isBurning()) {
            int flame = menu.burnLeft(12);
            sprite(graphics, 61, 46 - flame, 176, 12 - flame, 14, flame + 2);
        }
        sprite(graphics, 89, 33, 176, 14, menu.cookProgress(22) - 6, 16);
        int pipe = menu.cookProgress(38);
        if (menu.hasInput(0)) {
            sprite(graphics, 87, 32, 254, 246, 2, pipe - 2);
        }
        if (menu.hasInput(1)) {
            sprite(graphics, 87, 51 - pipe, 198, 12 - pipe, 2, pipe);
        }
    }
}
