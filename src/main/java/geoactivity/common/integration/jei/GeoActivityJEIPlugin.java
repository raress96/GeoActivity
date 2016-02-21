package geoactivity.common.integration.jei;

import geoactivity.common.blocks.Machines.ACR.ACRGUI;
import geoactivity.common.blocks.Machines.AE.AEGUI;
import geoactivity.common.blocks.Machines.CR.CRGUI;
import geoactivity.common.blocks.Machines.ChM.ChMContainer;
import geoactivity.common.blocks.Machines.ChM.ChMGUI;
import geoactivity.common.blocks.Machines.CrM.CrMContainer;
import geoactivity.common.blocks.Machines.CrM.CrMCrafting;
import geoactivity.common.blocks.Machines.CrM.CrMGUI;
import geoactivity.common.integration.jei.category.AdvancedCoalRefinerRecipeCategory;
import geoactivity.common.integration.jei.category.AtomExtractorRecipeCategory;
import geoactivity.common.integration.jei.category.ChemistryMachineRecipeCategory;
import geoactivity.common.integration.jei.category.CoalRefinerRecipeCategory;
import geoactivity.common.integration.jei.category.CrMRecipeCategory;
import geoactivity.common.integration.jei.handler.AdvancedCoalRefinerRecipeHandler;
import geoactivity.common.integration.jei.handler.AtomExtractorRecipeHandler;
import geoactivity.common.integration.jei.handler.ChemistryMachineRecipeHandler;
import geoactivity.common.integration.jei.handler.CoalRefinerRecipeHandler;
import geoactivity.common.integration.jei.handler.CrMShapedRecipeHandler;
import geoactivity.common.integration.jei.handler.CrMShapelessRecipeHandler;
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

		registry.addRecipeCategories(
			new CrMRecipeCategory(guiHelper),
			new CoalRefinerRecipeCategory(guiHelper),
			new AdvancedCoalRefinerRecipeCategory(guiHelper),
			new AtomExtractorRecipeCategory(guiHelper),
			new ChemistryMachineRecipeCategory(guiHelper)
		);

		registry.addRecipeHandlers(
			new CrMShapedRecipeHandler(),
			new CrMShapelessRecipeHandler(),
			new CoalRefinerRecipeHandler(),
			new AdvancedCoalRefinerRecipeHandler(),
			new AtomExtractorRecipeHandler(),
			new ChemistryMachineRecipeHandler()
		);

		registry.addRecipeClickArea(CrMGUI.class, 84, 44, 24, 12, GeoActivityRecipeCategoryUid.CRAFTING_MACHINE);
		registry.addRecipeClickArea(CRGUI.class, 88, 36, 24, 12, GeoActivityRecipeCategoryUid.COAL_REFINER);
		registry.addRecipeClickArea(ACRGUI.class, 84, 34, 24, 12, GeoActivityRecipeCategoryUid.ADVANCED_COAL_REFINER);
		registry.addRecipeClickArea(AEGUI.class, 45, 30, 24, 12, GeoActivityRecipeCategoryUid.ATOM_EXTRACTOR);
		registry.addRecipeClickArea(ChMGUI.class, 76, 32, 24, 12, GeoActivityRecipeCategoryUid.CHEMISTRY_MACHINE);

		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		recipeTransferRegistry.addRecipeTransferHandler(CrMContainer.class, GeoActivityRecipeCategoryUid.CRAFTING_MACHINE, 4, 9, 13, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ChMContainer.class, GeoActivityRecipeCategoryUid.CHEMISTRY_MACHINE, 0, 2, 2, 36);

		registry.addRecipes(CrMCrafting.getInstance().getRecipeList());
		registry.addRecipes(CoalRefinerRecipeCategory.getCoalRefinerRecipes(this.jeiHelpers));
		registry.addRecipes(AdvancedCoalRefinerRecipeCategory.getAdvancedCoalRefinerRecipes(this.jeiHelpers));
		registry.addRecipes(AtomExtractorRecipeCategory.getAtomExtractorRecipes(this.jeiHelpers));
		registry.addRecipes(ChemistryMachineRecipeCategory.getChemistryMachineRecipes(this.jeiHelpers));
	}

	@Override
	public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry)
	{}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
	{}

}
