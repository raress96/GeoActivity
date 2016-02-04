package com.entirecraft.general.integration.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

import com.entirecraft.general.blocks.Machines.AE.AEGUI;
import com.entirecraft.general.blocks.Machines.CR.CRGUI;
import com.entirecraft.general.blocks.Machines.ChM.ChMGUI;
import com.entirecraft.general.blocks.Machines.CrM.CrMGUI;
import com.entirecraft.lib.Reference;

public class NEIGeoActivityConfig implements IConfigureNEI
{
	@Override
	public void loadConfig()
	{
		CrMShapedHandler.guiclass = CrMGUI.class;
		CRHandler.guiclass = CRGUI.class;
		AEHandler.guiclass = AEGUI.class;
		ChMHandler.guiclass = ChMGUI.class;

		API.registerGuiOverlay(CrMShapedHandler.guiclass, "CrM.gui", 4, 9);
		API.registerGuiOverlay(CRHandler.guiclass, "CR.gui", 8, 9);
		API.registerGuiOverlay(AEHandler.guiclass, "AE.gui", 5, 9);
		API.registerGuiOverlay(ChMHandler.guiclass, "ChM.gui", 5, -6);

		//CrM
		API.registerRecipeHandler(new CrMShapedHandler());
		API.registerUsageHandler(new CrMShapedHandler());
		API.registerRecipeHandler(new CrMShapelessHandler());
		API.registerUsageHandler(new CrMShapelessHandler());

		//CR
		API.registerRecipeHandler(new CRHandler());
		API.registerUsageHandler(new CRHandler());

		//AE
		API.registerRecipeHandler(new AEHandler());
		API.registerUsageHandler(new AEHandler());

		//ChM
		API.registerRecipeHandler(new ChMHandler());
		API.registerUsageHandler(new ChMHandler());
	}

	@Override
	public String getName()
	{
		return Reference.MOD_NAME + " NEI";
	}

	@Override
	public String getVersion()
	{
		return Reference.VERSION;
	}
}
