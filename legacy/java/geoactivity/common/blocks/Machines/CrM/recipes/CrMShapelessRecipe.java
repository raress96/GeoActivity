package geoactivity.common.blocks.Machines.CrM.recipes;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class CrMShapelessRecipe implements IRecipe
{
    private final ItemStack recipeOutput;
    public final List<ItemStack> recipeItems;
	private List<ItemStack> compatiblePerks;

	public CrMShapelessRecipe(ItemStack output, List<ItemStack> inputs, List<ItemStack> perks)
	{
        this.recipeOutput = output;
        this.recipeItems = inputs;
		this.compatiblePerks = perks;
	}

	public List<ItemStack> getCompatiblePerks()
	{
		return this.compatiblePerks;
	}

    @Override
	public ItemStack getRecipeOutput()
    {
        return this.recipeOutput;
    }

    @Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
        }

        return aitemstack;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
	public boolean matches(InventoryCrafting inv, World worldIn)
    {
        List<ItemStack> list = Lists.newArrayList(this.recipeItems);

        for (int i = 0; i < inv.getHeight(); ++i)
        {
            for (int j = 0; j < inv.getWidth(); ++j)
            {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (itemstack != null)
                {
                    boolean flag = false;

                    for (ItemStack itemstack1 : list)
                    {
                        if (itemstack.getItem() == itemstack1.getItem() && (itemstack1.getMetadata() == 32767 || itemstack.getMetadata() == itemstack1.getMetadata()))
                        {
                            flag = true;
                            list.remove(itemstack1);
                            break;
                        }
                    }

                    if (!flag)
                    {
                        return false;
                    }
                }
            }
        }

        return list.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        return this.recipeOutput.copy();
    }

    /**
     * Returns the size of the recipe area
     */
    @Override
	public int getRecipeSize()
    {
        return this.recipeItems.size();
    }
}
