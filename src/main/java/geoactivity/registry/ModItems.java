package geoactivity.registry;

import geoactivity.GeoActivity;
import geoactivity.item.DropTeleportPerkItem;
import geoactivity.armor.AdvancedArmorItem;
import geoactivity.armor.ModArmorMaterials;
import geoactivity.item.Element;
import geoactivity.item.InfoBlockItem;
import geoactivity.tool.AutoStoneBuilderItem;
import geoactivity.tool.PerkTool;
import geoactivity.tool.ToolProfile;
import geoactivity.tool.ToolProfiles;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorType;
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
import geoactivity.item.ModFuels;
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

    // Machines
    public static final DeferredItem<BlockItem> COAL_REFINER = machineItem(ModBlocks.COAL_REFINER, "coal_refiner");
    public static final DeferredItem<BlockItem> CRAFTING_MACHINE = machineItem(ModBlocks.CRAFTING_MACHINE, "crafting_machine");
    public static final DeferredItem<BlockItem> ATOM_EXTRACTOR = machineItem(ModBlocks.ATOM_EXTRACTOR, "atom_extractor");
    public static final DeferredItem<BlockItem> CHEMISTRY_MACHINE = machineItem(ModBlocks.CHEMISTRY_MACHINE, "chemistry_machine");
    public static final DeferredItem<BlockItem> ADVANCED_COAL_REFINER = machineItem(ModBlocks.ADVANCED_COAL_REFINER, "advanced_coal_refiner");
    public static final DeferredItem<BlockItem> ADVANCED_ORE_REFINER = machineItem(ModBlocks.ADVANCED_ORE_REFINER, "advanced_ore_refiner");
    public static final DeferredItem<BlockItem> THERMIC_GENERATOR = machineItem(ModBlocks.THERMIC_GENERATOR, "thermic_generator");
    public static final DeferredItem<BlockItem> TOOL_CHARGER = machineItem(ModBlocks.TOOL_CHARGER, "tool_charger");
    public static final DeferredItem<BlockItem> GEOTHERMAL_GENERATOR = machineItem(ModBlocks.GEOTHERMAL_GENERATOR, "geothermal_generator");
    public static final DeferredItem<BlockItem> THERMIC_MELTER = machineItem(ModBlocks.THERMIC_MELTER, "thermic_melter");

    // Coal and carbon
    public static final DeferredItem<Item> LIGNITE_COAL = ITEMS.registerSimpleItem("lignite_coal",
            p -> p.cookingFuel(ModFuels.LIGNITE_COAL));
    public static final DeferredItem<Item> BITUMINOUS_COAL = ITEMS.registerSimpleItem("bituminous_coal",
            p -> p.cookingFuel(ModFuels.BITUMINOUS_COAL));
    // uncommon: 1.10.2 meant this, but its name check never matched
    public static final DeferredItem<Item> ANTHRACITE_COAL = ITEMS.registerSimpleItem("anthracite_coal",
            p -> p.cookingFuel(ModFuels.ANTHRACITE_COAL).rarity(Rarity.UNCOMMON));
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

    // Tools (1.10.2 Reinforced material: harvest level 2 = iron tier, 500 uses, speed 8, damage 3, enchantability 18)
    public static final ToolMaterial REINFORCED = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 500, 8.0F, 3.0F, 18,
            ModTags.Items.REINFORCED_TOOL_MATERIALS);
    public static final DeferredItem<Item> REINFORCED_SWORD = ITEMS.registerItem("reinforced_sword", Item::new, p -> p.sword(REINFORCED, 3.0F, -2.4F));
    public static final DeferredItem<Item> REINFORCED_PICKAXE = ITEMS.registerItem("reinforced_pickaxe", Item::new, p -> p.pickaxe(REINFORCED, 1.0F, -2.8F));
    public static final DeferredItem<Item> REINFORCED_AXE = ITEMS.registerItem("reinforced_axe", Item::new, p -> p.axe(REINFORCED, 2.0F, -3.1F));
    public static final DeferredItem<Item> REINFORCED_SHOVEL = ITEMS.registerItem("reinforced_shovel", Item::new, p -> p.shovel(REINFORCED, 1.5F, -3.0F));
    public static final DeferredItem<Item> REINFORCED_HOE = ITEMS.registerItem("reinforced_hoe", Item::new, p -> p.hoe(REINFORCED, -3.0F, -1.0F));
    public static final DeferredItem<PerkTool> REINFORCED_MINER = tool("reinforced_miner", ToolProfiles.REINFORCED_MINER, false);
    public static final DeferredItem<PerkTool> ADVANCED_SWORD = tool("advanced_sword", ToolProfiles.ADVANCED_SWORD, true);
    public static final DeferredItem<PerkTool> ADVANCED_PICKAXE = tool("advanced_pickaxe", ToolProfiles.ADVANCED_PICKAXE, true);
    public static final DeferredItem<PerkTool> ADVANCED_AXE = tool("advanced_axe", ToolProfiles.ADVANCED_AXE, true);
    public static final DeferredItem<PerkTool> ADVANCED_SHOVEL = tool("advanced_shovel", ToolProfiles.ADVANCED_SHOVEL, true);
    public static final DeferredItem<PerkTool> REDSTONE_MINER = tool("redstone_miner", ToolProfiles.REDSTONE_MINER, false);
    public static final DeferredItem<PerkTool> REDSTONE_BATTLE_AXE = tool("redstone_battle_axe", ToolProfiles.REDSTONE_BATTLE_AXE, false);
    public static final DeferredItem<PerkTool> REDSTONE_BATTLE_MINER = tool("redstone_battle_miner", ToolProfiles.REDSTONE_BATTLE_MINER, false);
    public static final DeferredItem<PerkTool> ADVANCED_REDSTONE_BATTLE_MINER = tool("advanced_redstone_battle_miner",
            ToolProfiles.ADVANCED_REDSTONE_BATTLE_MINER, false);
    // Armor
    public static final DeferredItem<Item> REINFORCED_HELMET = reinforcedArmor("reinforced_helmet", ArmorType.HELMET);
    public static final DeferredItem<Item> REINFORCED_CHESTPLATE = reinforcedArmor("reinforced_chestplate", ArmorType.CHESTPLATE);
    public static final DeferredItem<Item> REINFORCED_LEGGINGS = reinforcedArmor("reinforced_leggings", ArmorType.LEGGINGS);
    public static final DeferredItem<Item> REINFORCED_BOOTS = reinforcedArmor("reinforced_boots", ArmorType.BOOTS);
    public static final DeferredItem<AdvancedArmorItem> ADVANCED_HELMET = advancedArmor("advanced_helmet", ArmorType.HELMET);
    public static final DeferredItem<AdvancedArmorItem> ADVANCED_CHESTPLATE = advancedArmor("advanced_chestplate", ArmorType.CHESTPLATE);
    public static final DeferredItem<AdvancedArmorItem> ADVANCED_LEGGINGS = advancedArmor("advanced_leggings", ArmorType.LEGGINGS);
    public static final DeferredItem<AdvancedArmorItem> ADVANCED_BOOTS = advancedArmor("advanced_boots", ArmorType.BOOTS);

    public static final DeferredItem<AutoStoneBuilderItem> AUTO_STONE_BUILDER = ITEMS.registerItem("auto_stone_builder",
            AutoStoneBuilderItem::new, p -> p.durability(1000));

    private ModItems() {}

    private static DeferredItem<Item> reinforcedArmor(String name, ArmorType type) {
        return ITEMS.registerItem(name, Item::new, p -> p.humanoidArmor(ModArmorMaterials.REINFORCED, type));
    }

    private static DeferredItem<AdvancedArmorItem> advancedArmor(String name, ArmorType type) {
        return ITEMS.registerItem(name, p -> new AdvancedArmorItem(p, type), p -> AdvancedArmorItem.properties(p, type));
    }

    private static DeferredItem<PerkTool> tool(String name, ToolProfile profile, boolean crafted) {
        return ITEMS.registerItem(name, p -> crafted ? new PerkTool.Crafted(p, profile) : new PerkTool(p, profile), p -> {
            Item.Properties props = p.stacksTo(1);
            return profile.maxDamage() > 0 ? props.durability(profile.maxDamage()) : props;
        });
    }

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
        return ITEMS.registerItem(name, p -> new InfoBlockItem(block.get(), p), p -> p.useBlockDescriptionPrefix());
    }
}
