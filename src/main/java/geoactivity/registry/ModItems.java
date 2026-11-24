package geoactivity.registry;

import geoactivity.GeoActivity;
import geoactivity.item.DropTeleportPerkItem;
import geoactivity.item.Element;
import geoactivity.item.TooltipItem;
import geoactivity.perk.ArmorPerk;
import geoactivity.perk.MachinePerk;
import geoactivity.perk.ToolPerk;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GeoActivity.MOD_ID);

    // Block items
    public static final DeferredItem<BlockItem> LIGNITE_ORE = ITEMS.registerSimpleBlockItem(ModBlocks.LIGNITE_ORE);
    public static final DeferredItem<BlockItem> BITUMINOUS_ORE = ITEMS.registerSimpleBlockItem(ModBlocks.BITUMINOUS_ORE);
    public static final DeferredItem<BlockItem> ANTHRACITE_ORE = ITEMS.registerSimpleBlockItem(ModBlocks.ANTHRACITE_ORE);
    public static final DeferredItem<BlockItem> HARDENED_BRICK = ITEMS.registerSimpleBlockItem(ModBlocks.HARDENED_BRICK);
    public static final DeferredItem<BlockItem> ADVANCED_HARDENED_BRICK = ITEMS.registerSimpleBlockItem(ModBlocks.ADVANCED_HARDENED_BRICK);
    public static final DeferredItem<BlockItem> THERMAL_HARDENED_BRICK = ITEMS.registerSimpleBlockItem(ModBlocks.THERMAL_HARDENED_BRICK);
    public static final DeferredItem<BlockItem> HARDENED_GLASS = ITEMS.registerSimpleBlockItem(ModBlocks.HARDENED_GLASS);
    public static final DeferredItem<BlockItem> GRAPHITE_BASE = ITEMS.registerSimpleBlockItem(ModBlocks.GRAPHITE_BASE);

    // Coal and carbon
    public static final DeferredItem<Item> LIGNITE_COAL = ITEMS.registerSimpleItem("lignite_coal");
    public static final DeferredItem<Item> BITUMINOUS_COAL = ITEMS.registerSimpleItem("bituminous_coal");
    public static final DeferredItem<Item> ANTHRACITE_COAL = ITEMS.registerSimpleItem("anthracite_coal");
    public static final DeferredItem<Item> GRAPHITE = ITEMS.registerSimpleItem("graphite");
    public static final DeferredItem<Item> CARBON_FIBER = ITEMS.registerSimpleItem("carbon_fiber");
    public static final DeferredItem<Item> CARBON_STICK = ITEMS.registerSimpleItem("carbon_stick");
    public static final DeferredItem<TooltipItem> CARBON_ORE_CUTTER = ITEMS.registerItem("carbon_ore_cutter",
            p -> new TooltipItem(p, false), p -> p.stacksTo(16));

    // Precious alloys
    public static final DeferredItem<Item> SMALL_PRECIOUS_ALLOY_INGOT = ITEMS.registerSimpleItem("small_precious_alloy_ingot",
            p -> p.rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> PRECIOUS_ALLOY_INGOT = ITEMS.registerSimpleItem("precious_alloy_ingot",
            p -> p.rarity(Rarity.RARE));
    public static final DeferredItem<Item> LARGE_PRECIOUS_ALLOY_INGOT = ITEMS.registerSimpleItem("large_precious_alloy_ingot",
            p -> p.rarity(Rarity.EPIC));

    // Perks
    public static final Map<ToolPerk, DeferredItem<? extends Item>> TOOL_PERKS = registerToolPerks();
    public static final Map<MachinePerk, DeferredItem<TooltipItem>> MACHINE_PERKS = registerMachinePerks();
    public static final Map<ArmorPerk, DeferredItem<TooltipItem>> ARMOR_PERKS = registerArmorPerks();

    // Element containers (own creative tab)
    public static final DeferredItem<Item> EMPTY_ELEMENT_CONTAINER = ITEMS.registerSimpleItem("empty_element_container",
            p -> p.stacksTo(32));
    public static final Map<Element, DeferredItem<Item>> ELEMENT_CONTAINERS = registerElements();

    private ModItems() {}

    public static Item toolPerk(ToolPerk perk) {
        return TOOL_PERKS.get(perk).get();
    }

    public static Item machinePerk(MachinePerk perk) {
        return MACHINE_PERKS.get(perk).get();
    }

    public static Item armorPerk(ArmorPerk perk) {
        return ARMOR_PERKS.get(perk).get();
    }

    public static Item element(Element element) {
        return ELEMENT_CONTAINERS.get(element).get();
    }

    private static Map<ToolPerk, DeferredItem<? extends Item>> registerToolPerks() {
        Map<ToolPerk, DeferredItem<? extends Item>> map = new EnumMap<>(ToolPerk.class);
        for (ToolPerk perk : ToolPerk.values()) {
            map.put(perk, perk == ToolPerk.DROP_TELEPORT
                    ? ITEMS.registerItem(perk.itemId(), DropTeleportPerkItem::new, p -> p.stacksTo(1))
                    : ITEMS.registerItem(perk.itemId(), p -> new TooltipItem(p, true), p -> p.stacksTo(1)));
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map<MachinePerk, DeferredItem<TooltipItem>> registerMachinePerks() {
        Map<MachinePerk, DeferredItem<TooltipItem>> map = new EnumMap<>(MachinePerk.class);
        for (MachinePerk perk : MachinePerk.values()) {
            map.put(perk, ITEMS.registerItem(perk.itemId(), p -> new TooltipItem(p, true), p -> p.stacksTo(1)));
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map<ArmorPerk, DeferredItem<TooltipItem>> registerArmorPerks() {
        Map<ArmorPerk, DeferredItem<TooltipItem>> map = new EnumMap<>(ArmorPerk.class);
        for (ArmorPerk perk : ArmorPerk.values()) {
            map.put(perk, ITEMS.registerItem(perk.itemId(), p -> new TooltipItem(p, true), p -> p.stacksTo(1)));
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map<Element, DeferredItem<Item>> registerElements() {
        Map<Element, DeferredItem<Item>> map = new EnumMap<>(Element.class);
        for (Element element : Element.values()) {
            map.put(element, ITEMS.registerSimpleItem(element.itemId(), p -> p.stacksTo(32)));
        }
        return Collections.unmodifiableMap(map);
    }

    /** Block item for a machine block, with the shift-for-info tooltip from the lang file. */
    static DeferredItem<BlockItem> machineItem(Supplier<? extends Block> block, String name) {
        return ITEMS.registerItem(name, p -> new geoactivity.item.InfoBlockItem(block.get(), p), p -> p.useBlockDescriptionPrefix());
    }
}
