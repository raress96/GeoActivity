package geoactivity.common.blocks.Machines.AE;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


public class AEGUI extends GuiContainer
{
	private static final ResourceLocation guitexture = new ResourceLocation("GeoActivity:textures/gui/AE.png");
	private AETileE tile_entity;

	public AEGUI(InventoryPlayer playerInv, AETileE tile)
	{
		super(new AEContainer(tile, playerInv));
		tile_entity = tile;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		this.fontRendererObj.drawString("Atom Extractor", 52, 8, 4210752);
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
		
		var7 = tile_entity.getCookProgressScaled(35);
		this.drawTexturedModalRect(var5 + 42, var6 + 30, 176, 14, var7 + 1, 16);
	}
}
