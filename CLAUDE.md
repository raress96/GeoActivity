# GeoActivity

Minecraft tech mod by Rares (published as "Decebaldecebal"). Adds 3 coal ores, graphite/carbon items, a chemistry
system (19 elements), 10 machines (incl. 2 multiblocks and 4 RF/FE machines), perk-upgradable tools and armor.
License: CC BY-NC-SA 4.0.

Gameplay reference (every number, recipe source, mechanic, known bug): **`docs/GAMEPLAY_SPEC.md`**. Read it before
touching gameplay code.

## Status: porting 1.10.2 → NeoForge 26.3 (in progress)

- Original: MC 1.10.2, Forge 12.18.0.2010, ForgeGradle 2, Java 8, JEI 3.7, bundled CoFH RF API. Last change July 2016.
- Target: **MC 26.3, NeoForge 26.3.0.x, ModDevGradle 2.0.x, Java 25, JEI 26.3 (31.x)**.
- Branch: `claude/admiring-keller-ed50lr`. `master` still holds the 1.10.2 code.
- During the port the old sources live in `legacy/` (not compiled) for side-by-side reading. Deleted in the final phase.
  After that, use `git show master:<path>` to read the original code.

| Phase | Scope | State |
|---|---|---|
| 0 | CLAUDE.md, gameplay spec | done |
| 1 | Build (MDG, wrapper, Java 25), entrypoint, registries skeleton, asset moves, GameTest/JUnit harness, CI | done |
| 2 | Ores + worldgen, simple items/blocks, vanilla recipes, tags, loot, lang, models (datagen) | done |
| 3 | Machine framework; Coal Refiner, Atom Extractor, Crafting Machine, Chemistry Machine | done |
| 4 | Multiblocks: Advanced Coal Refiner, Advanced Ore Refiner | done |
| 5 | Energy: Thermic Generator, Geothermal Generator, Thermic Melter, Tool Charger | todo |
| 6 | Tools, perk engine, item inventories, Auto Stone Builder | todo |
| 7 | Armor | todo |
| 8 | JEI | todo |
| 9 | Delete `legacy/`, README/Changelog, version 3.0.0, smoke tests | todo |

Update this table (and the layout section) as phases land.

## Porting rules

1. **Gameplay stays 1:1** with `docs/GAMEPLAY_SPEC.md`. Balance/design changes are a later, separate pass that Rares
   decides on.
2. **Fix technical bugs** the port makes moot (GUI short overflow, coord-0 multiblock bug, localized-name checks,
   NBT wiping). **Log gameplay quirks** in the spec's "Known bugs and quirks" section instead of silently fixing them.
3. **Don't trust memory for APIs.** NeoForge/Minecraft 26.x renamed and reworked a lot (e.g. `Identifier` instead of
   `ResourceLocation`, transfer API with `ResourceHandler<ItemResource>` / `EnergyHandler`, `ValueInput`/`ValueOutput`
   for saving, item model definitions in `assets/<ns>/items/`, equipment assets, data-driven GameTests). Check the
   decompiled sources MDG provides (IDE sources jar, or `~/.gradle/caches` / `build/moddev`) before using an API.
4. Every metadata variant from 1.10.2 is a separate block/item now, with snake_case IDs
   (`lignite_ore`, `hydrogen_element_container`, `speed_perk`, `advanced_hardened_brick`, ...). Old saves are not
   compatible and that's accepted.
5. Prefer data over code: recipes (vanilla and machine), tags, loot tables, worldgen, models, blockstates and lang are
   produced by datagen into `src/generated/resources` and committed.

## Target architecture (package `geoactivity`)

- `GeoActivity` – `@Mod` entrypoint; `GeoActivityClient` – client-only `@Mod(dist = CLIENT)`.
- `registry/` – one `DeferredRegister` holder per kind: blocks, items, block entity types, menu types, recipe
  types/serializers, data components, creative tabs (`geoactivity`, `geoactivity_elements`).
- `block/`, `block/entity/` – blocks and block entities (tickers, item handlers with the old sided rules).
- `client/screen/` – `MachineScreen` draws the 1.10.2 GUI textures; item GUIs open via
  `player.openMenu(..., hand)`. GUI textures: `textures/gui/container/*.png` (lowercase).
- `machine/` – `MachineBlock` / `MachineBlockEntity` (vanilla `WorldlyContainer`, exposed to hoppers/pipes through
  `WorldlyContainerWrapper`) / `MachineMenu`; one sub-package per machine with its block, block entity, menu and
  recipe table. Machine recipes are **Java tables** (`*Recipes.all()`), like 1.10.2: same on client and server, so JEI
  reads them directly. Inputs are an item or a tag (`RecipeInput`). Making them datapack-driven is a later option.
- `item/`, `item/tool/`, `item/armor/` – items; per-stack state lives in data components
  (`geoactivity:perks`, `geoactivity:energy`, `minecraft:container` for tool inventories, droptp target, ...).
- `perk/` – perk definitions and the shared block-breaking/drop pipeline.
- `multiblock/` – shape checks for ACR (2×2×2) and AOR (28 blocks).
- `worldgen/` – ore features + biome modifier (datagen).
- `datagen/` – all providers. `compat/jei/` – JEI plugin.
- `src/test/java` – JUnit; GameTests under `geoactivity.gametest`.

Energy is NeoForge FE (RF-compatible) through the NeoForge energy capability; the bundled `cofh/api` is gone.
`ContainerData` syncs values as 16-bit, so 32-bit values (energy) are split across two data slots.

## Commands

Java 25 is provisioned by the Gradle toolchain (foojay); the system JDK (21) only runs Gradle itself.

```bash
./gradlew build                # compile + unit tests + jar (build/libs/geoactivity-<ver>.jar)
./gradlew runData              # datagen -> src/generated/resources (commit the output)
./gradlew runGameTestServer    # headless GameTests; non-zero exit on failure
./gradlew runServer            # dedicated server (--nogui); accept EULA in run/eula.txt
./gradlew runClient            # dev client (JEI included at runtime)
xvfb-run -s "-screen 0 1280x720x24" ./gradlew runClient   # headless client in a container (Mesa llvmpipe)
```

## Testing

- GameTests are the main safety net: in-world fixtures for every machine, multiblock, tool perk and armor perk, plus
  registry/asset completeness and recipe-count checks against the spec. Add tests with each feature.
- JUnit (MDG `unitTest`) for pure logic: perk math, recipe matching, multiblock shape checks.
- Smoke: `runServer` boots clean (grep the log for `ERROR`/missing registries); `runClient` under Xvfb loads without
  missing model/texture warnings. Visual/GUI feel needs a manual playtest by Rares.
- CI (`.github/workflows/build.yml`) runs build + GameTests on push.

## Conventions

- Java 25, 4-space indent, `final` where useful, records for immutable data (recipes, components).
- Registry IDs and resource paths: lowercase snake_case. Translation keys come from datagen.
- Keep classes small and per-feature; shared behaviour in base classes only where 3+ users exist.
- Commit per phase (or smaller), message `Port: <what>`. Don't push to `master`.
