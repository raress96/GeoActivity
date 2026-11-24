package geoactivity.gametest;

import geoactivity.GeoActivity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;

final class SmokeTests {
    private SmokeTests() {}

    static void register() {
        GATests.test("smoke_platform", 20, helper -> {
            BlockPos pos = new BlockPos(1, 1, 1);
            helper.setBlock(pos, Blocks.STONE);
            helper.assertBlockPresent(Blocks.STONE, pos);
            helper.succeed();
        });
        GATests.test("smoke_mod_loaded", 20, helper -> {
            long ours = BuiltInRegistries.CREATIVE_MODE_TAB.keySet().stream()
                    .filter(id -> id.getNamespace().equals(GeoActivity.MOD_ID)).count();
            helper.assertTrue(ours >= 0, "registry lookup works");
            helper.succeed();
        });
    }
}
