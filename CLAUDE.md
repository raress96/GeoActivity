# GeoActivity

Minecraft tech mod by Rares (published as "Decebaldecebal"). Adds 3 coal ores, graphite/carbon items, a chemistry
system (19 elements), 10 machines (incl. 2 multiblocks and 4 RF/FE machines), perk-upgradable tools and armor.
License: CC BY-NC-SA 4.0.

Gameplay reference (every number, recipe source, mechanic, known bug): **`docs/GAMEPLAY_SPEC.md`**. Read it before
touching gameplay code.

## Status: ported 1.10.2 → NeoForge 26.3 (3.0.0, unreleased)

- Original: MC 1.10.2, Forge 12.18.0.2010, ForgeGradle 2, Java 8, JEI 3.7, bundled CoFH RF API. Last change July 2016.
- Target: **MC 26.3, NeoForge 26.3.0.x, ModDevGradle 2.0.x, Java 25, JEI 26.3 (31.x)**.
- Branch: `claude/admiring-keller-ed50lr` (draft PR #7). `master` still holds the 1.10.2 code; read it with
  `git show master:<path>`.

| Phase | Scope | State |
|---|---|---|
| 0 | CLAUDE.md, gameplay spec | done |
| 1 | Build (MDG, wrapper, Java 25), entrypoint, registries skeleton, asset moves, GameTest/JUnit harness, CI | done |
| 2 | Ores + worldgen, simple items/blocks, vanilla recipes, tags, loot, lang, models (datagen) | done |
| 3 | Machine framework; Coal Refiner, Atom Extractor, Crafting Machine, Chemistry Machine | done |
| 4 | Multiblocks: Advanced Coal Refiner, Advanced Ore Refiner | done |
| 5 | Energy: Thermic Generator, Geothermal Generator, Thermic Melter, Tool Charger | done |
| 6 | Tools, perk engine, item inventories, Auto Stone Builder | done |
| 7 | Armor | done |
| 8 | JEI | done |
| 9 | Delete `legacy/`, README, finalise `CHANGELOG.md` 3.0.0, version 3.0.0, smoke tests | done |

Update this table (and the layout section) as phases land.

**Next up:** Rares's manual playtest (GUI feel, multiblocks in a real world), then the balance/design pass he decides
on (quirks are listed in `docs/GAMEPLAY_SPEC.md` §9), then release 3.0.0 (set the date in `CHANGELOG.md`).
Possible follow-ups: JEI recipe transfer ("+" button), datapack-driven machine recipes (would let packs add recipes).

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
- `item/` – simple items; `tool/` – GUI tools: `ToolProfile(s)` hold each tool's 1.10.2 numbers, `PerkTool` is the
  item, `ToolLogic` fuel/perk rules, `ToolEvents` the perk engine (area/capitator via `BreakBlockEvent`, drop perks via
  `BlockDropsEvent`), `ToolMenu` the tool inventory, plus the Auto Stone Builder. Per-stack state is in data components
  (`geoactivity:perks`, `geoactivity:energy`, `geoactivity:drop_target`, `geoactivity:builder_state`,
  `minecraft:container` for tool inventories). Reinforced tools are vanilla-style items (`Item.Properties.pickaxe(...)`).
- `armor/` – `ModArmorMaterials` (Reinforced = plain vanilla armor), `AdvancedArmorItem` (coal fuel, one perk, tool-style
  inventory via `ProfiledItem` + `ToolMenu`), `ArmorEvents` (perk damage cut, wear), `ArmorRecipes` (Crafting Machine).
  Equipment assets in `assets/geoactivity/equipment/`.
- `perk/` – perk enums and `PerkData`.
- `multiblock/` – shape checks for ACR (2×2×2) and AOR (28 blocks).
- `worldgen/` – ore features + biome modifier (datagen).
- `datagen/` – all providers. `compat/jei/` – JEI plugin (`@JeiPlugin`, compile-only API): one `TexturedCategory` per
  machine drawn from its 1.10.2 GUI texture, recipes straight from `*Recipes.all()`. Never reference it from common
  code or GameTests (JEI is optional and client-only).
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
./gradlew runClient -PquickPlay=<world>   # joins run/saves/<world> directly
```

Headless client in a container (Mesa llvmpipe; needs `mesa-utils`/`xdotool` only for debugging and driving it):
26.x opens its window through SDL3 and wants an sRGB framebuffer, which Xvfb's GLX visuals lack, so force EGL. The
NeoForge early loading window fails the same way: set `earlyWindowControl = false` in `run/config/fml.toml`.

```bash
Xvfb :99 -screen 0 1280x720x24 +extension GLX &
DISPLAY=:99 SDL_VIDEO_FORCE_EGL=1 ./gradlew runClient -PquickPlay=smoke   # world copied from run/world after runServer
DISPLAY=:99 import -window root shot.png                                  # screenshot (ImageMagick)
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
