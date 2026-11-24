package geoactivity.gametest;

import geoactivity.armor.AdvancedArmorItem;
import geoactivity.perk.ArmorPerk;
import geoactivity.perk.PerkData;
import geoactivity.registry.ModDataComponents;
import geoactivity.registry.ModItems;
import geoactivity.tool.ToolLogic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Phase 7: Reinforced and Advanced armor, armor perks. */
final class ArmorTests {
    private ArmorTests() {}

    static void register() {
        GATests.test("armor_typed_perk_reduces_only_matching_damage", 20, helper -> {
            // one zombie per hit, so hurt cooldowns don't mix in
            Zombie plain = zombie(helper, new BlockPos(2, 1, 2), chest(null));
            Zombie blast = zombie(helper, new BlockPos(6, 1, 2), chest(ArmorPerk.BLAST));
            Zombie plainCactus = zombie(helper, new BlockPos(2, 1, 8), chest(null));
            Zombie blastCactus = zombie(helper, new BlockPos(6, 1, 8), chest(ArmorPerk.BLAST));
            // equipment attributes apply on the entity's next tick
            helper.runAtTickTime(2, () -> {
                float plainLoss = hit(helper, plain, s -> s.explosion(null, null));
                float blastLoss = hit(helper, blast, s -> s.explosion(null, null));
                helper.assertTrue(blastLoss < plainLoss, "blast perk absorbs more of an explosion: " + blastLoss + " vs " + plainLoss);
                float plainCactusLoss = hit(helper, plainCactus, DamageSources::cactus);
                float blastCactusLoss = hit(helper, blastCactus, DamageSources::cactus);
                helper.assertValueEqual(blastCactusLoss, plainCactusLoss, "blast perk vs cactus (armor "
                        + blastCactus.getArmorValue() + " / " + plainCactus.getArmorValue() + ")");
                helper.succeed();
            });
        });

        GATests.test("armor_destroyed_gives_no_protection", 20, helper -> {
            ItemStack destroyed = chest(null);
            destroyed.setDamageValue(destroyed.getMaxDamage() - 2);
            helper.assertTrue(AdvancedArmorItem.isDestroyed(destroyed), "max damage - 2 is destroyed");
            helper.assertTrue(destroyed.getItem().getDefaultAttributeModifiers(destroyed).modifiers().isEmpty(), "no armor attributes");
            Zombie naked = zombie(helper, new BlockPos(2, 1, 2), ItemStack.EMPTY);
            Zombie broken = zombie(helper, new BlockPos(6, 1, 2), destroyed);
            Zombie armored = zombie(helper, new BlockPos(10, 1, 2), chest(ArmorPerk.GENERAL));
            helper.runAtTickTime(2, () -> {
                float nakedLoss = hit(helper, naked, DamageSources::cactus);
                helper.assertValueEqual(hit(helper, broken, DamageSources::cactus), nakedLoss, "destroyed armor = no armor");
                helper.assertTrue(hit(helper, armored, DamageSources::cactus) < nakedLoss, "working armor protects");
                helper.assertValueEqual(broken.getItemBySlot(EquipmentSlot.CHEST).getDamageValue(), destroyed.getMaxDamage() - 2,
                        "destroyed armor doesn't wear further");
                helper.succeed();
            });
        });

        GATests.test("armor_wears_1_with_matching_perk_else_2", 20, helper -> {
            ServerPlayer general = armoredPlayer(helper, new BlockPos(2, 1, 2), chest(ArmorPerk.GENERAL));
            ServerPlayer fire = armoredPlayer(helper, new BlockPos(6, 1, 2), chest(ArmorPerk.FIRE));
            general.hurtServer(helper.getLevel(), helper.getLevel().damageSources().cactus(), 8.0F);
            fire.hurtServer(helper.getLevel(), helper.getLevel().damageSources().cactus(), 8.0F);
            helper.assertValueEqual(general.getItemBySlot(EquipmentSlot.CHEST).getDamageValue(), 1, "general perk always matches");
            helper.assertValueEqual(fire.getItemBySlot(EquipmentSlot.CHEST).getDamageValue(), 2, "fire perk vs cactus");
            helper.succeed();
        });

        GATests.test("armor_respiration_and_no_fall", 20, helper -> {
            ItemStack helmet = piece(ModItems.ADVANCED_HELMET.get(), ArmorPerk.RESPIRATION);
            ItemStack boots = piece(ModItems.ADVANCED_BOOTS.get(), ArmorPerk.NO_FALL);
            ServerPlayer player = armoredPlayer(helper, new BlockPos(3, 1, 3), ItemStack.EMPTY);
            player.setItemSlot(EquipmentSlot.HEAD, helmet);
            player.setItemSlot(EquipmentSlot.FEET, boots);
            ItemStack worn = player.getItemBySlot(EquipmentSlot.HEAD);
            player.setAirSupply(0);
            worn.inventoryTick(helper.getLevel(), player, EquipmentSlot.HEAD);
            helper.assertValueEqual(player.getAirSupply(), 150, "air refilled");
            helper.assertValueEqual(worn.getDamageValue(), 16, "respiration costs 16");

            player.fallDistance = 10.0;
            player.getItemBySlot(EquipmentSlot.FEET).inventoryTick(helper.getLevel(), player, EquipmentSlot.MAINHAND);
            helper.assertValueEqual(player.fallDistance, 10.0, "no effect when not worn");
            player.getItemBySlot(EquipmentSlot.FEET).inventoryTick(helper.getLevel(), player, EquipmentSlot.FEET);
            helper.assertValueEqual(player.fallDistance, 0.0, "fall distance reset");
            helper.succeed();
        });

        GATests.test("crafting_machine_makes_and_perks_advanced_armor", 5, helper -> {
            var grid = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
            for (int slot : new int[] {0, 1, 2, 3, 5}) {
                grid.set(slot, new ItemStack(ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get()));
            }
            ItemStack helmet = geoactivity.machine.crafting.CraftingMachineBlockEntity.findResult(grid);
            helper.assertTrue(helmet.is(ModItems.ADVANCED_HELMET.get()), "helmet crafted: " + helmet);
            helper.assertValueEqual(helmet.getDamageValue(), helmet.getMaxDamage() - 2, "crafted empty");
            ItemStack perk = new ItemStack(ModItems.armorPerk(ArmorPerk.RESPIRATION));
            CraftingMachineTestAccess.applyPerk(helmet, perk);
            helper.assertTrue(ToolLogic.perks(helmet).has(ArmorPerk.RESPIRATION), "respiration installed");
            helper.assertTrue(perk.isEmpty(), "perk used");

            var reset = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
            reset.set(4, helmet);
            ItemStack fresh = geoactivity.machine.crafting.CraftingMachineBlockEntity.findResult(reset);
            helper.assertTrue(fresh.is(ModItems.ADVANCED_HELMET.get()) && ToolLogic.perks(fresh).isEmpty(), "reset recipe: " + fresh);
            helper.succeed();
        });

        GATests.test("armor_refuels_and_equips_on_use", 5, helper -> {
            ItemStack helmet = new ItemStack(ModItems.ADVANCED_HELMET.get());
            helmet.setDamageValue(helmet.getMaxDamage() - 2);
            NonNullList<ItemStack> inv = NonNullList.withSize(2, ItemStack.EMPTY);
            inv.set(0, new ItemStack(ModItems.ANTHRACITE_COAL.get(), 2));
            ToolLogic.setInventory(helmet, inv);
            int start = helmet.getDamageValue();
            ServerPlayer player = ToolTests.survivalPlayer(helper, helmet, new BlockPos(3, 1, 3));
            helmet.getItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            ItemStack worn = player.getItemBySlot(EquipmentSlot.HEAD);
            helper.assertTrue(worn.is(ModItems.ADVANCED_HELMET.get()), "equipped: " + worn);
            // 300 per anthracite while it fits: 383 -> 83 (a second one would go below 0)
            helper.assertValueEqual(worn.getDamageValue(), start - 300, "refuelled damage");
            helper.assertValueEqual(ToolLogic.inventory(worn, 2).get(0).getCount(), 1, "coal left");
            helper.assertFalse(AdvancedArmorItem.isDestroyed(worn), "working again");
            helper.succeed();
        });

        GATests.test("reinforced_armor_values", 5, helper -> {
            int[] expected = {2, 6, 5, 2}; // helmet, chest, legs, boots (like vanilla iron since 3.0.0)
            Item[] pieces = {ModItems.REINFORCED_HELMET.get(), ModItems.REINFORCED_CHESTPLATE.get(),
                    ModItems.REINFORCED_LEGGINGS.get(), ModItems.REINFORCED_BOOTS.get()};
            for (int i = 0; i < pieces.length; i++) {
                ItemStack stack = new ItemStack(pieces[i]);
                double armor = stack.getAttributeModifiers().modifiers().stream()
                        .filter(e -> e.attribute().is(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR))
                        .mapToDouble(e -> e.modifier().amount()).sum();
                helper.assertValueEqual((int) armor, expected[i], "armor of " + stack);
            }
            helper.succeed();
        });
    }

    private static ItemStack chest(ArmorPerk perk) {
        return piece(ModItems.ADVANCED_CHESTPLATE.get(), perk);
    }

    private static ItemStack piece(Item item, ArmorPerk perk) {
        ItemStack stack = new ItemStack(item);
        if (perk != null) {
            stack.set(ModDataComponents.PERKS, PerkData.of(perk.key(), 1));
        }
        return stack;
    }

    /**
     * A husk: a zombie that doesn't burn in daylight. Test areas are roofed with barriers, which let skylight through,
     * so a helmetless zombie could catch fire and its 1 fire damage would eat into the next hit's hurt cooldown.
     */
    private static Zombie zombie(GameTestHelper helper, BlockPos pos, ItemStack chest) {
        Zombie zombie = helper.spawnWithNoFreeWill(EntityTypes.HUSK, pos);
        zombie.setItemSlot(EquipmentSlot.CHEST, chest);
        return zombie;
    }

    /** Damage {@code entity} for 10 and return the health it lost. */
    private static float hit(GameTestHelper helper, net.minecraft.world.entity.LivingEntity entity, Function<DamageSources, DamageSource> source) {
        float before = entity.getHealth();
        entity.hurtServer(helper.getLevel(), source.apply(helper.getLevel().damageSources()), 10.0F);
        return before - entity.getHealth();
    }

    private static ServerPlayer armoredPlayer(GameTestHelper helper, BlockPos at, ItemStack chest) {
        ServerPlayer player = ToolTests.survivalPlayer(helper, ItemStack.EMPTY, at);
        player.connection.markClientLoaded(); // otherwise the player is invulnerable
        player.setItemSlot(EquipmentSlot.CHEST, chest);
        return player;
    }
}
