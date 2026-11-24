package geoactivity.client.screen;

import geoactivity.machine.energy.ToolChargerBlockEntity;
import geoactivity.machine.energy.ToolChargerMenu;
import geoactivity.util.EnergyFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ToolChargerScreen extends MachineScreen<ToolChargerMenu> {
    private static final int BAR_HEIGHT = 67;

    public ToolChargerScreen(ToolChargerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "tool_charger");
    }

    @Override
    protected void drawBars(GuiGraphicsExtractor graphics) {
        int height = (int) Math.min(BAR_HEIGHT, (long) menu.energy() * BAR_HEIGHT / ToolChargerBlockEntity.CAPACITY);
        sprite(graphics, 8, 75 - height, 176, BAR_HEIGHT - height, 16, height);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, Component.translatable("gui.geoactivity.energy"), 100, 10, -1, true);
        graphics.text(font, EnergyFormat.units(menu.energy()) + "/" + EnergyFormat.units(ToolChargerBlockEntity.CAPACITY) + " FE",
                100, 20, -1, true);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        int x = mouseX - leftPos;
        int y = mouseY - topPos;
        if (x >= 8 && x <= 25 && y >= 8 && y <= 76) {
            graphics.setTooltipForNextFrame(font, Component.literal(menu.energy() + "/" + ToolChargerBlockEntity.CAPACITY + " FE")
                    .withStyle(ChatFormatting.AQUA), mouseX, mouseY);
        }
    }
}
