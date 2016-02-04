package com.entirecraft.general.items.tools.Red.ABMLogic;

import java.util.ArrayList;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.entirecraft.general.blocks.Machines.TC.TCTileE;
import com.entirecraft.util.BaseElectricTool;
import com.entirecraft.util.GeneralHelper.GuiColors;

public class ABMGUI extends GuiContainer
{
	private static final ResourceLocation guitexture = new ResourceLocation("GeoActivity:textures/gui/AREDBM.png");

	EntityPlayer player;

	public ABMGUI(ABMInventory inv, EntityPlayer player)
	{
		super(new ABMContainer(inv, player));

		this.player = player;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(guitexture);

		ItemStack item = player.inventory.getCurrentItem();
		short energy = (short) (((BaseElectricTool) item.getItem()).getEnergyStored(item) / 5000 / 14);

		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		this.drawTexturedModalRect(guiLeft + 80, guiTop + 75 - energy, 176, 67 - energy, 16, energy);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
		if (((y - guiTop) >= 8) && ((y - guiTop) <= 76))
			if (((x - guiLeft) >= 80) && ((x - guiLeft) <= 97))
			{
				ItemStack item = player.inventory.getCurrentItem();
				ArrayList<String> lines = new ArrayList<String>();
				
				lines.add(GuiColors.LIGHTBLUE + "" + ((BaseElectricTool) item.getItem()).getEnergyStored(item) + "/" + "5000000 RF");
				
				drawHoveringText(lines, x - guiLeft, y - guiTop, fontRendererObj);
			}
	}
}
