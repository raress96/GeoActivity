package geoactivity.gametest;

import geoactivity.machine.energy.GeothermalGeneratorBlockEntity;
import geoactivity.machine.energy.ThermicGeneratorBlockEntity;
import geoactivity.machine.energy.ThermicMelterBlockEntity;
import geoactivity.machine.energy.ToolChargerBlockEntity;
import geoactivity.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/** Phase 5: Thermic Generator, Thermic Melter, Geothermal Generator, Tool Charger. */
final class EnergyTests {
    private EnergyTests() {}

    static void register() {
        GATests.skyTest("thermic_generator_feeds_block_below_in_daytime", 100, helper -> {
            // tests in a batch share the world clock, so hold it at noon every tick
            helper.setTime(6000);
            helper.onEachTick(() -> helper.setTime(6000));
            BlockPos gen = new BlockPos(4, 3, 4);
            helper.setBlock(gen, ModBlocks.THERMIC_GENERATOR.get());
            helper.setBlock(gen.below(), ModBlocks.TOOL_CHARGER.get());
            for (Direction dir : new Direction[] {Direction.NORTH, Direction.EAST, Direction.UP}) {
                helper.setBlock(gen.relative(dir), ModBlocks.THERMAL_HARDENED_BRICK.get());
            }
            var tg = helper.getBlockEntity(gen, ThermicGeneratorBlockEntity.class);
            var tc = helper.getBlockEntity(gen.below(), ToolChargerBlockEntity.class);
            helper.succeedWhen(() -> {
                helper.assertValueEqual(ThermicGeneratorBlockEntity.countBricks(helper.getLevel(), helper.absolutePos(gen)), 3, "bricks");
                helper.assertTrue(tc.energyStored() >= 60 * 20, "tool charger received power: " + tc.energyStored());
                helper.assertValueEqual(tg.energyStored(), 0, "generator pushed everything down");
            });
        });

        GATests.test("thermic_melter_turns_obsidian_into_lava", 450, helper -> {
            BlockPos melter = new BlockPos(5, 3, 5);
            helper.setBlock(melter, ModBlocks.THERMIC_MELTER.get());
            helper.setBlock(melter.offset(2, 0, 0), Blocks.OBSIDIAN);
            helper.setBlock(melter.offset(-2, -1, 1), Blocks.OBSIDIAN);
            var tm = helper.getBlockEntity(melter, ThermicMelterBlockEntity.class);
            // fill it through the capability, 500 FE/t max
            var level = helper.getLevel();
            var cap = level.getCapability(Capabilities.Energy.BLOCK, helper.absolutePos(melter), Direction.UP);
            try (var tx = Transaction.openRoot()) {
                helper.assertValueEqual(cap.insert(10_000, tx), ThermicMelterBlockEntity.MAX_INPUT, "insert limit");
                tx.commit();
            }
            tm.energy().add(120_000);
            helper.succeedWhen(() -> {
                helper.assertBlockPresent(Blocks.LAVA, melter.offset(2, 0, 0));
                helper.assertBlockPresent(Blocks.LAVA, melter.offset(-2, -1, 1));
                helper.assertTrue(tm.energyStored() < 50_000, "2 × 50k used: " + tm.energyStored());
            });
        });

        GATests.test("geothermal_generator_counts_lava", 700, helper -> {
            BlockPos gen = new BlockPos(5, 2, 5);
            // 2×2 lava basin east of the generator, walled in so it can't flow
            for (int x = 4; x <= 8; x++) {
                for (int z = 3; z <= 7; z++) {
                    helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
                    helper.setBlock(new BlockPos(x, 2, z), Blocks.STONE);
                }
            }
            helper.setBlock(gen, ModBlocks.GEOTHERMAL_GENERATOR.get());
            BlockPos[] lava = {gen.east(), gen.east(2), gen.east().south(), gen.east(2).south()};
            for (BlockPos pos : lava) {
                helper.setBlock(pos, Blocks.LAVA);
            }
            var gg = helper.getBlockEntity(gen, GeothermalGeneratorBlockEntity.class);
            helper.succeedWhen(() -> {
                helper.assertValueEqual(gg.lavaBlocks(), 4, "lava sources found");
                // the test world is low, so efficiency is 100%: 4 × 4 = 16 FE/t
                helper.assertValueEqual(gg.perTick(), 16, "FE/t");
                helper.assertTrue(gg.energyStored() > 0, "generating");
            });
        });
    }
}
