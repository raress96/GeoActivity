package geoactivity.datagen;

import geoactivity.GeoActivity;
import geoactivity.item.Element;
import geoactivity.perk.ArmorPerk;
import geoactivity.perk.MachinePerk;
import geoactivity.perk.ToolPerk;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import java.util.List;
import java.util.Locale;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;

/** English names and tooltips, converted from the 1.10.2 {@code en_US.lang} and hardcoded tooltip strings. */
public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, GeoActivity.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.geoactivity", "Geo Activity");
        add("itemGroup.geoactivity.elements", "Geo Activity Chemistry");
        add("tooltip.geoactivity.hold_shift", "Hold %s for more info.");
        add("tooltip.geoactivity.shift", "Shift");

        blocks();
        items();
        perks();
        elements();
        tools();
    }

    private void blocks() {
        addBlock(ModBlocks.LIGNITE_ORE, "Lignite Ore");
        addBlock(ModBlocks.BITUMINOUS_ORE, "Bituminous Ore");
        addBlock(ModBlocks.ANTHRACITE_ORE, "Anthracite Ore");
        addBlock(ModBlocks.HARDENED_BRICK, "Hardened Brick");
        addBlock(ModBlocks.ADVANCED_HARDENED_BRICK, "Advanced Hardened Brick");
        addBlock(ModBlocks.THERMAL_HARDENED_BRICK, "Thermal Hardened Brick");
        addBlock(ModBlocks.HARDENED_GLASS, "Hardened Glass");
        addBlock(ModBlocks.GRAPHITE_BASE, "Graphite Base");

        addBlock(ModBlocks.COAL_REFINER, "Coal Refiner");
        addBlock(ModBlocks.CRAFTING_MACHINE, "Crafting Machine");
        addBlock(ModBlocks.ATOM_EXTRACTOR, "Atom Extractor");
        blockTooltip(ModBlocks.ATOM_EXTRACTOR.get(), "Upgrade the speed by placing Advanced Hardened Bricks",
                "adjacent to it (up to a maximum of 4 bricks).");
        addBlock(ModBlocks.CHEMISTRY_MACHINE, "Chemistry Machine");
        blockTooltip(ModBlocks.CHEMISTRY_MACHINE.get(), "Multiblock. Requires 4 Advanced Hardened Bricks and item frames.",
                "Check the JEI description page.");
        addBlock(ModBlocks.ADVANCED_COAL_REFINER, "Advanced Coal Refiner");
        blockTooltip(ModBlocks.ADVANCED_COAL_REFINER.get(), "Multiblock: 2x2x2");
        addBlock(ModBlocks.ADVANCED_ORE_REFINER, "Advanced Ore Refiner");
        blockTooltip(ModBlocks.ADVANCED_ORE_REFINER.get(), "Complicated multiblock. Check the JEI description page.");
        addBlock(ModBlocks.THERMIC_GENERATOR, "Thermic Generator");
        blockTooltip(ModBlocks.THERMIC_GENERATOR.get(), "Requires Thermal Hardened Bricks to be adjacent to it.",
                "Produces 20 FE/t per Thermal Hardened Brick.", "Only outputs power through the bottom.");
        addBlock(ModBlocks.TOOL_CHARGER, "Tool Charger");
        blockTooltip(ModBlocks.TOOL_CHARGER.get(), "Used to charge FE (RF) powered Redstone Tools.");
        addBlock(ModBlocks.GEOTHERMAL_GENERATOR, "Geothermal Generator");
        blockTooltip(ModBlocks.GEOTHERMAL_GENERATOR.get(),
                "Generates FE from lava pools. Placed in a lava pool, it will scan all lava blocks",
                "recursively to a distance of up to 25 meters from the block every minute.",
                "For each lava block found, it will generate 4 FE/t. Output to bottom/top only.",
                "Works better at lower altitudes.",
                "It has a 40% chance to turn lava blocks in a 3x3x3 cube centered around the block",
                "into obsidian, so it will eventually stop working.",
                "Use the Thermic Melter to solve this problem.");
        addBlock(ModBlocks.THERMIC_MELTER, "Thermic Melter");
        blockTooltip(ModBlocks.THERMIC_MELTER.get(), "Turns obsidian in a 5x5x5 cube centered around the block slowly into lava.",
                "Uses 10 FE/t passively and 50k FE per obsidian block.", "Transfer (in): 500 FE/t");
        add("gui.geoactivity.energy", "Energy: ");
        add("gui.geoactivity.lava_blocks", "Lava Blocks: %s");
        add("gui.geoactivity.efficiency", "Efficiency: %s%%");
        add("gui.geoactivity.fe_per_tick", "FE/t: %s");
        add("gui.geoactivity.update_in", "Update in:");
    }

    private void items() {
        addItem(ModItems.LIGNITE_COAL, "Lignite Coal");
        addItem(ModItems.BITUMINOUS_COAL, "Bituminous Coal");
        addItem(ModItems.ANTHRACITE_COAL, "Anthracite Coal");
        addItem(ModItems.GRAPHITE, "Graphite");
        addItem(ModItems.CARBON_FIBER, "Carbon Fiber");
        addItem(ModItems.CARBON_STICK, "Carbon Stick");
        addItem(ModItems.CARBON_ORE_CUTTER, "Carbon Ore Cutter");
        tooltip(ModItems.CARBON_ORE_CUTTER.get(), "Used in the Advanced Ore Refiner to increase yield.");
        addItem(ModItems.SMALL_PRECIOUS_ALLOY_INGOT, "Small Precious Alloy Ingot");
        addItem(ModItems.PRECIOUS_ALLOY_INGOT, "Precious Alloy Ingot");
        addItem(ModItems.LARGE_PRECIOUS_ALLOY_INGOT, "Large Precious Alloy Ingot");
    }

    private void perks() {
        String[] works = {"Works on:"};
        tool(ToolPerk.BLANK, "Blank Perk", "Just a blank perk");
        tool(ToolPerk.SPEED, "Speed Perk", "Tools mine faster.", "Works on:", " - pickaxes", " - shovels", " - axes");
        tool(ToolPerk.EFFICIENCY, "Efficiency Perk", "Tools or armor use less fuel.", "Doesn't work on RF powered tools.");
        tool(ToolPerk.DAMAGE, "Damage Perk", "Tools do more damage.");
        tool(ToolPerk.EXPERIENCE, "Experience Perk", "More experience from mining.", works[0], " - pickaxes", " - swords");
        tool(ToolPerk.SMELTING, "Auto-Smelt Perk", "Auto smelt mined blocks.", works[0], " - pickaxes", " - shovels", " - axes");
        tool(ToolPerk.FORTUNE, "Fortune Perk", "More drops from certain blocks.", works[0], " - pickaxes", " - shovels");
        tool(ToolPerk.SILK_TOUCH, "Silk-Touch Perk", "Get the exact block you mine.", works[0], " - pickaxes", " - shovels", " - axes");
        tool(ToolPerk.RADIUS, "Wider Radius Perk", "Increases the mining radius to 3x3x1 on Advanced and Redstone Tools.",
                "Further increases the range on Advanced Redstone Tools.", works[0], " - pickaxes", " - shovels");
        tool(ToolPerk.CAPITATOR, "Capitator Perk", "Tools destroy blocks in a certain range.", works[0], " - pickaxes", " - shovels", " - axes");
        tool(ToolPerk.NO_DROPS, "No Drops Perk", "Tools do not drop any blocks.", works[0], " - pickaxes", " - shovels", " - axes");
        tool(ToolPerk.DROP_TELEPORT, "Drop Teleportation Perk", "Automatically teleport block drops into a chosen inventory.",
                "Shift + Right Click on an inventory to bind the perk to it.", "Not compatible with Advanced Tools.", "Works only on Redstone Tools.");
        add("tooltip.geoactivity.drop_teleport.bound", "Bound to inventory located at: %s %s %s");
        add("tooltip.geoactivity.drop_teleport.unbind", "Right Click in air to unbind.");
        add("message.geoactivity.drop_teleport.bound", "The perk was successfully bound to that inventory.");
        tool(ToolPerk.ARMOR_BYPASS, "Armor Bypass Perk", "A third of the tool's damage bypasses armor.");

        machine(MachinePerk.SPEED, "Speed Perk (Machine)", "Machines run faster.");
        machine(MachinePerk.EFFICIENCY, "Efficiency Perk (Machine)", "Machines use less fuel.");

        armor(ArmorPerk.GENERAL, "General Protection Perk (Armor)", "Provides more protection against all damage taken.");
        armor(ArmorPerk.BLAST, "Blast Protection Perk (Armor)", "Provides more protection against explosions.");
        armor(ArmorPerk.FIRE, "Fire Protection Perk (Armor)", "Provides more protection against fire damage.");
        armor(ArmorPerk.MAGIC, "Magic Protection Perk (Armor)", "Provides more protection against magic damage.");
        armor(ArmorPerk.PROJECTILE, "Projectile Protection Perk (Armor)", "Provides more protection against projectiles.");
        armor(ArmorPerk.NO_FALL, "No Fall Damage Perk (Armor)", "Take no damage from falling.", works[0], " - pants", " - boots");
        armor(ArmorPerk.RESPIRATION, "Respiration Perk (Armor)", "Breathe underwater.", works[0], " - helmets");
    }

    private void tools() {
        addItem(ModItems.REINFORCED_SWORD, "Reinforced Sword");
        addItem(ModItems.REINFORCED_PICKAXE, "Reinforced Pickaxe");
        addItem(ModItems.REINFORCED_AXE, "Reinforced Axe");
        addItem(ModItems.REINFORCED_SHOVEL, "Reinforced Shovel");
        addItem(ModItems.REINFORCED_HOE, "Reinforced Hoe");
        addItem(ModItems.REINFORCED_MINER, "Reinforced Miner");
        addItem(ModItems.ADVANCED_SWORD, "Advanced Sword");
        addItem(ModItems.ADVANCED_PICKAXE, "Advanced Pickaxe");
        addItem(ModItems.ADVANCED_AXE, "Advanced Axe");
        addItem(ModItems.ADVANCED_SHOVEL, "Advanced Shovel");
        addItem(ModItems.REDSTONE_MINER, "Redstone Miner");
        addItem(ModItems.REDSTONE_BATTLE_AXE, "Redstone BattleAxe");
        addItem(ModItems.REDSTONE_BATTLE_MINER, "Redstone BattleMiner");
        addItem(ModItems.ADVANCED_REDSTONE_BATTLE_MINER, "Advanced Redstone BattleMiner");
        addItem(ModItems.AUTO_STONE_BUILDER, "Auto Stone Builder");
        add("tooltip.geoactivity.destroyed", "Destroyed");
        add("tooltip.geoactivity.energy", "Energy: %s / %s FE");
        add("tooltip.geoactivity.blocks", "%s blocks");
        add("tooltip.geoactivity.armor_bypass_damage", "%s Armor Bypass Damage");
        add("tooltip.geoactivity.perk.speed", "Has extra speed! [%s]");
        add("tooltip.geoactivity.perk.efficiency", "Uses less fuel! [%s]");
        add("tooltip.geoactivity.perk.damage", "Deals extra damage! [%s]");
        add("tooltip.geoactivity.perk.experience", "Gives you more experience.");
        add("tooltip.geoactivity.perk.smelting", "Auto smelts!");
        add("tooltip.geoactivity.perk.fortune", "Has Fortune %s enchant!");
        add("tooltip.geoactivity.perk.silk_touch", "Has Silk-Touch enchant!");
        add("tooltip.geoactivity.perk.radius", "Has wider mining radius! [%s]");
        add("tooltip.geoactivity.perk.capitator", "Destroys blocks in a vertical line.");
        add("tooltip.geoactivity.perk.no_drops", "Gives no block drops.");
        add("tooltip.geoactivity.perk.drop_teleport", "Teleports block drops.");
        add("tooltip.geoactivity.perk.armor_bypass", "Deals damage that bypasses armor.");
        add("tooltip.geoactivity.perk.blank", "");

        addItem(ModItems.REINFORCED_HELMET, "Reinforced Helmet");
        addItem(ModItems.REINFORCED_CHESTPLATE, "Reinforced Chestplate");
        addItem(ModItems.REINFORCED_LEGGINGS, "Reinforced Leggings");
        addItem(ModItems.REINFORCED_BOOTS, "Reinforced Boots");
        addItem(ModItems.ADVANCED_HELMET, "Advanced Helmet");
        addItem(ModItems.ADVANCED_CHESTPLATE, "Advanced Chestplate");
        addItem(ModItems.ADVANCED_LEGGINGS, "Advanced Leggings");
        addItem(ModItems.ADVANCED_BOOTS, "Advanced Boots");
        add("tooltip.geoactivity.armor_perk.general", "Extra protection against all damage!");
        add("tooltip.geoactivity.armor_perk.blast", "Extra protection against explosions!");
        add("tooltip.geoactivity.armor_perk.fire", "Extra protection against fire damage!");
        add("tooltip.geoactivity.armor_perk.magic", "Extra protection against magic damage!");
        add("tooltip.geoactivity.armor_perk.projectile", "Extra protection against projectiles!");
        add("tooltip.geoactivity.armor_perk.no_fall", "Take no fall damage.");
        add("tooltip.geoactivity.armor_perk.respiration", "Breathe underwater.");
    }

    private void elements() {
        addItem(ModItems.EMPTY_ELEMENT_CONTAINER, "Empty Element Container");
        for (Element element : Element.values()) {
            String name = element.key().substring(0, 1).toUpperCase(Locale.ROOT) + element.key().substring(1);
            add(ModItems.element(element), name + " Element Container");
        }
    }

    private void tool(ToolPerk perk, String name, String... tooltip) {
        named(ModItems.toolPerk(perk), name, tooltip);
    }

    private void machine(MachinePerk perk, String name, String... tooltip) {
        named(ModItems.machinePerk(perk), name, tooltip);
    }

    private void armor(ArmorPerk perk, String name, String... tooltip) {
        named(ModItems.armorPerk(perk), name, tooltip);
    }

    private void named(Item item, String name, String... tooltip) {
        add(item, name);
        tooltip(item, tooltip);
    }

    void blockTooltip(net.minecraft.world.level.block.Block block, String... lines) {
        tooltipKey(block.getDescriptionId(), List.of(lines));
    }

    /** Lines are {@code <item description id>.tooltip.0..n}; see {@link geoactivity.util.Tooltips}. */
    void tooltip(Item item, String... lines) {
        tooltipKey(item.getDescriptionId(), List.of(lines));
    }

    void tooltipKey(String descriptionId, List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            add(descriptionId + ".tooltip." + i, lines.get(i));
        }
    }
}
