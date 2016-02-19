package geoactivity.common.blocks.Machines.CR;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class CRGUI extends GuiContainer
{
	private static final ResourceLocation guitexture = new ResourceLocation("GeoActivity:textures/gui/CR.png");
	private CRTileE furnaceInventory;

	public CRGUI(InventoryPlayer playerInv, CRTileE tile)
	{
		super(new CRContainer(tile, playerInv));
		furnaceInventory = tile;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		this.fontRendererObj.drawString("Coal Refiner", 60, 8, 4210752);
		this.fontRendererObj.drawString("Inventory", 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(guitexture);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
		int var7;

		if (furnaceInventory.isBurning())
		{
			var7 = furnaceInventory.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(var5 + 49, var6 + 36 + 12 - var7, 176, 12 - var7, 14, var7 + 2);
		}

		var7 = furnaceInventory.getCookProgressScaled(24);
		this.drawTexturedModalRect(var5 + 89, var6 + 34, 176, 14, var7 + 1, 16);
	}
}
