package com.entirecraft.general.blocks.Machines.CrM;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class CrMGUI extends GuiContainer
{
	private static final ResourceLocation guitexture = new ResourceLocation("GeoActivity:textures/gui/CrM.png");
	private CMTileE furnaceInventory;

	public CrMGUI(InventoryPlayer playerInv, CMTileE tile)
	{
		super(new CrMContainer(tile, playerInv));
		furnaceInventory = tile;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j)
	{
		this.fontRendererObj.drawString("Crafting Machine", 78, 5, 0x404040);
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
			this.drawTexturedModalRect(x + 66, y + 75 - var7, 176, 12 - var7, 14, var7 + 2);
		}

		var7 = furnaceInventory.getCookProgressScaled(40);
		this.drawTexturedModalRect(x + 62 , y + 42, 176, 14, var7, 16);
	}
}
