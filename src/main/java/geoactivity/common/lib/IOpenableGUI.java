package geoactivity.common.lib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IOpenableGUI
{
	Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z);
	Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z);
}