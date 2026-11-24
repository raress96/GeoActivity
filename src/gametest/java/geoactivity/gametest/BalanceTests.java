package geoactivity.gametest;

import geoactivity.item.ModFuels;
import geoactivity.perk.PerkData;
import geoactivity.perk.ToolPerk;
import geoactivity.registry.ModDataComponents;
import geoactivity.registry.ModItems;
import geoactivity.tool.ToolLogic;
import geoactivity.tool.ToolProfiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

/** 3.0.0 balance pass: the 1.10.2 quirks it fixed, and the coals as vanilla furnace fuel. */
final class BalanceTests {
    private BalanceTests() {}

    static void register() {
        GATests.test("redstone_tool_burns_every_fuel_slot", 5, helper -> {
            int size = ToolProfiles.REDSTONE_MINER.inventorySize();
            ItemStack miner = new ItemStack(ModItems.REDSTONE_MINER.get());
            miner.setDamageValue(998);
            NonNullList<ItemStack> inv = NonNullList.withSize(size, ItemStack.EMPTY);
            inv.set(1, new ItemStack(ModItems.LIGNITE_COAL.get(), 3));
            inv.set(3, new ItemStack(ModItems.LIGNITE_COAL.get(), 2));
            ToolLogic.setInventory(miner, inv);
            ToolLogic.refuel(miner, ToolProfiles.REDSTONE_MINER);
            helper.assertValueEqual(miner.getDamageValue(), 998 - 5 * 130, "all 5 lignite burned, 130 each");
            NonNullList<ItemStack> left = ToolLogic.inventory(miner, size);
            helper.assertTrue(left.get(1).isEmpty() && left.get(3).isEmpty(), "fuel slots 1 and 3 used up: " + left);
            helper.succeed();
        });

        GATests.test("advanced_pickaxe_and_shovel_capitator_mine_a_column", 20, helper -> {
            column(helper, 2, Blocks.COBBLESTONE, Blocks.STONE);
            column(helper, 7, Blocks.CLAY, Blocks.DIRT);
            mine(helper, ModItems.ADVANCED_PICKAXE.get(), new BlockPos(2, 4, 3), new BlockPos(1, 1, 5));
            mine(helper, ModItems.ADVANCED_SHOVEL.get(), new BlockPos(7, 4, 3), new BlockPos(6, 1, 5));
            for (int x : new int[] {2, 7}) {
                for (int y = 2; y <= 6; y++) {
                    helper.assertBlockPresent(Blocks.AIR, new BlockPos(x, y, 3));
                }
            }
            helper.assertBlockPresent(Blocks.COBBLESTONE, new BlockPos(2, 1, 3));
            helper.assertBlockPresent(Blocks.COBBLESTONE, new BlockPos(2, 7, 3));
            helper.assertBlockPresent(Blocks.CLAY, new BlockPos(7, 1, 3));
            helper.assertBlockPresent(Blocks.CLAY, new BlockPos(7, 7, 3));
            helper.succeed();
        });

        GATests.test("armor_bypass_perk_ignores_a_third_of_armor", 20, helper -> {
            Zombie plain = diamondZombie(helper, new BlockPos(2, 1, 2));
            Zombie target = diamondZombie(helper, new BlockPos(7, 1, 2));
            ItemStack perked = new ItemStack(ModItems.ADVANCED_SWORD.get());
            perked.set(ModDataComponents.PERKS, PerkData.of(ToolPerk.ARMOR_BYPASS.key(), 1));
            ServerPlayer player = ToolTests.survivalPlayer(helper, new ItemStack(ModItems.ADVANCED_SWORD.get()), new BlockPos(4, 1, 6));
            // equipment attributes apply on the entity's next tick
            helper.runAtTickTime(2, () -> {
                float plainLoss = hit(helper, plain, player);
                player.setItemInHand(InteractionHand.MAIN_HAND, perked);
                float bypassLoss = hit(helper, target, player);
                // diamond armor takes 7.8 of a 10 hit (2.2 left); with the perk it takes 2/3 of that (4.8 left)
                helper.assertTrue(Math.abs(plainLoss - 2.2F) < 0.05F, "plain hit through diamond armor: " + plainLoss);
                helper.assertTrue(Math.abs(bypassLoss - 4.8F) < 0.05F, "armor bypass hit: " + bypassLoss);
                helper.succeed();
            });
        });

        GATests.test("new_coals_burn_in_vanilla_furnaces", 20, helper -> {
            Item[] coals = {ModItems.LIGNITE_COAL.get(), ModItems.BITUMINOUS_COAL.get(), ModItems.ANTHRACITE_COAL.get()};
            int[] ticks = {ModFuels.LIGNITE_TICKS, ModFuels.BITUMINOUS_TICKS, ModFuels.ANTHRACITE_TICKS};
            for (int i = 0; i < coals.length; i++) {
                light(helper, new BlockPos(1 + 3 * i, 1, 2), Blocks.FURNACE, coals[i]);
                light(helper, new BlockPos(1 + 3 * i, 1, 6), Blocks.BLAST_FURNACE, coals[i]);
            }
            helper.runAtTickTime(3, () -> {
                for (int i = 0; i < coals.length; i++) {
                    helper.assertValueEqual(litTotal(helper, new BlockPos(1 + 3 * i, 1, 2)), ticks[i], "furnace burn time of " + coals[i]);
                    helper.assertValueEqual(litTotal(helper, new BlockPos(1 + 3 * i, 1, 6)), ticks[i] / 2, "blast furnace burn time of " + coals[i]);
                }
                helper.succeed();
            });
        });

        GATests.test("anthracite_coal_is_uncommon", 5, helper -> {
            helper.assertValueEqual(new ItemStack(ModItems.ANTHRACITE_COAL.get()).get(DataComponents.RARITY), Rarity.UNCOMMON, "anthracite");
            helper.assertValueEqual(new ItemStack(ModItems.LIGNITE_COAL.get()).get(DataComponents.RARITY), Rarity.COMMON, "lignite");
            helper.succeed();
        });
    }

    /** {@code edge} at y 1 and 7, {@code body} at y 2..6, at (x, y, 3). */
    private static void column(GameTestHelper helper, int x, Block edge, Block body) {
        helper.setBlock(new BlockPos(x, 1, 3), edge);
        for (int y = 2; y <= 6; y++) {
            helper.setBlock(new BlockPos(x, y, 3), body);
        }
        helper.setBlock(new BlockPos(x, 7, 3), edge);
    }

    private static void mine(GameTestHelper helper, Item tool, BlockPos target, BlockPos playerAt) {
        ItemStack stack = new ItemStack(tool);
        stack.set(ModDataComponents.PERKS, PerkData.of(ToolPerk.CAPITATOR.key(), 1));
        ServerPlayer player = ToolTests.survivalPlayer(helper, stack, playerAt);
        player.gameMode.destroyBlock(helper.absolutePos(target));
    }

    private static Zombie diamondZombie(GameTestHelper helper, BlockPos pos) {
        Zombie zombie = helper.spawnWithNoFreeWill(EntityTypes.HUSK, pos); // doesn't burn in daylight
        zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
        zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
        zombie.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
        zombie.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
        return zombie;
    }

    /** A 10-damage melee hit from {@code player}; returns the health lost. */
    private static float hit(GameTestHelper helper, Zombie zombie, ServerPlayer player) {
        float before = zombie.getHealth();
        zombie.hurtServer(helper.getLevel(), helper.getLevel().damageSources().playerAttack(player), 10.0F);
        return before - zombie.getHealth();
    }

    private static void light(GameTestHelper helper, BlockPos pos, Block furnace, Item coal) {
        helper.setBlock(pos, furnace);
        AbstractFurnaceBlockEntity be = helper.getBlockEntity(pos, AbstractFurnaceBlockEntity.class);
        be.setItem(0, new ItemStack(Items.RAW_IRON, 4));
        be.setItem(1, new ItemStack(coal));
    }

    private static int litTotal(GameTestHelper helper, BlockPos pos) {
        AbstractFurnaceBlockEntity be = helper.getBlockEntity(pos, AbstractFurnaceBlockEntity.class);
        return be.saveWithoutMetadata(helper.getLevel().registryAccess()).getIntOr("lit_total_time", -1);
    }
}
