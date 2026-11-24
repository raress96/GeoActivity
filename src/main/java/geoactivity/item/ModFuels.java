package geoactivity.item;

import geoactivity.GeoActivity;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootPredicates;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConditionalValue;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

/**
 * Vanilla furnace burn times of the mod's coals (3.0.0 balance pass; 1.10.2 burned them only in its own machines).
 * 1.5×, 2× and 3× coal, so they stay below a coal block (10×) and a lava bucket (12.5×). The mod's machines and tools
 * keep their own, much higher fuel values. Like vanilla, smokers and blast furnaces burn them twice as fast.
 */
public final class ModFuels {
    public static final ResourceKey<ContextIntProvider> LIGNITE_COAL = key("cooking/time_lignite_coal");
    public static final ResourceKey<ContextIntProvider> BITUMINOUS_COAL = key("cooking/time_bituminous_coal");
    public static final ResourceKey<ContextIntProvider> ANTHRACITE_COAL = key("cooking/time_anthracite_coal");

    public static final int LIGNITE_TICKS = 2400;
    public static final int BITUMINOUS_TICKS = 3200;
    public static final int ANTHRACITE_TICKS = 4800;

    private ModFuels() {}

    private static ResourceKey<ContextIntProvider> key(String path) {
        return ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, GeoActivity.id(path));
    }

    /** Datagen: same shape as vanilla's {@code cooking/time_*} providers. */
    public static void bootstrap(BootstrapContext<ContextIntProvider> context) {
        HolderGetter<ContextIntProvider> providers = context.lookup(Registries.CONTEXT_INT_PROVIDER);
        HolderGetter<LootItemCondition> predicates = context.lookup(Registries.PREDICATE);
        Holder<ContextIntProvider> divisor = Holder.direct(new ConditionalValue(
                predicates.getOrThrow(LootPredicates.FAST_FURNACE),
                providers.getOrThrow(ContextIntProviders.COOKING_FAST_BURN_TIME_REDUCTION_FACTOR),
                providers.getOrThrow(ContextIntProviders.COOKING_NORMAL_BURN_TIME_REDUCTION_FACTOR)));
        context.register(LIGNITE_COAL, ContextIntProviders.div(ContextIntProviders.exactly(LIGNITE_TICKS), divisor).value());
        context.register(BITUMINOUS_COAL, ContextIntProviders.div(ContextIntProviders.exactly(BITUMINOUS_TICKS), divisor).value());
        context.register(ANTHRACITE_COAL, ContextIntProviders.div(ContextIntProviders.exactly(ANTHRACITE_TICKS), divisor).value());
    }
}
