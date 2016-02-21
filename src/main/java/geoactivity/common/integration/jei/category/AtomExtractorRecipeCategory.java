package geoactivity.common.integration.jei.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import geoactivity.common.GAMod;
import geoactivity.common.blocks.Machines.AE.AERecipes;
import geoactivity.common.integration.jei.GeoActivityRecipeCategoryUid;
import geoactivity.common.integration.jei.wrapper.AtomExtractorRecipeWrapper;
import geoactivity.common.util.Translator;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AtomExtractorRecipeCategory implements IRecipeCategory
{
	@Nonnull
	protected IDrawable background;
	@Nonnull
	protected String localizedName;
	@Nonnull
	protected IDrawableAnimated arrow;

	public AtomExtractorRecipeCategory(IGuiHelper guiHelper)
	{
		ResourceLocation location = new ResourceLocation("geoactivity", "textures/gui/AE.png");

		background = guiHelper.createDrawable(location, 10, 10, 160, 65);
		localizedName = Translator.translateToLocal("ga.jei.category.atom_extractor");

		IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 176, 14, 36, 16);
		arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 400, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public String getUid()
	{
		return GeoActivityRecipeCategoryUid.ATOM_EXTRACTOR;
	}

	@Override
	public String getTitle()
	{
		return this.localizedName;
	}

	@Override
	public IDrawable getBackground()
	{
		return this.background;
	}

	@Override
	public void drawExtras(Minecraft minecraft)
	{}

	@Override
	public void drawAnimations(Minecraft minecraft)
	{
		arrow.draw(minecraft, 31, 19);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper)
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, false, 11, 19);
		guiItemStacks.init(1, false, 39, 44);
		guiItemStacks.init(2, false, 75, 19);
		guiItemStacks.init(3, false, 101, 19);
		guiItemStacks.init(4, false, 127, 19);

		if (recipeWrapper instanceof AtomExtractorRecipeWrapper)
		{
			AtomExtractorRecipeWrapper wrapper = (AtomExtractorRecipeWrapper) recipeWrapper;

			ItemStack[] outputElemens = wrapper.getOutputElements();

			guiItemStacks.set(0, wrapper.getInputStack());
			guiItemStacks.set(2, outputElemens[0]);
			guiItemStacks.set(3, outputElemens[1]);
			guiItemStacks.set(4, outputElemens[2]);

			guiItemStacks.set(1, new ItemStack(GAMod.elementContainer, wrapper.getElementContainerNumber()));
		}
		else
		{
			System.out.println("RecipeWrapper is not a known crafting wrapper type: " + recipeWrapper.toString());
		}
	}

	@Nonnull
	public static List<AtomExtractorRecipeWrapper> getAtomExtractorRecipes(IJeiHelpers helpers)
	{
		IStackHelper stackHelper = helpers.getStackHelper();
		Map<ItemStack, ItemStack[]> smeltingMap = AERecipes.getInstance().getSmeltingList();

		List<AtomExtractorRecipeWrapper> recipes = new ArrayList<>();

		for (Map.Entry<ItemStack, ItemStack[]> itemStackItemStackEntry : smeltingMap.entrySet())
		{
			ItemStack input = itemStackItemStackEntry.getKey();
			ItemStack[] outputs = itemStackItemStackEntry.getValue();

			int time = AERecipes.getInstance().getTime(input);
			int number = AERecipes.getInstance().getNumber(input);

			List<ItemStack> inputs = stackHelper.getSubtypes(input);
			AtomExtractorRecipeWrapper recipe = new AtomExtractorRecipeWrapper(input, outputs, time, number);
			recipes.add(recipe);
		}

		return recipes;
	}
}
