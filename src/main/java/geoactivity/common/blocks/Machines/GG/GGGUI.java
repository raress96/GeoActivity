package geoactivity.common.blocks.Machines.GG;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import geoactivity.common.blocks.Machines.CrM.CrMTileE;
import geoactivity.common.util.GeneralHelper;

public class GGGUI extends GuiContainer
{
	private static final ResourceLocation guitexture = new ResourceLocation("GeoActivity:textures/gui/GG.png");
	private GGTileE tile_entity;

	public GGGUI(InventoryPlayer playerInv, GGTileE tile)
	{
		super(new GGContainer(tile, playerInv));
		tile_entity = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		mc.renderEngine.bindTexture(guitexture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j)
	{
		this.fontRendererObj.drawString("Geothermal Generator", 34, 5, 0x404040);
		
		this.fontRendererObj.drawString("Lava Blocks: " + tile_entity.lavaBlocks, 25, 20, 0x404040);
		this.fontRendererObj.drawString("Efficiency: " + tile_entity.hightEfficiency + "%", 25, 30, 0x404040);
		this.fontRendererObj.drawString("RF/t: " + tile_entity.RFPerTick, 25, 40, 0x404040);
		this.fontRendererObj.drawString("Energy: " , 25, 55, 0x404040);
		this.fontRendererObj.drawString(GeneralHelper.getEnergyUnits(tile_entity.buffer.getEnergyStored()) +
		"/" + GeneralHelper.getEnergyUnits(GGTileE.internalStorage) + " RF", 25, 65, 0x404040);
		
		this.fontRendererObj.drawString("Update in:", 114, 40, 0x404040);
		this.fontRendererObj.drawString((GGTileE.updateAfter - tile_entity.ticksTillUpdate) / 20 + " s", 124, 50, 0x404040);
	}
}
