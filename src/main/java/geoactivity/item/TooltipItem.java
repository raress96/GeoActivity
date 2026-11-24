package geoactivity.item;

import geoactivity.util.Tooltips;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** Item with fixed tooltip lines from the lang file ({@code <description id>.tooltip.N}). */
public class TooltipItem extends Item {
    private final boolean needsShift;

    public TooltipItem(Properties properties, boolean needsShift) {
        super(properties);
        this.needsShift = needsShift;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> out, TooltipFlag flag) {
        String base = getDescriptionId() + ".tooltip";
        if (needsShift) {
            Tooltips.shiftForInfo(out, base, ChatFormatting.RED);
        } else {
            Tooltips.lines(out, base, ChatFormatting.GRAY);
        }
    }
}
