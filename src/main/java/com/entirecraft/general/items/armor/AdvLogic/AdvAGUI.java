package com.entirecraft.general.items.armor.AdvLogic;

import net.minecraft.entity.player.EntityPlayer;

import com.entirecraft.general.items.tools.Adv.Logic.AdvTGUI;

public class AdvAGUI extends AdvTGUI
{
	public AdvAGUI(AdvAInventory inv, EntityPlayer player)
	{
		super(inv, player);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		this.fontRendererObj.drawString("Advanced Armor", 53, 8, 4210752);
		this.fontRendererObj.drawString("Inventory", 8, ySize - 94, 4210752);
	}
}
