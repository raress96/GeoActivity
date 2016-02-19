package geoactivity.common.lib;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

public interface IOpenableGUI
{
	GuiContainer getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z);
	Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z);
}