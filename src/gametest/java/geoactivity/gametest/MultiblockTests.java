package geoactivity.gametest;

import geoactivity.machine.orerefiner.AdvancedOreRefinerBlock;
import geoactivity.machine.orerefiner.AdvancedOreRefinerBlockEntity;
import geoactivity.machine.orerefiner.AdvancedOreRefinerStructure;
import geoactivity.machine.refiner.AdvancedCoalRefinerBlock;
import geoactivity.machine.refiner.AdvancedCoalRefinerBlockEntity;
import geoactivity.perk.MachinePerk;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;

/** Phase 4: Advanced Coal Refiner (2×2×2) and Advanced Ore Refiner (28 blocks). */
final class MultiblockTests {
    private MultiblockTests() {}

    static void register() {
        GATests.test("acr_forms_when_last_block_placed", 20, helper -> {
            BlockPos origin = new BlockPos(3, 1, 3);
            // 7 blocks set directly, the 8th placed by a player like in survival
            BlockPos last = origin.offset(1, 1, 1);
            for (int i = 0; i < 8; i++) {
                BlockPos pos = origin.offset(i & 1, (i >> 1) & 1, (i >> 2) & 1);
                if (!pos.equals(last)) {
                    helper.setBlock(pos, ModBlocks.ADVANCED_COAL_REFINER.get());
                }
            }
            var player = helper.makeMockPlayer(GameType.SURVIVAL);
            ItemStack stack = new ItemStack(ModItems.ADVANCED_COAL_REFINER.get());
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            helper.placeAt(player, stack, last.below(), Direction.UP);
            helper.assertBlockPresent(ModBlocks.ADVANCED_COAL_REFINER.get(), last);
            for (int i = 0; i < 8; i++) {
                BlockPos pos = origin.offset(i & 1, (i >> 1) & 1, (i >> 2) & 1);
                helper.assertBlockProperty(pos, AdvancedCoalRefinerBlock.FORMED, true);
            }
            var core = helper.getBlockEntity(last, AdvancedCoalRefinerBlockEntity.class);
            helper.assertTrue(core.isCore(), "last placed block is the core");
            var member = helper.getBlockEntity(origin, AdvancedCoalRefinerBlockEntity.class);
            member.setItem(AdvancedCoalRefinerBlockEntity.FUEL, new ItemStack(Items.COAL, 5));
            helper.assertValueEqual(core.getItem(AdvancedCoalRefinerBlockEntity.FUEL).getCount(), 5, "members share the core inventory");
            helper.assertTrue(helper.getLevel().getCapability(Capabilities.Item.BLOCK, helper.absolutePos(origin), Direction.UP) != null,
                    "formed members expose the inventory");
            helper.succeed();
        });

        GATests.test("acr_at_coordinate_zero_relative_still_forms", 10, helper -> {
            // 1.10.2 used 0 as "no core" and failed whenever a coordinate was 0; the port uses real positions
            BlockPos origin = new BlockPos(0, 1, 0);
            for (int i = 0; i < 8; i++) {
                helper.setBlock(origin.offset(i & 1, (i >> 1) & 1, (i >> 2) & 1), ModBlocks.ADVANCED_COAL_REFINER.get());
            }
            helper.assertTrue(AdvancedCoalRefinerBlockEntity.tryForm(helper.getLevel(), helper.absolutePos(origin)), "formed");
            helper.succeed();
        });

        GATests.test("acr_extra_neighbour_blocks_forming", 10, helper -> {
            BlockPos origin = new BlockPos(3, 1, 3);
            for (int i = 0; i < 8; i++) {
                helper.setBlock(origin.offset(i & 1, (i >> 1) & 1, (i >> 2) & 1), ModBlocks.ADVANCED_COAL_REFINER.get());
            }
            helper.setBlock(origin.west(), ModBlocks.ADVANCED_COAL_REFINER.get());
            helper.assertFalse(AdvancedCoalRefinerBlockEntity.tryForm(helper.getLevel(), helper.absolutePos(origin)),
                    "placed block with 4 ACR neighbours");
            helper.succeed();
        });

        GATests.test("acr_refines_two_lanes_with_speed_perks", 200, helper -> {
            BlockPos origin = new BlockPos(3, 1, 3);
            var core = formAcr(helper, origin);
            core.setItem(AdvancedCoalRefinerBlockEntity.PERK_1, new ItemStack(ModItems.machinePerk(MachinePerk.SPEED)));
            core.setItem(AdvancedCoalRefinerBlockEntity.PERK_2, new ItemStack(ModItems.machinePerk(MachinePerk.SPEED)));
            core.setItem(AdvancedCoalRefinerBlockEntity.FUEL, new ItemStack(ModItems.LIGNITE_COAL.get()));
            core.setItem(AdvancedCoalRefinerBlockEntity.IN_1, new ItemStack(Items.COAL));
            core.setItem(AdvancedCoalRefinerBlockEntity.IN_2, new ItemStack(ModItems.LIGNITE_COAL.get()));
            // 2 speed perks: 200 × (1 − 0.5) = 100 ticks
            helper.runAtTickTime(90, () -> helper.assertTrue(core.getItem(AdvancedCoalRefinerBlockEntity.OUT_1).isEmpty(), "not before 100"));
            helper.succeedWhen(() -> {
                helper.assertTrue(core.getItem(AdvancedCoalRefinerBlockEntity.OUT_1).is(ModItems.GRAPHITE.get()), "lane 1");
                helper.assertValueEqual(core.getItem(AdvancedCoalRefinerBlockEntity.OUT_2).getCount(), 8, "lane 2: 8 graphite");
            });
        });

        GATests.test("acr_breaking_a_block_unforms_and_drops_inventory", 20, helper -> {
            BlockPos origin = new BlockPos(3, 1, 3);
            var core = formAcr(helper, origin);
            core.setItem(AdvancedCoalRefinerBlockEntity.FUEL, new ItemStack(Items.COAL, 7));
            helper.getLevel().destroyBlock(helper.absolutePos(origin), true);
            helper.assertBlockProperty(origin.offset(1, 1, 1), AdvancedCoalRefinerBlock.FORMED, false);
            helper.assertBlockProperty(origin.offset(1, 0, 0), AdvancedCoalRefinerBlock.FORMED, false);
            helper.assertItemEntityPresent(Items.COAL, origin, 2.0);
            helper.assertItemEntityPresent(ModItems.ADVANCED_COAL_REFINER.get(), origin, 2.0);
            helper.assertTrue(core.getItem(AdvancedCoalRefinerBlockEntity.FUEL).isEmpty(), "core emptied");
            helper.succeed();
        });

        GATests.test("aor_forms_refines_and_restores", 900, helper -> {
            BlockPos core = new BlockPos(6, 2, 3);
            buildAor(helper, core, Direction.NORTH);
            var level = helper.getLevel();
            helper.setBlock(core, ModBlocks.ADVANCED_ORE_REFINER.get(), Direction.NORTH);
            helper.assertTrue(AdvancedOreRefinerBlockEntity.tryForm(level, helper.absolutePos(core), Direction.NORTH), "formed");
            var be = helper.getBlockEntity(core, AdvancedOreRefinerBlockEntity.class);
            helper.assertValueEqual(be.members().size(), 29, "core + 28 structure blocks");
            BlockPos brick = core.south().above(2);
            helper.assertBlockProperty(brick, AdvancedOreRefinerBlock.CORE, false);
            be.setItem(AdvancedOreRefinerBlockEntity.ORE_FIRST, new ItemStack(Items.IRON_ORE, 2));
            be.setItem(AdvancedOreRefinerBlockEntity.FUEL, new ItemStack(ModItems.LIGNITE_COAL.get(), 2));
            be.setItem(AdvancedOreRefinerBlockEntity.WOOD, new ItemStack(Items.OAK_LOG, 8));
            helper.succeedWhen(() -> {
                ItemStack out = be.getItem(AdvancedOreRefinerBlockEntity.OUT_FIRST);
                helper.assertTrue(out.is(Items.IRON_INGOT) && out.getCount() >= 4 && out.getCount() <= 6,
                        "2 ores -> 4..6 ingots, got " + out);
                helper.assertTrue(be.getItem(AdvancedOreRefinerBlockEntity.WOOD).isEmpty(), "8 logs used for heating");
                // break a brick: everything goes back
                level.destroyBlock(helper.absolutePos(brick), true);
                helper.assertBlockPresent(ModBlocks.ADVANCED_HARDENED_BRICK.get(), core.south(1).below());
                helper.assertBlockPresent(ModBlocks.ADVANCED_COAL_REFINER.get(), core.relative(Direction.SOUTH, 2).relative(Direction.EAST, 2));
                helper.assertBlockProperty(core, AdvancedOreRefinerBlock.FORMED, false);
                helper.assertItemEntityPresent(ModItems.ADVANCED_HARDENED_BRICK.get(), brick, 2.0);
                helper.assertItemEntityPresent(Items.IRON_INGOT, brick, 2.0);
            });
        });

        GATests.test("aor_incomplete_structure_does_not_form", 10, helper -> {
            BlockPos core = new BlockPos(6, 2, 3);
            buildAor(helper, core, Direction.NORTH);
            helper.setBlock(core.relative(Direction.SOUTH, 4), Blocks.STONE);
            helper.assertTrue(AdvancedOreRefinerStructure.find(helper.getLevel(), helper.absolutePos(core), Direction.NORTH) == null,
                    "missing back brick");
            helper.succeed();
        });
    }

    static AdvancedCoalRefinerBlockEntity formAcr(GameTestHelper helper, BlockPos origin) {
        for (int i = 0; i < 8; i++) {
            helper.setBlock(origin.offset(i & 1, (i >> 1) & 1, (i >> 2) & 1), ModBlocks.ADVANCED_COAL_REFINER.get());
        }
        BlockPos coreAbs = helper.absolutePos(origin.offset(1, 1, 1));
        helper.assertTrue(AdvancedCoalRefinerBlockEntity.tryForm(helper.getLevel(), coreAbs), "ACR formed");
        return (AdvancedCoalRefinerBlockEntity) helper.getLevel().getBlockEntity(coreAbs);
    }

    /** Builds the 28-block structure behind {@code core} (relative coordinates). */
    static void buildAor(GameTestHelper helper, BlockPos core, Direction front) {
        Direction back = front.getOpposite();
        Direction side = front.getClockWise();
        var brick = ModBlocks.ADVANCED_HARDENED_BRICK.get();
        for (int y = -1; y <= 2; y++) {
            helper.setBlock(core.relative(back, 1).above(y), brick);
            helper.setBlock(core.relative(back, 4).above(y), brick);
        }
        for (int depth = 2; depth <= 3; depth++) {
            helper.setBlock(core.relative(back, depth).below(), brick);
            helper.setBlock(core.relative(back, depth).above(2), brick);
            for (int y = 0; y <= 1; y++) {
                for (int lateral : new int[] {-2, -1, 1, 2}) {
                    helper.setBlock(core.relative(back, depth).relative(side, lateral).above(y), ModBlocks.ADVANCED_COAL_REFINER.get());
                }
            }
        }
    }
}
