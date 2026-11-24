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
