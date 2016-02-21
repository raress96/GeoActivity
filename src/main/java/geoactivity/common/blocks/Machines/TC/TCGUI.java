package geoactivity.common.blocks.Machines.TC;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import geoactivity.common.util.GeneralHelper;
import geoactivity.common.util.GeneralHelper.GuiColors;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class TCGUI extends GuiContainer
{
	private static ResourceLocation guitexture = new ResourceLocation("GeoActivity:textures/gui/TC.png");

	private TCTileE tile;

	public TCGUI(InventoryPlayer playerInv, TCTileE tile)
	{
		super(new TCContainer(tile, playerInv));

		this.tile = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int x, int y)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(guitexture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		int var8 = tile.getEnergyScaled(14);
		drawTexturedModalRect(guiLeft + 8, guiTop + 75 - var8, 176, 67 - var8, 16, var8);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
		drawString(fontRendererObj, "Energy: ", 100, 10, -1);
		drawString(fontRendererObj, GeneralHelper.getEnergyUnits(tile.buffer.getEnergyStored()) + "/"
				+ GeneralHelper.getEnergyUnits(TCTileE.internalStorage) + " RF", 100, 20, -1);

		if (((y - guiTop) >= 8) && ((y - guiTop) <= 76))
			if (((x - guiLeft) >= 8) && ((x - guiLeft) <= 25))
			{
				ArrayList<String> lines = new ArrayList<String>();

				lines.add(GuiColors.LIGHTBLUE + "" + tile.buffer.getEnergyStored() + "/" + TCTileE.internalStorage + " RF");

				drawHoveringText(lines, x - guiLeft, y - guiTop, fontRendererObj);
			}
	}
}
