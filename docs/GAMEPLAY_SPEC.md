# GeoActivity gameplay spec (from the 1.10.2 code)

Source of truth for the port. Numbers come from the 1.10.2 code, release 2.4 (`git show 4d3b9d4:<path>`).
Full recipe lists are not copied here; they are transcribed from the legacy recipe classes straight into datagen, and
GameTests check the counts. Sections marked **[BUG]** / **[QUIRK]** describe existing behaviour; see the last section
for what the port does with each.

Legacy paths below are relative to `src/main/java/geoactivity/common/` in the old layout.

## 1. Content overview

| Old registry name (meta) | New ID(s) | Notes |
|---|---|---|
| `ligniteore`, `bituminousore`, `anthraciteore` | `lignite_ore`, `bituminous_ore`, `anthracite_ore` | §2 |
| `hardenedbrick_normal` (meta 0/1/2) | `hardened_brick`, `advanced_hardened_brick`, `thermal_hardened_brick` | §3 |
| `hardenedglass`, `graphitebase` | `hardened_glass`, `graphite_base` | §3 |
| 10 machines | `coal_refiner`, `advanced_coal_refiner`, `advanced_ore_refiner`, `atom_extractor`, `chemistry_machine`, `crafting_machine`, `thermic_generator`, `geothermal_generator`, `thermic_melter`, `tool_charger` | §5 |
| `lignitecoal`, `bituminouscoal`, `anthracitecoal` | `lignite_coal`, `bituminous_coal`, `anthracite_coal` | |
| `graphitecoal`, `carbonfiber`, `carbonstick`, `carbonorecutter` | `graphite`, `carbon_fiber`, `carbon_stick`, `carbon_ore_cutter` | |
| `preciousalloyingot_*` (3 metas) | `small_precious_alloy_ingot`, `precious_alloy_ingot`, `large_precious_alloy_ingot` | rarity uncommon/rare/epic |
| `elementcontainer_*` (20 metas) | `empty_element_container`, `<element>_element_container` ×19 | stack 32 |
| `perk_*` (13 metas) | `blank_perk`, `speed_perk`, `efficiency_perk`, `damage_perk`, `experience_perk`, `smelting_perk`, `fortune_perk`, `silk_touch_perk`, `radius_perk`, `capitator_perk`, `no_drops_perk`, `drop_teleport_perk`, `armor_bypass_perk` | stack 1 |
| `perk_m_*` (2 metas) | `machine_speed_perk`, `machine_efficiency_perk` | stack 1 |
| `perk_a_*` (7 metas) | `general_protection_perk`, `blast_protection_perk`, `fire_protection_perk`, `magic_protection_perk`, `projectile_protection_perk`, `no_fall_perk`, `respiration_perk` | stack 1 |
| tools | `reinforced_{sword,pickaxe,axe,shovel,hoe}`, `reinforced_miner`, `advanced_{sword,pickaxe,axe,shovel}`, `redstone_miner`, `redstone_battle_axe`, `redstone_battle_miner`, `advanced_redstone_battle_miner`, `auto_stone_builder` | §6 |
| armor | `reinforced_{helmet,chestplate,leggings,boots}`, `advanced_{helmet,chestplate,leggings,boots}` | §7 |

Elements (old meta order): 0 empty, 1 hydrogen, 2 helium, 3 lithium, 4 carbon, 5 nitrogen, 6 oxygen, 7 neon,
8 sodium, 9 magnesium, 10 aluminium, 11 silicon, 12 phosphorus, 13 sulfur, 14 chlorine, 15 potassium, 16 calcium,
17 iron, 18 iodine, 19 gold.

Creative tabs: `geoactivity` (icon: lignite coal) and `geoactivity_elements` (icon: empty element container; holds the
element containers).

Declared but never registered in 1.10.2: `advancedHoe`, `advancedhardenedbrick` field. Not ported.

## 2. Ores and world gen

Overworld only (dimension 0). Per chunk: N attempts of a vein (replaces stone) at random x/z, y uniform in
[minY, maxY).

| Ore | Attempts/chunk | Y | Vein size | Pickaxe level | Hardness | Resistance | Smelt XP |
|---|---|---|---|---|---|---|---|
| Lignite | 14 | 46–64 | 3 | 2 (iron) | 3 | 15 | 2 |
| Bituminous | 14 | 34–48 | 3 | 2 (iron) | 4 | 15 | 3 |
| Anthracite | 14 | 18–38 | 3 | 3 (diamond) | 5 | 15 | 4 |

- Drops its coal, fixed 3 XP. Custom fortune table: fortune 1 → 1–2, 2–3 → 2–3, 4 → 3. **[QUIRK]** Port uses the
  vanilla ore-drops formula instead (loot tables can't express the old table cleanly).
- Ores smelt into their coal. Old OreDict names: `gemLigniteCoal`, `gemBituminousCoal`, `gemAnthraciteCoal`.
- Old config `config/GeoActivity/geoactivity.cfg` (attempts, minY, maxY, vein size per ore). Port: values live in the
  placed features; override with a datapack.
- **[QUIRK]** The new coals are not furnace fuel in vanilla furnaces. Only the mod's machines and tools burn them.
  3.0.0 balance pass: vanilla furnace fuel at 2400/3200/4800 ticks (lignite/bituminous/anthracite).

## 3. Simple blocks

| Block | Material / tool | Hardness | Resistance | Notes |
|---|---|---|---|---|
| Hardened brick (normal, advanced, thermal) | stone, pickaxe level 3 | 20 | 150 | Advanced = AE upgrade, ChM and AOR structure. Thermal = Thermic Generator. |
| Hardened glass | glass, pickaxe level 3 | 20 | 130 | cutout, not full cube |
| Graphite base | stone, pickaxe level 2 | – | – | cutout, not full cube |

Explosion resistance in 1.10.2 was `setResistance(r)` → effective r × 3 / 5; the port stores the effective value
(ores 9, bricks 90, glass 78, graphite base 9). Graphite base hardness is 3.

Machines: iron material, pickaxe. Coal Refiner level 2 / hardness 3.5; Atom Extractor hardness 5; Chemistry Machine
level 3 / hardness 10; AOR level 3 / hardness 20.

## 4. Vanilla-style recipes

Transcribed from `GAInit.java` (1.10.2). Counts to verify: **34 shaped**, **4 smelting**.

- Shaped: 7 tools (Reinforced Miner crafted at damage 498, Auto Stone Builder at 998 = "empty"), 5 blocks
  (8 hardened bricks from graphite + stone bricks + carbon stick; 8 hardened glass; graphite base; advanced brick;
  thermal brick), 10 machines (e.g. coal refiner = gold, iron, furnaces, coal), 4 reinforced armor pieces, 8 misc
  (diamond from carbon parts; 4 graphite → coal; 6 graphite → carbon fiber; 2 fiber → stick; 3 precious alloy tiers;
  8 element containers).
- Smelting: each ore → its coal (XP 2/3/4); any filled element container → empty container (XP 0.5). The latter was one
  wildcard recipe in 1.10.2 and is 19 JSON recipes now, so the port has 13 + 3 + 19 = 35 recipes after phase 2.

## 5. Machines

All item machines save their inventory and sync progress to the GUI. Slot coordinates refer to the old GUI textures,
which are reused as-is.

### 5.1 Coal Refiner (CR)
- States: facing (horizontal), active. Comparator output, flame particles when active.
- Slots: 0 input (66,35), 1 fuel (30,35), 2 output (126,35, gives XP).
- 400 ticks per item. Fuel burn ticks: coal 400, lignite 3200, bituminous 6400, anthracite 9600.
- Recipes (`CRRecipes`, 8): coal → 1 graphite; lignite/bituminous/anthracite coal → 8/16/24 graphite; coal ore →
  4 graphite; each custom ore → 2 of its coal. XP 0.2–0.5.

### 5.2 Advanced Coal Refiner (ACR) – multiblock
- 2×2×2 cube of ACR blocks. Formed when the last block is placed: the placed block has exactly 3 ACR neighbours (one
  vertical, one along X, one along Z) and so does the diagonally opposite corner. Last placed block = core; the other
  7 forward inventory/GUI to it. Breaking any block unforms the cube. State: formed.
- Slots: 0 fuel, 1–2 inputs, 3–4 outputs, 5–6 machine perks. Two parallel lanes using the CR recipes.
- Cook ticks = 200 × (1 − 0.25·speed). Fuel ticks = base × (1 − 0.25·speed) × (1 + 0.5·efficiency); base coal 200,
  lignite 1600, bituminous 3200, anthracite 4800.
- **[BUG]** Coordinate 0 is used as "no core", so formation breaks at x/y/z = 0.

### 5.3 Atom Extractor (AE)
- States: facing, active. No fuel, no energy.
- Slots: 0 input (22,30), 1 empty containers (50,55), 2–4 outputs.
- Recipes (`AERecipes`, **59**): input → up to 3 element-container stacks + time. E.g. diamond → 32 carbon (1200 t);
  iron ingot → 2 iron + 1 lithium (400 t); nether star → 10 He, 10 Li, 12 Ne (5000 t). Matching by item, or by shared
  OreDict ID (port: tags).
- Starting a cycle consumes 1 input and as many empty containers as the total output count.
- Upgrades: advanced hardened bricks touching any face, up to 4. Time −= time·upg/8 + 10·upg.

### 5.4 Chemistry Machine (ChM) – structure
- Not ticking; 2 input slots.
- Structure: 4 advanced hardened bricks at the diagonal corners (±1, 0, ±1). Item frames on those bricks display
  element containers; the number of frames showing an element = the count required (e.g. 4 frames of gold).
- Trigger: placing a coal block on top. Consumes 1 of each input, clears the frames, removes the coal block, drops the
  result as an item entity.
- Recipes (`ChMCrafting`, **19**, order-insensitive): 8 tool perks (incl. drop-teleport), 2 armor perks, carbon stick
  + blaze rod → 2 carbon ore cutters, 8 conversions (e.g. 2 gold → 4 blaze rods, 2 blaze rods → emerald).
- **[BUG]** Old trigger relied on `onNeighborChange`, unreliable. Port uses `neighborChanged`.

### 5.5 Crafting Machine (CrM)
- Slots: 0 result preview, 1 fuel (coal block or custom coals), 2 output, 3 perk, 4–12 3×3 grid.
- 80 ticks per craft; consumes one of each grid item on tick 1. Fuel ticks: coal block 82, lignite 164,
  bituminous 328, anthracite 490.
- Recipes (`CrMCrafting`, **32** = 13 shaped + 19 shapeless, each with a compatible-perks list): perks, tier II
  advanced tools/armor, tier III redstone tools, 8 "reset" recipes. Also repairs two identical items (+5%).
- The perk in slot 3 is applied to the output (`speed`, `efficiency`, `auto-smelt`, `silk-touch`, `widerRadius`,
  `capitator`, `barmor`, `blast`, `fall`, ...).
- **[BUG]** Applies the perk with `setTagCompound`, wiping all other stack data (enchantments, previous perk).
- Compatible-perk lists (`PerkHelper`) are only shown in JEI, not enforced.

### 5.6 Advanced Ore Refiner (AOR) – multiblock
- 28 blocks extending behind the core (5 wide × 4 tall × 4 deep): depth 1 a column of 4 advanced bricks (y−1..y+2);
  depths 2–3 bricks at y−1 and y+2 with air between; depth 4 four bricks; at depths 2–3, heights y..y+1, two 2×2×2
  ACR cubes on either side (16 ACR blocks). On formation all 28 become AOR blocks (remembering what they were);
  breaking one restores the bricks and ACRs. States: facing, formed, core.
- Slots: 0–3 machine perks, 4–7 ores, 8–11 outputs, 12 fuel (custom coals only), 13 wood (logs), 14 carbon ore cutter.
- Valid ore: a block item that is an ore, drops itself, and has a furnace result. Port: `#c:ores` + smelting lookup.
- Output per ore: 2 (25% chance 3). With a cutter: 3 (1/6 chance 4); each cutter = 16 boosted cycles.
- Cook ticks = 50 × (2 − 0.25·speed) for all 4 lanes at once.
- Heat-up: uses 8 logs, then heat += 10 + 2·speed per tick up to the fuel value: lignite 1600, bituminous 3200,
  anthracite 4800, × (2 − 0.25·speed) × (1 + 0.25·efficiency).
- **[BUG]** Ore check uses the localized name containing "Ore" (language dependent).

### 5.7 Thermic Generator (TG)
- No GUI. +20 FE/t per thermal hardened brick (top + 4 sides, max 5) that can see the sky, daytime only.
- Buffer 10k. Pushes energy down only.

### 5.8 Geothermal Generator (GG)
- GUI with energy info, no slots.
- Every 1200 ticks: flood-fill lava source blocks within 25 blocks, starting from its sides.
  Efficiency % = clamp(120 − y, 1, 100). FE/t = lava × 4 × eff / 100. Two GGs sharing lava are both halved.
- Buffer 50k; transfer max(2·FE/t, 80). Outputs down, then up.
- Mid-cycle: in the surrounding 3×3×3, each column has a 40% chance to turn lava into obsidian.

### 5.9 Thermic Melter (TM)
- No GUI. Receives from any side. Buffer 200k, 500 FE/t in, −10 FE/t passive.
- Every 800 ticks: each obsidian in a 5×5×5 cube → lava, 50k FE each.

### 5.10 Tool Charger (TC)
- 1 slot (80,35) for electric tools (the Advanced Redstone BattleMiner). Buffer 10M, 20k FE/t in from any side,
  charges the tool every tick.
- **[BUG]** GUI energy synced as a short → overflows (also GG). Port splits into two 16-bit slots.

## 6. Tools

### 6.1 Materials
- Reinforced: harvest 2 (iron), 500 uses, speed 8, damage 3, enchantability 18.
- Advanced: harvest 3 (diamond), 1000 uses, speed 10, damage 5, enchantability 8.

### 6.2 Tool table
| Item | Behaves as | Attack dmg / speed | Durability / fuel | Inventory |
|---|---|---|---|---|
| Reinforced sword/pickaxe/axe/shovel/hoe | vanilla tools | axe 5 / −3.1, others vanilla | normal | – |
| Reinforced Miner | pickaxe + shovel, level 2 | 5 / −3.0 | crafted at 498 = empty, unusable ≥498; coal repairs 100/200/300 (lignite/bit./anth.); hit costs 2 | 1 fuel slot |
| Advanced Sword | sword | 7 + damage perk / −2.4 | unusable ≥998; hit costs 1 | fuel + perk view |
| Advanced Pickaxe/Shovel/Axe | tool | 5 / 5.5 / 6, −3.0 | unusable ≥998; coal repairs 100/200/300 (+20/30/40 with efficiency); hit 2, block 1 | slot 0 coal, slot 1 perk (read-only) |
| Redstone Miner | pickaxe + shovel | 6 | max 1000, unusable ≥998 | 4 fuel + 4 perk |
| Redstone Battle Axe | axe + hoe action | 10 | max 1000, unusable ≥998 | 4 fuel + 4 perk |
| Redstone Battle Miner | pickaxe/shovel/axe + hoe action | 12 | max 2000, unusable ≥1998 | 4 fuel + 8 perk |
| Advanced Redstone Battle Miner | all + hoe action | 12, mining speed 12 | 5,000,000 FE, 20,000 FE/t in, no extract; 1000 FE per block/hit/till (×2 at radius ≥3) | 12 perk slots |

- Redstone tool fuel: lignite 130, bituminous 260, anthracite 400 (+20/30/40 per efficiency level). Hit costs 1.
- Tools never break: they become "destroyed" (no attributes, can't mine) at the threshold and are refuelled with coal.
- Mining speed: tool speed if the block is effective for the tool (harvest level ≤ material's), else 1.
  "Can harvest" = speed > 1. Old effectiveness was by block Material: pickaxe = rock, iron, anvil, circuits, glass,
  ice, piston; axe = wood, leaves, plants, vine, circuits, cactus, gourd; shovel = grass, ground, clay, sand, snow;
  miner = pickaxe + shovel; battle miner = all. Port: `mineable/*` tags.
- Hoe action (battle axe, battle miners): grass/dirt → farmland, coarse dirt → dirt.
- Item GUIs open with sneak + right-click.
- **[BUG]** Reinforced Miner `getToolClasses` says pickaxe + axe (should be pickaxe + shovel).
- **[BUG]** Redstone tools only burn fuel slot 0; slots 1–3 just hold items. Fixed in the 3.0.0 balance pass.

### 6.3 Perks on tools
Redstone tools: perk items sit in the tool's inventory; on GUI close they become flags/levels on the stack. Advanced
tools/armor: perks only via the Crafting Machine, one perk at a time.

| Perk | Effect | Max level |
|---|---|---|
| speed | +2 mining speed per level | Adv 1, Redstone 2, RF miner 4 |
| efficiency | +20/+30/+40 repair per coal per level | Adv/armor 1, Redstone 2; ignored on RF miner |
| damage | +1 attack damage per level | Adv 1, Redstone 2, RF miner 4 |
| experience | +1 XP per mined ore that doesn't drop itself; +1 XP per kill (+1 more vs creatures with battle tools) | – |
| auto-smelt | drop → furnace result. Redstone tools + fortune on ores: F1 +0–1, F2 +1, F3 +1 (+1 at 1/7), F4 +1 (+1 at 1/4) | disables silk touch |
| fortune | fortune level for drops | Adv 1; Redstone/RF miner up to 4 |
| silk touch | drops the block itself | disabled by smelting or fortune |
| radius | 3×3×1 on the clicked face, mining speed −8. Adv pickaxe + Redstone Miner: only blocks matching the centre; Battle Miner + RF miner: anything harvestable. RF miner levels: 3×3×1, 5×5×1, 7×7×3, 9×9×5; level ≥3 halves speed and doubles FE cost | overrides capitator |
| capitator | mining speed −8. Adv Axe: fells matching blocks (recursive 3×3×3). Redstone Battle Axe: same, any log. Redstone Miner: column of matching blocks ~9 up/down. Battle Miner + RF miner: logs felled, otherwise a 5 up / 5 down column | – |
| no drops | block deleted, no drops (stone-break sound on redstone tools) | overrides everything |
| drop teleport | drops inserted into a bound inventory (sneak-use on an inventory to bind, right-click air to unbind); leftovers drop. Redstone tools + RF miner only | – |
| armor bypass | **[BUG]** not implemented; only a tooltip ("damage/3 armor bypass damage"). 3.0.0: armor reduces only two thirds of a melee hit | – |

Drop priority: no drops > silk touch > smelting > fortune > (radius/capitator/droptp manual drops) > vanilla.
Area breaking requires hardness > 0, harvest level ≤ 3, an effective tool, and edit permission; creative just clears.
- **[BUG]** Advanced pickaxe/shovel capitator column never fires (`pos.add(0, yPos, 0)` misuse). 3.0.0: they use the
  Redstone Miner's column (matching blocks, 10 up / 9 down; the broken loop aimed at 9 / 9).

### 6.4 Machine perks
- ACR (2 slots): time × (1 − 0.25·speed), fuel × (1 + 0.5·efficiency).
- AOR (4 slots): time factor 2.0 − 0.25·speed, fuel × (1 + 0.25·efficiency).

### 6.5 Where perks come from
- Crafting Machine: blank, speed, efficiency, damage, armor bypass, both machine perks, general/blast/fire/magic/
  projectile armor perks.
- Chemistry Machine: experience, smelting, fortune, silk touch, radius (speed perk + diamond), capitator (efficiency
  perk + diamond), no drops, drop teleport, no fall, respiration.

### 6.6 Auto Stone Builder
- Max damage 1000; crafted at 998 (empty). Sneak + right-click: GUI with coal, source item, target block.
- On GUI close it finds how source → target: smelt (cost 2), 2×2 craft (cost 1, uses 4 source), smelt+craft or
  craft+smelt (cost 3). Pulls matching source items from the player's main inventory, stores up to 64 as a count.
- Use on a block: places the target on the clicked face; auto-refills from inventory when the count hits 1; stops at
  damage ≥998. Right-click air with damage >150 burns coal: lignite repairs 150, bituminous 300, anthracite 450.

## 7. Armor

- Reinforced: durability factor 30, protection {2, 6, 5, 2} in old array order, enchantability 18. Plain armor.
  **[QUIRK]** Read as boots 2, legs 6, chest 5, helmet 2 → legs give more than the chest. 3.0.0: chest 6, legs 5.
- Advanced: durability factor 35, protection {4, 10, 7, 4}, toughness 2, enchantability 8. Crafted at max damage − 2
  (empty), refuelled with coal like Advanced tools. Destroyed at max − 2 (absorbs ~1 damage).
- Advanced absorption (old `ISpecialArmor`): per piece armor/25, uncapped. General perk adds +1/+2/+1/+1
  (helmet/chest/legs/boots) always; blast/fire/magic/projectile add +2/+3/+2/+2 when the damage type matches.
  Armor bar shows 3/8/6/3 (1 when destroyed). Wear: 1 durability if a perk matched the damage, else 2.
  **[QUIRK]** Port: vanilla armor attributes + perk bonus as extra reduction in a damage event; exact old math differs.
- Respiration (helmet): when air hits 0, set air to 150, costs 16 durability. No-fall (legs or boots): fall distance
  reset every tick, free.

## 8. GUIs and client

- Old: single `IGuiHandler`, ID 0; machine GUIs opened at the core for multiblocks; item GUIs via sneak + right-click.
- Tooltips with Shift detail. GUI textures (uppercase names in 1.10.2) reused, renamed lowercase.
- JEI (old): categories for CrM, CR, ACR, AE, ChM; info pages for AOR and ChM; click areas and transfer handlers.

## 9. Known bugs and quirks – port decisions

Port notes (phase 3):
- Atom Extractor: LOG and LOG2 were two identical recipes; now one `#minecraft:logs` recipe (58 total).
  Inputs with an OreDictionary name use the matching `c:` tag (e.g. `c:gems/diamond`).
- Coal Refiner: "coal ore" input is now `c:ores/coal` (includes deepslate coal ore).
- Crafting Machine: an interrupted craft keeps its result instead of losing it; a crafting machine broken mid-craft
  drops the pending result. The 1.10.2 armor-efficiency branch crashed (null perk slot); fixed.
- Advanced Coal Refiner / Ore Refiner: each lane only processes when its output fits (1.10.2 could overfill a
  stack past 64). Swallowing a formed ACR cube into an Ore Refiner drops that cube's items instead of deleting them.
  Valid ores are `c:ores` items with a furnace recipe.
- Energy machines (phase 5): energy is NeoForge FE (same numbers as RF). The Geothermal Generator's lava scan is an
  iterative flood fill (1.10.2 recursed and could overflow the stack on big lakes); the Thermic Generator re-counts
  its bricks every second (1.10.2 only on neighbour changes, so sky changes went unnoticed). The Tool Charger charges
  any item with the NeoForge energy capability (1.10.2: any CoFH RF item).
- Tools (phase 6): Fortune/Silk Touch perks recompute the block's loot with an enchanted copy of the tool, so they
  follow modern loot tables (1.10.2 used block methods; Auto-Smelt + Fortune now smelts the fortune-boosted drops).
  Area/capitator blocks are broken through the player's normal block breaking (protection, events, drops, wear).
  Tree felling is capped at 256 blocks. Damage never reaches max: tools stop at their "destroyed" threshold.
  The RF miner stores FE in a component and shows a charge bar (1.10.2 abused the damage bar).
- Armor (phase 7): vanilla armor attributes carry the base protection. Advanced armor uses its 1.10.2 armor-bar
  values (helmet 3, chest 8, legs 6, boots 3, toughness 2; none when destroyed) instead of the material's {4, 10, 7,
  4}, which only fed the old `ISpecialArmor` ratio. Perks add their 1.10.2 share (General +1/+2/+1/+1, typed +2/+3/+2/
  +2, out of 25) as an extra cut in `LivingIncomingDamageEvent`, before vanilla armor. Wear per hit comes from
  `ArmorHurtEvent`: 1 with a matching perk, 2 without, 0 when destroyed. Advanced armor opens its inventory (coal +
  perk-slot GUI, like the Advanced tools) on sneak + right-click; plain right-click refuels and equips it.
  Respiration/No Fall run in the worn item's inventory tick.
- JEI (phase 8): categories for the Coal Refiner (catalysts: Coal Refiner and Advanced Coal Refiner; the separate
  1.10.2 ACR category showed the same recipes twice side by side), Atom Extractor, Chemistry Machine and Crafting
  Machine (shaped + shapeless), drawn from the 1.10.2 GUI textures and read from the Java recipe tables. Click areas
  kept. Description pages for the AOR and ChM (1.10.2 text) plus new ones for the ACR and the three no-GUI energy
  machines. Not ported: JEI recipe transfer (the "+" button that moves items into the machine).
- Chemistry Machine: each element group must be on its own brick (1.10.2 only enforced this for consecutive groups;
  the JEI description already said one element type per brick).


Balance pass (3.0.0, decided by Rares): the six "balance pass" rows below were changed. Kept on purpose: the cheap
graphite → diamond recipe (about 3 anthracite coal per diamond) and the Chemistry Machine's gold → blaze rods →
emerald chain; they are part of the mod's identity. Revisit with player feedback.

| Item | Decision |
|---|---|
| GUI energy short overflow (GG, TC) | fixed (technical) |
| ACR formation at coordinate 0 | fixed (technical) |
| AOR/experience perk localized "Ore" name check | fixed via `#c:ores` (technical) |
| ChM trigger via `onNeighborChange` | fixed via `neighborChanged` (technical) |
| CrM wipes enchantments/data when applying a perk | fixed: only the perk is replaced (technical); one-perk limit kept |
| Coal item rarity check always COMMON | balance pass: anthracite uncommon, as intended |
| Reinforced Miner tool classes pickaxe + axe | ported as pickaxe + shovel (the item's intent, matches its mining speed) |
| Redstone tools burn only fuel slot 0 | balance pass: all 4 fuel slots burn, in order |
| Adv pickaxe/shovel capitator never fires | balance pass: column of matching blocks, like the Redstone Miner |
| Armor bypass perk not implemented | balance pass: armor reduces only 2/3 of a melee hit (NeoForge `DamageContainer` armor modifier) |
| Reinforced armor legs > chest | balance pass: {2, 6, 5, 2} like vanilla iron |
| New coals not vanilla furnace fuel | balance pass: 1.5×/2×/3× coal (`cookingFuel` + `context_int_provider`) |
| Ore fortune table | vanilla ore formula (can't port 1:1) |
| Advanced armor absorption math | approximated (can't port 1:1) |
| Material-based tool effectiveness | tags (can't port 1:1) |
| Ore gen config file | datapack overrides |
