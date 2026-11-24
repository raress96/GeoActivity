package geoactivity.armor;

import geoactivity.GeoActivity;
import geoactivity.perk.ArmorPerk;
import geoactivity.perk.PerkData;
import geoactivity.tool.ToolLogic;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Advanced Armor perks (1.10.2 {@code ISpecialArmor}). Each worn, non-destroyed piece with a matching perk absorbs an
 * extra share of the damage: General always adds 1/2/1/1 (helmet/chest/legs/boots) out of 25; Blast/Fire/Magic/
 * Projectile add 2/3/2/2 out of 25 against their damage type. Wear per hit: 1 if the perk matched, else 2.
 * The base protection is the normal armor value (see {@link ModArmorMaterials}).
 */
@EventBusSubscriber(modid = GeoActivity.MOD_ID)
public final class ArmorEvents {
    private ArmorEvents() {}

    @SubscribeEvent
    static void onIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            return;
        }
        int share = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = event.getEntity().getItemBySlot(slot);
            if (stack.getItem() instanceof AdvancedArmorItem armor && slot == armor.type().getSlot() && !AdvancedArmorItem.isDestroyed(stack)) {
                share += bonus(ToolLogic.perks(stack), armor.type(), source);
            }
        }
        if (share > 0) {
            event.setAmount(event.getAmount() * Math.max(0.0F, 1.0F - share / 25.0F));
        }
    }

    /** Extra absorption (out of 25) of one piece against {@code source}. */
    public static int bonus(PerkData perks, ArmorType type, DamageSource source) {
        boolean chest = type == ArmorType.CHESTPLATE;
        if (perks.has(ArmorPerk.GENERAL)) {
            return chest ? 2 : 1;
        }
        return matchesTypedPerk(perks, source) ? (chest ? 3 : 2) : 0;
    }

    static boolean matchesTypedPerk(PerkData perks, DamageSource source) {
        return perks.has(ArmorPerk.BLAST) && source.is(DamageTypeTags.IS_EXPLOSION)
                || perks.has(ArmorPerk.FIRE) && source.is(DamageTypeTags.IS_FIRE)
                || perks.has(ArmorPerk.MAGIC) && source.is(Tags.DamageTypes.IS_MAGIC)
                || perks.has(ArmorPerk.PROJECTILE) && source.is(DamageTypeTags.IS_PROJECTILE);
    }

    /** 1.10.2 {@code damageArmor}: 1 durability when a perk matched, otherwise 2; destroyed pieces don't wear. */
    @SubscribeEvent
    static void onArmorHurt(ArmorHurtEvent event) {
        for (var entry : event.getArmorMap().entrySet()) {
            ItemStack stack = entry.getValue().armorItemStack;
            if (!(stack.getItem() instanceof AdvancedArmorItem)) {
                continue;
            }
            if (AdvancedArmorItem.isDestroyed(stack)) {
                event.setNewDamage(entry.getKey(), 0);
                continue;
            }
            PerkData perks = ToolLogic.perks(stack);
            DamageSource source = event.getDamageSource();
            boolean matched = perks.has(ArmorPerk.GENERAL) || matchesTypedPerk(perks, source);
            event.setNewDamage(entry.getKey(), matched ? 1 : 2);
        }
    }
}
