package geoactivity.gametest;

import geoactivity.machine.chemistry.ChemistryRecipes;
import geoactivity.machine.crafting.CraftingMachineRecipes;
import geoactivity.machine.extractor.AtomExtractorRecipes;
import java.util.List;
import net.minecraft.locale.Language;

/**
 * Phase 8: what the JEI categories rely on (the plugin itself only loads on a client with JEI): every recipe fits its
 * category's slots, and every JEI string is translated.
 */
final class JeiDataTests {
    private JeiDataTests() {}

    static void register() {
        GATests.test("jei_recipes_fit_their_category", 5, helper -> {
            for (var recipe : AtomExtractorRecipes.all()) {
                helper.assertTrue(recipe.outputs().size() <= 3, "atom extractor shows 3 outputs: " + recipe);
            }
            for (var recipe : ChemistryRecipes.all()) {
                helper.assertTrue(recipe.groups().size() <= 4, "chemistry machine shows 4 element groups: " + recipe);
            }
            for (var recipe : CraftingMachineRecipes.all()) {
                switch (recipe) {
                    case CraftingMachineRecipes.Shaped s -> helper.assertTrue(s.width() <= 3 && s.height() <= 3, "3x3: " + s);
                    case CraftingMachineRecipes.Shapeless s -> helper.assertTrue(s.inputs().size() <= 9, "9 inputs: " + s);
                }
            }
            helper.succeed();
        });

        GATests.test("jei_strings_are_translated", 5, helper -> {
            Language lang = Language.getInstance();
            List<String> keys = List.of("jei.geoactivity.crafting_machine.perk_slot", "jei.geoactivity.chemistry_machine.group");
            List<String> described = List.of("advanced_coal_refiner", "advanced_ore_refiner", "chemistry_machine",
                    "thermic_generator", "geothermal_generator", "thermic_melter");
            for (String key : keys) {
                helper.assertTrue(lang.has(key), "missing " + key);
            }
            for (String id : described) {
                helper.assertTrue(lang.has("jei.geoactivity.description." + id), "missing description for " + id);
            }
            helper.succeed();
        });
    }
}
