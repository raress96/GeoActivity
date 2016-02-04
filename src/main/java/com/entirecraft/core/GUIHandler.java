package com.entirecraft.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import com.entirecraft.general.blocks.Machines.ACR.ACRContainer;
import com.entirecraft.general.blocks.Machines.ACR.ACRGUI;
import com.entirecraft.general.blocks.Machines.ACR.ACRTileE;
import com.entirecraft.general.blocks.Machines.AE.AEContainer;
import com.entirecraft.general.blocks.Machines.AE.AEGUI;
import com.entirecraft.general.blocks.Machines.AE.AETileE;
import com.entirecraft.general.blocks.Machines.AOR.AORContainer;
import com.entirecraft.general.blocks.Machines.AOR.AORGUI;
import com.entirecraft.general.blocks.Machines.AOR.AORTileE;
import com.entirecraft.general.blocks.Machines.CR.CRContainer;
import com.entirecraft.general.blocks.Machines.CR.CRGUI;
import com.entirecraft.general.blocks.Machines.CR.CRTileE;
import com.entirecraft.general.blocks.Machines.ChM.ChMContainer;
import com.entirecraft.general.blocks.Machines.ChM.ChMGUI;
import com.entirecraft.general.blocks.Machines.ChM.ChMTileE;
import com.entirecraft.general.blocks.Machines.CrM.CMTileE;
import com.entirecraft.general.blocks.Machines.CrM.CrMContainer;
import com.entirecraft.general.blocks.Machines.CrM.CrMGUI;
import com.entirecraft.general.blocks.Machines.GG.GGContainer;
import com.entirecraft.general.blocks.Machines.GG.GGGUI;
import com.entirecraft.general.blocks.Machines.GG.GGTileE;
import com.entirecraft.general.blocks.Machines.TC.TCContainer;
import com.entirecraft.general.blocks.Machines.TC.TCGUI;
import com.entirecraft.general.blocks.Machines.TC.TCTileE;
import com.entirecraft.general.items.ASBLogic.ASBContainer;
import com.entirecraft.general.items.ASBLogic.ASBGUI;
import com.entirecraft.general.items.ASBLogic.ASBInventory;
import com.entirecraft.general.items.armor.AdvLogic.AdvAGUI;
import com.entirecraft.general.items.armor.AdvLogic.AdvAInventory;
import com.entirecraft.general.items.tools.Adv.Logic.AdvTContainer;
import com.entirecraft.general.items.tools.Adv.Logic.AdvTGUI;
import com.entirecraft.general.items.tools.Adv.Logic.AdvTInventory;
import com.entirecraft.general.items.tools.RMLogic.RMContainer;
import com.entirecraft.general.items.tools.RMLogic.RMGUI;
import com.entirecraft.general.items.tools.RMLogic.RMInventory;
import com.entirecraft.general.items.tools.Red.ABMLogic.ABMContainer;
import com.entirecraft.general.items.tools.Red.ABMLogic.ABMGUI;
import com.entirecraft.general.items.tools.Red.ABMLogic.ABMInventory;
import com.entirecraft.general.items.tools.Red.BMLogic.BMContainer;
import com.entirecraft.general.items.tools.Red.BMLogic.BMGUI;
import com.entirecraft.general.items.tools.Red.BMLogic.BMInventory;
import com.entirecraft.general.items.tools.Red.Logic.RedContainer;
import com.entirecraft.general.items.tools.Red.Logic.RedGUI;
import com.entirecraft.general.items.tools.Red.Logic.RedInventory;
import com.entirecraft.lib.GuiIDs;


public class GUIHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		
		if (!world.isBlockLoaded(pos))
			return null;

		TileEntity tile_entity = world.getTileEntity(pos);

		switch (id)
		{
			case GuiIDs.CR:
				if (!(tile_entity instanceof CRTileE))
					return null;
				return new CRContainer((CRTileE) tile_entity, player.inventory);
			case GuiIDs.RM:
			{
				RMInventory inv = new RMInventory(player.inventory.getCurrentItem(), player);
				return new RMContainer(inv, player);
			}
			case GuiIDs.CrM:
				if (!(tile_entity instanceof CMTileE))
					return null;
				return new CrMContainer((CMTileE) tile_entity, player.inventory);
			case GuiIDs.ADV:
			{
				AdvTInventory inv = new AdvTInventory(player.inventory.getCurrentItem(), player);
				return new AdvTContainer(inv, player);
			}
			case GuiIDs.ACR:
				if (!(tile_entity instanceof ACRTileE))
					return null;
				return new ACRContainer((ACRTileE) tile_entity, player.inventory);
			case GuiIDs.AE:
				if (!(tile_entity instanceof AETileE))
					return null;
				return new AEContainer((AETileE) tile_entity, player.inventory);
			case GuiIDs.ChM:
				if (!(tile_entity instanceof ChMTileE))
					return null;
				return new ChMContainer((ChMTileE) tile_entity, player.inventory);
			case GuiIDs.REDM:
			{
				RedInventory inv = new RedInventory(player.inventory.getCurrentItem(), player, 8);
				return new RedContainer(inv, player);
			}
			case GuiIDs.REDBM:
			{
				BMInventory inv = new BMInventory(player.inventory.getCurrentItem(), player);
				return new BMContainer(inv, player);
			}
			case GuiIDs.ASB:
			{
				ASBInventory inv = new ASBInventory(player.inventory.getCurrentItem(), player);
				return new ASBContainer(inv, player);
			}
			case GuiIDs.AOR:
				if (!(tile_entity instanceof AORTileE))
					return null;
				return new AORContainer((AORTileE) tile_entity, player.inventory);
			case GuiIDs.ADVArmor:
			{
				AdvAInventory inv = new AdvAInventory(player.inventory.getCurrentItem(), player);
				return new AdvTContainer(inv, player);
			}
			case GuiIDs.AREDBM:
			{
				ABMInventory inv = new ABMInventory(player.inventory.getCurrentItem(), player);
				return new ABMContainer(inv, player);
			}
			case GuiIDs.TC:
				if (!(tile_entity instanceof TCTileE))
					return null;
				return new TCContainer((TCTileE) tile_entity, player.inventory);
			case GuiIDs.GG:
				if (!(tile_entity instanceof GGTileE))
					return null;
				return new GGContainer((GGTileE) tile_entity, player.inventory);
			default:
				return null;
		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		
		if (!world.isBlockLoaded(pos))
			return null;

		TileEntity tile_entity = world.getTileEntity(pos);

		switch (id)
		{
			case GuiIDs.CR:
				if (!(tile_entity instanceof CRTileE))
					return null;
				return new CRGUI(player.inventory, (CRTileE) tile_entity);
			case GuiIDs.RM:
			{
				RMInventory inv = new RMInventory(player.inventory.getCurrentItem(), player);
				return new RMGUI(inv, player);
			}
			case GuiIDs.CrM:
				if (!(tile_entity instanceof CMTileE))
					return null;
				return new CrMGUI(player.inventory, (CMTileE) tile_entity);
			case GuiIDs.ADV:
			{
				AdvTInventory inv = new AdvTInventory(player.inventory.getCurrentItem(), player);
				return new AdvTGUI(inv, player);
			}
			case GuiIDs.ACR:
				if (!(tile_entity instanceof ACRTileE))
					return null;
				return new ACRGUI(player.inventory, (ACRTileE) tile_entity);
			case GuiIDs.AE:
				if (!(tile_entity instanceof AETileE))
					return null;
				return new AEGUI(player.inventory, (AETileE) tile_entity);
			case GuiIDs.ChM:
				if (!(tile_entity instanceof ChMTileE))
					return null;
				return new ChMGUI(player.inventory, (ChMTileE) tile_entity);
			case GuiIDs.REDM:
			{
				RedInventory inv = new RedInventory(player.inventory.getCurrentItem(), player, 8);
				return new RedGUI(inv, player);
			}
			case GuiIDs.REDBM:
			{
				BMInventory inv = new BMInventory(player.inventory.getCurrentItem(), player);
				return new BMGUI(inv, player);
			}
			case GuiIDs.ASB:
			{
				ASBInventory inv = new ASBInventory(player.inventory.getCurrentItem(), player);
				return new ASBGUI(inv, player);
			}
			case GuiIDs.AOR:
				if (!(tile_entity instanceof AORTileE))
					return null;
				return new AORGUI(player.inventory, (AORTileE) tile_entity);
			case GuiIDs.ADVArmor:
			{
				AdvAInventory inv = new AdvAInventory(player.inventory.getCurrentItem(), player);
				return new AdvAGUI(inv, player);
			}
			case GuiIDs.AREDBM:
			{
				ABMInventory inv = new ABMInventory(player.inventory.getCurrentItem(), player);
				return new ABMGUI(inv, player);
			}
			case GuiIDs.TC:
				if (!(tile_entity instanceof TCTileE))
					return null;
				return new TCGUI(player.inventory, (TCTileE) tile_entity);
			case GuiIDs.GG:
				if (!(tile_entity instanceof GGTileE))
					return null;
				return new GGGUI(player.inventory, (GGTileE) tile_entity);
			default:
				return null;
		}
	}
}
