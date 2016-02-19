package com.entirecraft.general.integration.jei;

import com.entirecraft.general.blocks.Machines.CrM.CrMContainer;
import com.entirecraft.general.blocks.Machines.CrM.CrMCrafting;
import com.entirecraft.general.blocks.Machines.CrM.CrMGUI;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

@JEIPlugin
public class GeoActivityJEIPlugin implements IModPlugin
{
	private IJeiHelpers jeiHelpers;
	private IItemRegistry itemRegistry;

	public GeoActivityJEIPlugin()
	{}

	@Override
	public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers)
	{
		this.jeiHelpers = jeiHelpers;
	}

	@Override
	public void onItemRegistryAvailable(IItemRegistry itemRegistry)
	{
		this.itemRegistry = itemRegistry;
	}

	@Override
	public void register(IModRegistry registry)
	{
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new CrMRecipeCategory(guiHelper));
		registry.addRecipeHandlers(
			new CrMShapedRecipeHandler(),
			new CrMShapelessRecipeHandler()
		);

		registry.addRecipeClickArea(CrMGUI.class, 84, 44, 24, 12, GeoActivityRecipeCategoryUid.CRAFTING_MACHINE);

		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		recipeTransferRegistry.addRecipeTransferHandler(CrMContainer.class, GeoActivityRecipeCategoryUid.CRAFTING_MACHINE, 4, 9, 13, 36);

		registry.addRecipes(CrMCrafting.getInstance().getRecipeList());
	}

	@Override
	public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry)
	{}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
	{}

}
