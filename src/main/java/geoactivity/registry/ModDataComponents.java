package geoactivity.registry;

import geoactivity.GeoActivity;
import geoactivity.perk.PerkData;
import java.util.function.Supplier;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, GeoActivity.MOD_ID);

    /** Inventory that drops are sent to (Drop Teleportation perk). */
    public static final Supplier<DataComponentType<GlobalPos>> DROP_TARGET = COMPONENTS.registerComponentType("drop_target",
            b -> b.persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC));

    /** Perks installed on a tool or armor piece. */
    public static final Supplier<DataComponentType<PerkData>> PERKS = COMPONENTS.registerComponentType("perks",
            b -> b.persistent(PerkData.CODEC).networkSynchronized(PerkData.STREAM_CODEC));

    private ModDataComponents() {}
}
