package geoactivity.item;

import geoactivity.registry.ModDataComponents;
import geoactivity.util.Tooltips;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;

/**
 * Drop Teleportation perk: sneak + use on an inventory binds it; use in air (not sneaking) unbinds it.
 * The bound target is copied onto the tool when the perk is installed.
 */
public class DropTeleportPerkItem extends TooltipItem {
    public DropTeleportPerkItem(Properties properties) {
        super(properties, true);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        var handler = level.getCapability(Capabilities.Item.BLOCK, context.getClickedPos(), context.getClickedFace());
        if (handler == null || handler.size() == 0) {
            return InteractionResult.PASS;
        }
        context.getItemInHand().set(ModDataComponents.DROP_TARGET, GlobalPos.of(level.dimension(), context.getClickedPos()));
        if (!level.isClientSide()) {
            player.sendSystemMessage(Component.translatable("message.geoactivity.drop_teleport.bound"));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() && stack.has(ModDataComponents.DROP_TARGET.get())) {
            stack.remove(ModDataComponents.DROP_TARGET);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> out, TooltipFlag flag) {
        GlobalPos target = stack.get(ModDataComponents.DROP_TARGET.get());
        if (target == null) {
            super.appendHoverText(stack, context, display, out, flag);
            return;
        }
        var pos = target.pos();
        out.accept(Component.translatable("tooltip.geoactivity.drop_teleport.bound", pos.getX(), pos.getY(), pos.getZ())
                .withStyle(ChatFormatting.RED));
        out.accept(Component.translatable("tooltip.geoactivity.drop_teleport.unbind").withStyle(ChatFormatting.RED));
    }
}
