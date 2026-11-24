package geoactivity.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import geoactivity.item.NoCraftingRepair;
import geoactivity.registry.ModDataComponents;
import geoactivity.registry.ModItems;
import geoactivity.util.Smelting;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Auto Stone Builder: put a source item and a target block in its GUI; it pulls source items from your inventory,
 * converts them (smelt, 2×2 craft, or both) and places the target block on use. Coal is its durability.
 * Slots: 0 coal, 1 source, 2 target.
 */
public class AutoStoneBuilderItem extends Item implements NoCraftingRepair {
    public static final int UNUSABLE_AT = 998, MAX_BLOCKS = 64, SLOTS = 3;

    /** How many blocks are stored, what they are and the durability each placement costs. */
    public record State(int blocks, int cost, ItemStack target) {
        public static final State EMPTY = new State(0, 1, ItemStack.EMPTY);
        public static final Codec<State> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("blocks").forGetter(State::blocks),
                Codec.INT.fieldOf("cost").forGetter(State::cost),
                ItemStack.OPTIONAL_CODEC.fieldOf("target").forGetter(State::target)).apply(i, State::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, State> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, State::blocks, ByteBufCodecs.VAR_INT, State::cost, ItemStack.OPTIONAL_STREAM_CODEC, State::target,
                State::new);

        @Override
        public boolean equals(Object o) {
            return o instanceof State s && s.blocks == blocks && s.cost == cost && ItemStack.matches(s.target, target);
        }

        @Override
        public int hashCode() {
            return blocks * 31 + cost;
        }
    }

    public AutoStoneBuilderItem(Properties properties) {
        super(properties);
    }

    public static State state(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BUILDER_STATE.get(), State.EMPTY);
    }

    public static boolean isUsable(ItemStack stack) {
        return stack.getDamageValue() < UNUSABLE_AT;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        stack.set(net.minecraft.core.component.DataComponents.DAMAGE, Math.max(0, Math.min(damage, stack.getMaxDamage() - 1)));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            if (player.isShiftKeyDown()) {
                int slot = player.getInventory().getSelectedSlot();
                player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new AutoStoneBuilderMenu(id, inventory, slot),
                        stack.getHoverName()), buf -> buf.writeVarInt(slot));
            } else if (stack.getDamageValue() > 150) {
                refuel(stack);
            }
        }
        return InteractionResult.SUCCESS;
    }

    /** Lignite repairs 150, bituminous 300, anthracite 450, while the repair still fits. */
    static void refuel(ItemStack stack) {
        NonNullList<ItemStack> items = ToolLogic.inventory(stack, SLOTS);
        ItemStack coal = items.get(0);
        int fuel = coal.is(ModItems.LIGNITE_COAL.get()) ? 150 : coal.is(ModItems.BITUMINOUS_COAL.get()) ? 300
                : coal.is(ModItems.ANTHRACITE_COAL.get()) ? 450 : 0;
        while (fuel > 0 && !coal.isEmpty() && stack.getDamageValue() - fuel > 0) {
            stack.setDamageValue(stack.getDamageValue() - fuel);
            coal.shrink(1);
        }
        ToolLogic.setInventory(stack, items);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        State state = state(stack);
        Player player = context.getPlayer();
        if (!isUsable(stack) || state.blocks() <= 0 || !(state.target().getItem() instanceof BlockItem blockItem)) {
            return InteractionResult.PASS;
        }
        ItemStack configured = ToolLogic.inventory(stack, SLOTS).get(2);
        if (!configured.is(state.target().getItem())) {
            stack.set(ModDataComponents.BUILDER_STATE, State.EMPTY);
            return InteractionResult.PASS;
        }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        if (player != null && !player.mayUseItemAt(pos, context.getClickedFace(), stack) || !level.getBlockState(pos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide()) {
            BlockState placed = blockItem.getBlock().defaultBlockState();
            level.setBlockAndUpdate(pos, placed);
            var sound = placed.getSoundType();
            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            if (player == null || !player.isCreative()) {
                stack.setDamageValue(stack.getDamageValue() + state.cost());
            }
            State next = new State(state.blocks() - 1, state.cost(), state.target());
            stack.set(ModDataComponents.BUILDER_STATE, next);
            if (next.blocks() == 1 && player != null && level instanceof ServerLevel server) {
                search(stack, player.getInventory(), server);
            }
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * 1.10.2 {@code ASBInventory.searchItem}: work out how the source becomes the target (smelt: cost 2; 2×2 craft:
     * cost 1; smelt then craft or craft then smelt: cost 3) and pull source items from the player's main inventory
     * until 64 blocks are stored.
     */
    public static void search(ItemStack stack, Inventory inventory, ServerLevel level) {
        NonNullList<ItemStack> items = ToolLogic.inventory(stack, SLOTS);
        ItemStack source = items.get(1);
        ItemStack target = items.get(2);
        State state = state(stack);
        if (source.isEmpty() || target.isEmpty() || !(target.getItem() instanceof BlockItem)) {
            stack.set(ModDataComponents.BUILDER_STATE, State.EMPTY);
            return;
        }
        int blocks = state.target().is(target.getItem()) ? state.blocks() : 0;
        ItemStack smelted = Smelting.result(level, source);
        int perPull = 0, perBlocks = 0, cost = 1;
        if (!smelted.isEmpty()) {
            if (smelted.is(target.getItem())) {
                perPull = 1;
                perBlocks = smelted.getCount();
                cost = 2;
            } else {
                ItemStack crafted = craft2x2(level, smelted);
                if (crafted.is(target.getItem())) {
                    perPull = Math.max(1, 4 / smelted.getCount());
                    perBlocks = crafted.getCount();
                    cost = 3;
                }
            }
        } else {
            ItemStack crafted = craft2x2(level, source);
            if (crafted.is(target.getItem())) {
                perPull = 4;
                perBlocks = crafted.getCount();
                cost = 1;
            } else if (!crafted.isEmpty()) {
                ItemStack craftedSmelted = Smelting.result(level, crafted);
                if (craftedSmelted.is(target.getItem())) {
                    perPull = Math.max(1, 4 / crafted.getCount());
                    perBlocks = craftedSmelted.getCount();
                    cost = 3;
                }
            }
        }
        if (perPull > 0) {
            for (int i = 0; i < 36 && blocks < MAX_BLOCKS; i++) {
                ItemStack slot = inventory.getItem(i);
                if (slot == stack || !ItemStack.isSameItemSameComponents(slot, source)) {
                    continue;
                }
                while (slot.getCount() >= perPull && blocks + perBlocks <= MAX_BLOCKS) {
                    blocks += perBlocks;
                    slot.shrink(perPull);
                }
            }
        }
        stack.set(ModDataComponents.BUILDER_STATE, blocks > 0 ? new State(blocks, cost, target.copyWithCount(1)) : State.EMPTY);
    }

    private static ItemStack craft2x2(ServerLevel level, ItemStack ingredient) {
        ItemStack one = ingredient.copyWithCount(1);
        CraftingInput input = CraftingInput.of(2, 2, List.of(one, one.copy(), one.copy(), one.copy()));
        return level.recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, level)
                .map(holder -> holder.value().assemble(input))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> out, TooltipFlag flag) {
        if (!isUsable(stack)) {
            out.accept(Component.translatable("tooltip.geoactivity.destroyed").withStyle(ChatFormatting.RED));
        }
        out.accept(Component.translatable("tooltip.geoactivity.blocks", state(stack).blocks()).withStyle(ChatFormatting.GRAY));
    }
}
