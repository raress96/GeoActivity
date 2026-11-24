package geoactivity.item;

import geoactivity.util.Tooltips;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/** Block item with a shift-for-info tooltip ({@code <block description id>.tooltip.N} in the lang file). */
public class InfoBlockItem extends BlockItem {
    public InfoBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> out, TooltipFlag flag) {
        Tooltips.shiftForInfo(out, getDescriptionId() + ".tooltip", ChatFormatting.GRAY);
    }
}
