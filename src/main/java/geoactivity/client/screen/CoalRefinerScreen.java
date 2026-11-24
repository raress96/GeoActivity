package geoactivity.client.screen;

import geoactivity.machine.refiner.CoalRefinerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CoalRefinerScreen extends MachineScreen<CoalRefinerMenu> {
    public CoalRefinerScreen(CoalRefinerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "coal_refiner");
    }

    @Override
    protected void drawBars(GuiGraphicsExtractor graphics) {
        if (menu.isBurning()) {
            int flame = menu.burnLeft(12);
            sprite(graphics, 49, 36 + 12 - flame, 176, 12 - flame, 14, flame + 2);
        }
        sprite(graphics, 89, 34, 176, 14, menu.cookProgress(24) + 1, 16);
    }
}
