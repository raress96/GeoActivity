package geoactivity.common.integration.jei.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import geoactivity.common.blocks.Machines.CR.CRRecipes;
import geoactivity.common.integration.jei.GeoActivityRecipeCategoryUid;
import geoactivity.common.integration.jei.wrapper.CoalRefinerRecipeWrapper;
import geoactivity.common.util.GeneralHelper;
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

public class CoalRefinerRecipeCategory implements IRecipeCategory
{
	@Nonnull
	protected IDrawable background;
	@Nonnull
	protected String localizedName;
	@Nonnull
	protected IDrawableAnimated flame;
	@Nonnull
	protected IDrawableAnimated arrow;

	public CoalRefinerRecipeCategory(IGuiHelper guiHelper)
	{
		ResourceLocation location = new ResourceLocation("geoactivity", "textures/gui/CR.png");

		background = guiHelper.createDrawable(location, 10, 10, 160, 50);
		localizedName = Translator.translateToLocal("ga.jei.category.coal_refiner");

		IDrawableStatic flameDrawable = guiHelper.createDrawable(location, 176, 0, 14, 14);
		flame = guiHelper.createAnimatedDrawable(flameDrawable, 400, IDrawableAnimated.StartDirection.TOP, true);

		IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 176, 14, 24, 16);
		arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 400, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public String getUid()
	{
		return GeoActivityRecipeCategoryUid.COAL_REFINER;
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
		flame.draw(minecraft, 38, 26);
		arrow.draw(minecraft, 79, 24);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper)
	{
		recipeLayout.setRecipeTransferButton(140, 50);
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 55, 24);
		guiItemStacks.init(1, false, 19, 24);
		guiItemStacks.init(2, false, 115, 24);

		if (recipeWrapper instanceof CoalRefinerRecipeWrapper)
		{
			guiItemStacks.set(1, GeneralHelper.getCoalRefinerFuels());
			guiItemStacks.set(0, recipeWrapper.getInputs());
			guiItemStacks.set(2, recipeWrapper.getOutputs());
		}
		else
		{
			System.out.println("RecipeWrapper is not a known crafting wrapper type: " + recipeWrapper.toString());
		}
	}

	@Nonnull
	public static List<CoalRefinerRecipeWrapper> getCoalRefinerRecipes(IJeiHelpers helpers)
	{
		IStackHelper stackHelper = helpers.getStackHelper();
		Map<ItemStack, ItemStack> smeltingMap = CRRecipes.getInstance().getSmeltingList();

		List<CoalRefinerRecipeWrapper> recipes = new ArrayList<>();

		for (Map.Entry<ItemStack, ItemStack> itemStackItemStackEntry : smeltingMap.entrySet())
		{
			ItemStack input = itemStackItemStackEntry.getKey();
			ItemStack output = itemStackItemStackEntry.getValue();

			float experience = CRRecipes.getInstance().getExperience(output);

			List<ItemStack> inputs = stackHelper.getSubtypes(input);
			CoalRefinerRecipeWrapper recipe = new CoalRefinerRecipeWrapper(inputs, output, experience);
			recipes.add(recipe);
		}

		return recipes;
	}
}
