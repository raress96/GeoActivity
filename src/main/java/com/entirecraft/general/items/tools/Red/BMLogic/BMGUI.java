package com.entirecraft.general.items.tools.Red.BMLogic;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class BMGUI extends GuiContainer
{
	private static final ResourceLocation guitexture = new ResourceLocation("GeoActivity:textures/gui/REDBM.png");

	public BMGUI(BMInventory inv, EntityPlayer player)
	{
		super(new BMContainer(inv, player));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(guitexture);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
	}
}
