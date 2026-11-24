package geoactivity.armor;

import geoactivity.item.NoCraftingRepair;
import geoactivity.perk.ArmorPerk;
import geoactivity.perk.PerkData;
import geoactivity.perk.PerkableArmor;
import geoactivity.perk.ToolPerk;
import geoactivity.tool.ProfiledItem;
import geoactivity.tool.ToolLogic;
import geoactivity.tool.ToolMenu;
import geoactivity.tool.ToolProfile;
import geoactivity.tool.ToolProfiles;
import geoactivity.util.Tooltips;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * Advanced Armor: burns coal as durability like the Advanced tools, takes one perk from the Crafting Machine, and is
 * "destroyed" (no protection) at max damage − 2 until refuelled. Damage absorption from perks and the special wear
 * rules live in {@link ArmorEvents}.
 */
public class AdvancedArmorItem extends Item implements ProfiledItem, PerkableArmor, NoCraftingRepair {
    private final ArmorType type;

    public AdvancedArmorItem(Properties properties, ArmorType type) {
        super(properties);
        this.type = type;
    }

    /** Armor properties without fixed attribute modifiers (they depend on the stack, see below). */
    public static Properties properties(Properties p, ArmorType type) {
        var material = ModArmorMaterials.ADVANCED;
        return p.durability(type.getDurability(material.durability()))
                .enchantable(material.enchantmentValue())
                .component(DataComponents.EQUIPPABLE, Equippable.builder(type.getSlot())
                        .setEquipSound(material.equipSound()).setAsset(material.assetId()).build());
    }

    public ArmorType type() {
        return type;
    }

    @Override
    public ToolProfile profile() {
        return ToolProfiles.ADVANCED_ARMOR;
    }

    public static boolean isDestroyed(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 2;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        stack.set(DataComponents.DAMAGE, Math.max(0, Math.min(damage, stack.getMaxDamage() - 1)));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return isDestroyed(stack) ? ItemAttributeModifiers.EMPTY : ModArmorMaterials.ADVANCED.createAttributes(type);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return super.use(level, player, hand);
        }
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                int slot = player.getInventory().getSelectedSlot();
                player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new ToolMenu(id, inventory, slot), stack.getHoverName()),
                        buf -> buf.writeVarInt(slot));
            }
            return InteractionResult.SUCCESS;
        }
        if (!level.isClientSide() && stack.getDamageValue() > 0) {
            ToolLogic.refuel(stack, profile());
        }
        // like vanilla: use also equips it
        return super.use(level, player, hand);
    }

    /** Respiration (helmet) refills air when it runs out, for 16 durability; No Fall (legs/boots) cancels fall damage. */
    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (slot != type.getSlot() || isDestroyed(stack) || !(owner instanceof Player player)) {
            return;
        }
        PerkData perks = ToolLogic.perks(stack);
        if (type == ArmorType.HELMET && perks.has(ArmorPerk.RESPIRATION) && stack.getDamageValue() < stack.getMaxDamage() - 16
                && player.getAirSupply() <= 0) {
            player.setAirSupply(150);
            stack.setDamageValue(stack.getDamageValue() + 16);
        }
        if ((type == ArmorType.LEGGINGS || type == ArmorType.BOOTS) && perks.has(ArmorPerk.NO_FALL) && player.fallDistance > 0) {
            player.resetFallDistance();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> out, TooltipFlag flag) {
        if (isDestroyed(stack)) {
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
        for (ArmorPerk perk : ArmorPerk.values()) {
            if (perks.has(perk)) {
                out.accept(Component.translatable("tooltip.geoactivity.armor_perk." + perk.key()).withStyle(ChatFormatting.GRAY));
            }
        }
        if (perks.has(ToolPerk.EFFICIENCY)) {
            out.accept(Component.translatable("tooltip.geoactivity.perk.efficiency", 1).withStyle(ChatFormatting.GRAY));
        }
    }
}
