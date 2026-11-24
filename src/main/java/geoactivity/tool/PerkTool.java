package geoactivity.tool;

import geoactivity.item.NoCraftingRepair;
import geoactivity.perk.PerkData;
import geoactivity.perk.PerkableTool;
import geoactivity.perk.ToolPerk;
import geoactivity.registry.ModDataComponents;
import geoactivity.util.EnergyFormat;
import geoactivity.util.Tooltips;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * A GeoActivity GUI tool: never breaks, becomes unusable ("destroyed") when out of fuel/energy, opens its inventory
 * with sneak + use, refuels from its first fuel slot on use, and carries perks. Area/capitator breaking and the drop
 * perks live in {@link ToolEvents}.
 */
public class PerkTool extends Item implements NoCraftingRepair, ProfiledItem {
    private final ToolProfile profile;

    public PerkTool(Properties properties, ToolProfile profile) {
        super(properties);
        this.profile = profile;
    }

    @Override
    public ToolProfile profile() {
        return profile;
    }

    public boolean isEnergyTool() {
        return profile.maxDamage() == 0;
    }

    // --- usability, wear, energy ---

    public static int energy(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
    }

    public boolean isUsable(ItemStack stack, @Nullable Player player) {
        if (ToolLogic.isCreative(player)) {
            return true;
        }
        return isEnergyTool() ? energy(stack) > ToolProfiles.RF_PER_USE : stack.getDamageValue() < profile.unusableAt();
    }

    /** Uses up durability (fuel) or energy for one action. */
    public void wear(ItemStack stack, @Nullable LivingEntity user, int amount, boolean blockBreak) {
        if (user instanceof Player player && player.isCreative()) {
            return;
        }
        if (isEnergyTool()) {
            int cost = ToolProfiles.RF_PER_USE;
            if (blockBreak && ToolLogic.level(stack, ToolPerk.RADIUS) > 2) {
                cost *= 2;
            }
            stack.set(ModDataComponents.ENERGY, Math.max(0, energy(stack) - cost));
        } else if (amount > 0) {
            stack.setDamageValue(Math.min(stack.getMaxDamage() - 1, stack.getDamageValue() + amount));
        }
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        // the tool never breaks: it stops working at its "destroyed" threshold instead
        stack.set(DataComponents.DAMAGE, Math.max(0, Math.min(damage, stack.getMaxDamage() - 1)));
    }

    // --- mining ---

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (!isUsable(stack, null)) {
            return 1.0F;
        }
        if (profile.categories().isEmpty()) {
            return state.is(Blocks.COBWEB) ? 15.0F : state.is(net.minecraft.tags.BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
        }
        return ToolLogic.isEffective(profile, state) ? ToolLogic.speed(stack, profile) : 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (!isUsable(stack, null)) {
            return false;
        }
        if (profile.categories().isEmpty()) {
            return state.is(Blocks.COBWEB);
        }
        return ToolLogic.isEffective(profile, state);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F) {
            wear(stack, owner, profile.blockCost(), true);
        }
        return true;
    }

    // --- combat ---

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Player player = attacker instanceof Player p ? p : null;
        if (!isUsable(stack, player)) {
            return;
        }
        wear(stack, attacker, profile.hitCost(), false);
        if (ToolLogic.perks(stack).has(ToolPerk.EXPERIENCE) && !target.isAlive() && attacker.level() instanceof ServerLevel level) {
            // +1 XP per kill; battle tools give 1 more for creatures
            int xp = profile.hoe() && target instanceof PathfinderMob ? 2 : 1;
            ExperienceOrb.award(level, target.position(), xp);
        }
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        if (!isUsable(stack, null)) {
            return ItemAttributeModifiers.EMPTY;
        }
        double damage = profile.attackDamage() + ToolLogic.level(stack, ToolPerk.DAMAGE);
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, profile.attackSpeed(),
                        AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    // --- use: GUI, refuel, hoe ---

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            if (player.isShiftKeyDown()) {
                int slot = player.getInventory().getSelectedSlot();
                player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new ToolMenu(id, inventory, slot), stack.getHoverName()),
                        buf -> buf.writeVarInt(slot));
            } else {
                ToolLogic.refuel(stack, profile);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!profile.hoe() || !isUsable(context.getItemInHand(), context.getPlayer())) {
            return InteractionResult.PASS;
        }
        // till like a vanilla hoe (1.10.2 called the hoe event)
        var transformer = Items.IRON_HOE.getDefaultInstance().get(DataComponents.BLOCK_TRANSFORMER);
        if (transformer == null) {
            return InteractionResult.PASS;
        }
        InteractionResult result = transformer.value().transformBlock(context);
        if (result.consumesAction() && !context.getLevel().isClientSide()) {
            wear(context.getItemInHand(), context.getPlayer(), 1, false);
        }
        return result;
    }

    // --- display ---

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isEnergyTool() || super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (isEnergyTool()) {
            return Math.round(13.0F * energy(stack) / ToolProfiles.RF_CAPACITY);
        }
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return isEnergyTool() ? 0xCC0000 : super.getBarColor(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> out, TooltipFlag flag) {
        if (isEnergyTool()) {
            out.accept(Component.translatable("tooltip.geoactivity.energy", EnergyFormat.units(energy(stack)),
                    EnergyFormat.units(ToolProfiles.RF_CAPACITY)).withStyle(ChatFormatting.GRAY));
        }
        if (!isUsable(stack, null)) {
            out.accept(Component.translatable("tooltip.geoactivity.destroyed").withStyle(ChatFormatting.RED));
        }
        PerkData perks = ToolLogic.perks(stack);
        if (perks.isEmpty()) {
            return;
        }
        if (!Tooltips.shiftDown.getAsBoolean()) {
            out.accept(Component.translatable("tooltip.geoactivity.hold_shift",
                    Component.translatable("tooltip.geoactivity.shift").withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.GRAY));
            return;
        }
        for (ToolPerk perk : ToolPerk.values()) {
            int level = perks.level(perk);
            if (level > 0) {
                out.accept(Component.translatable("tooltip.geoactivity.perk." + perk.key(), level).withStyle(ChatFormatting.GRAY));
            }
        }
        if (perks.has(ToolPerk.ARMOR_BYPASS)) {
            double damage = profile.attackDamage() + perks.level(ToolPerk.DAMAGE);
            out.accept(Component.translatable("tooltip.geoactivity.armor_bypass_damage", String.format(java.util.Locale.ROOT, "%.1f", damage / 3))
                    .withStyle(ChatFormatting.BLUE));
        }
    }

    /** Advanced tools take their single perk from the Crafting Machine. */
    public static class Crafted extends PerkTool implements PerkableTool {
        public Crafted(Properties properties, ToolProfile profile) {
            super(properties, profile);
        }
    }
}
