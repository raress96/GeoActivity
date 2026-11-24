package geoactivity.gametest;

/** Expected counts from the 1.10.2 code, updated as content is ported. See docs/GAMEPLAY_SPEC.md. */
final class GAExpected {
    /** Recipes in the geoactivity namespace. */
    static final int RECIPES = 13 + 10 + 7 + 3 + 19;

    /** Crafting Machine: 12 perk + 12 tool recipes; the 8 armor recipes come with phase 7 (32 total). */
    static final int CRAFTING_MACHINE_RECIPES = 24;

    private GAExpected() {}
}
