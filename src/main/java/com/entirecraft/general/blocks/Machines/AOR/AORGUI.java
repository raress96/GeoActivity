package com.entirecraft.general.blocks.Machines.AOR;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class AORGUI extends GuiContainer
{
	private static final ResourceLocation guitexture = new ResourceLocation("GeoActivity:textures/gui/AOR.png");
	private AORTileE inv;

	public AORGUI(InventoryPlayer playerinv, AORTileE tile)
	{
		super(new AORContainer(tile, playerinv));
		inv = tile;
		this.ySize+=40;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		mc.renderEngine.bindTexture(guitexture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		boolean[] stacks = inv.hasStacks();
		
		int var = inv.getCookProgressScaled(20);
		
		if(stacks[0])
			this.drawTexturedModalRect(x + 59, y + 22, 176, 0, var+1, 9);
		if(stacks[2])
			this.drawTexturedModalRect(x + 59, y + 62, 176, 0, var+1, 9);
		
		if(stacks[1])
			this.drawTexturedModalRect(x + 122-var, y + 22, 188-var, 9, var, 9);
		if(stacks[3])
			this.drawTexturedModalRect(x + 122-var, y + 62, 188-var, 9, var, 9);
		
		int var2 = inv.getBurnTimeRemainingScaled(110);
		this.drawTexturedModalRect(x + 154, y + 116 - var2, 189, 107 - var2, 14, var2 + 2);
		
		byte var3 = inv.furnaceSpecial;
		this.drawTexturedModalRect(x + 29, y + 105, 176, 109, var3*4, 4);
	}
}
