package geoactivity.client.screen;

import geoactivity.machine.crafting.CraftingMachineMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CraftingMachineScreen extends MachineScreen<CraftingMachineMenu> {
    public CraftingMachineScreen(CraftingMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "crafting_machine");
    }

    @Override
    protected void init() {
        super.init();
        // 1.10.2 drew the title right of the grid
        titleLabelX = 78;
        titleLabelY = 5;
    }

    @Override
    protected void drawBars(GuiGraphicsExtractor graphics) {
        if (menu.isBurning()) {
            int flame = menu.burnLeft(12);
            sprite(graphics, 66, 75 - flame, 176, 12 - flame, 14, flame + 2);
        }
        sprite(graphics, 62, 42, 176, 14, menu.craftProgress(40), 16);
    }

    @Override
    protected boolean showInventoryLabel() {
        return false;
    }
}
