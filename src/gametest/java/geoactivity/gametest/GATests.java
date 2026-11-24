package geoactivity.gametest;

import geoactivity.GeoActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registers every GeoActivity GameTest. Run them all with {@code ./gradlew runGameTestServer}.
 *
 * <p>Each test is a function plus a structure that defines the cleared test area:
 * {@code geoactivity:platform} is an empty 16×8×16 box.
 */
@EventBusSubscriber(modid = GeoActivity.MOD_ID)
public final class GATests {
    public static final String PLATFORM = "platform";

    private record TestDef(String name, String structure, int maxTicks, boolean skyAccess, Consumer<GameTestHelper> function) {}

    private static final List<TestDef> TESTS = new ArrayList<>();

    static {
        SmokeTests.register();
        ContentTests.register();
        MachineTests.register();
        MultiblockTests.register();
        EnergyTests.register();
        ToolTests.register();
        ArmorTests.register();
        JeiDataTests.register();
        BalanceTests.register();
    }

    private GATests() {}

    public static void test(String name, int maxTicks, Consumer<GameTestHelper> function) {
        test(name, PLATFORM, maxTicks, function);
    }

    public static void test(String name, String structure, int maxTicks, Consumer<GameTestHelper> function) {
        TESTS.add(new TestDef(name, structure, maxTicks, false, function));
    }

    /** A test whose area is open to the sky (Thermic Generator bricks need to see it). */
    public static void skyTest(String name, int maxTicks, Consumer<GameTestHelper> function) {
        TESTS.add(new TestDef(name, PLATFORM, maxTicks, true, function));
    }

    private static ResourceKey<Consumer<GameTestHelper>> functionKey(String name) {
        return ResourceKey.create(Registries.TEST_FUNCTION, GeoActivity.id(name));
    }

    @SubscribeEvent
    static void registerFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> {
            for (TestDef def : TESTS) {
                helper.register(GeoActivity.id(def.name()), def.function());
            }
        });
    }

    @SubscribeEvent
    static void registerTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(GeoActivity.id("default"));
        for (TestDef def : TESTS) {
            var data = new TestData<>(environment, net.minecraft.world.level.Level.OVERWORLD, GeoActivity.id(def.structure()),
                    def.maxTicks(), 0, true, net.minecraft.world.level.block.Rotation.NONE, false, 1, 1, def.skyAccess(), 0);
            event.registerTest(GeoActivity.id(def.name()), new FunctionGameTestInstance(functionKey(def.name()), data));
        }
    }
}
