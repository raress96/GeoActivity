package com.entirecraft.general.blocks.Machines.ACR;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class ACRGUI extends GuiContainer
{
	private static final ResourceLocation guitexture = new ResourceLocation("GeoActivity:textures/gui/ACR.png");
	private ACRTileE furnaceInventory;

	public ACRGUI(InventoryPlayer playerinv, ACRTileE tile)
	{
		super(new ACRContainer(tile, playerinv));
		furnaceInventory = tile;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		this.fontRendererObj.drawString("Advanced Coal Refiner", 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		mc.renderEngine.bindTexture(guitexture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		int var7;

		if (furnaceInventory.isBurning())
		{
			var7 = furnaceInventory.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(x + 61, y + 46 - var7, 176, 12 - var7, 14, var7 + 2);
		}

		var7 = furnaceInventory.getCookProgressScaled(22);
		this.drawTexturedModalRect(x + 89 , y + 33, 176, 14, var7-6, 16);
		int var8 = furnaceInventory.getCookProgressScaled(38);
		if(furnaceInventory.isstackone())
			this.drawTexturedModalRect(x + 87 , y + 32, 254, 246, 2, var8 - 2);
		if(furnaceInventory.isstacktwo())
			this.drawTexturedModalRect(x + 87 , y + 51 - var8, 198, 12 - var8, 2, var8);
	}
}
