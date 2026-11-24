# Changelog

All notable changes to GeoActivity. Dates are given where the git history shows them (from 2016 on).

## [3.0.0] – Unreleased (Minecraft 26.3, NeoForge)

The whole mod ported to a current Minecraft. Gameplay is kept the same as 2.4, apart from the balance changes below.

### Changed
- Updated to Minecraft 26.3 and NeoForge 26.3 (Java 25), from Minecraft 1.10.2 and Forge.
- Energy is now FE (Forge Energy). It has the same numbers as RF and works with RF mods.
- Every block and item has its own ID now (the old metadata variants were split up, e.g. `lignite_ore`,
  `hydrogen_element_container`, `speed_perk`). **Worlds from 2.4 and earlier can't be loaded.**
- Ore generation numbers stay the same; the old `geoactivity.cfg` is replaced by datapack overrides.
- Recipes that used OreDictionary names now use item tags (e.g. any `c:gems/diamond`).
- Everything from 2.4 is in: ores and world generation, all items, the 10 machines (including both multiblocks and
  the 4 energy machines), all tools and their perks, the Auto Stone Builder, Reinforced and Advanced armor with armor
  perks.
- Advanced armor protection works through the normal armor system now, with perks adding extra protection on top,
  so the exact numbers differ slightly from 2.4.
- JEI support: recipe pages for the Coal Refiner, Atom Extractor, Chemistry Machine and Crafting Machine, plus
  description pages for the multiblocks and energy machines. Moving items into a machine from JEI (the "+" button) is
  not supported yet.

### Balance
- Lignite, Bituminous and Anthracite Coal now burn in vanilla furnaces: 1.5×, 2× and 3× as long as coal (twice as
  fast in smokers and blast furnaces). The mod's machines and tools keep their own fuel values.
- Reinforced armor: the chestplate now gives 6 armor and the leggings 5, like iron armor (in 2.4 the leggings gave
  more than the chestplate).
- Redstone tools now burn coal from all 4 fuel slots (only the first one worked before).
- The Capitator perk now works on the Advanced Pickaxe and Advanced Shovel: it mines the column of matching blocks
  above and below.
- The Armor Bypass perk now works: a third of a melee hit ignores the target's armor (in 2.4 it only had a tooltip).
- Anthracite Coal shows as uncommon.

### Fixed
- Energy values above 32k showed wrong in the Geothermal Generator and Tool Charger GUIs.
- The Advanced Coal Refiner didn't form at coordinate 0.
- The Advanced Ore Refiner and the Experience perk depended on the game language to recognise ores.
- The Crafting Machine wiped a tool's enchantments when adding a perk, and could crash when adding Efficiency to armor.
- An interrupted Crafting Machine craft could lose its result.
- The Geothermal Generator could crash the game on very big lava lakes.
- The Advanced Coal Refiner and Advanced Ore Refiner could overfill output stacks, and items in an Advanced Coal
  Refiner were deleted when it became part of an Advanced Ore Refiner.
- The Chemistry Machine didn't always notice the coal block being placed on top.

## [2.4] – 2016-07-10 – Minecraft 1.10.2

### Changed
- Updated to MC 1.10.2 and Forge 1.10.2-12.18.0.2010.

## [2.3.2] – 2016-07-10

### Fixed
- Tools not dealing damage properly.
- Tools not using attack speed properly.
- Tools not mining blocks properly.
- Atom Extractor and Thermic Generator not being updated properly.

## [2.3.1] – 2016-07-09 – Minecraft 1.9.4

### Changed
- Updated to MC 1.9.4 and Forge 1.9.4-12.17.0.1976.

### Fixed
- Item models not being displayed properly.
- Various other small bugs.

## [2.3] – 2016-04-23 – Minecraft 1.9

### Changed
- **Alpha version, expect bugs.**
- Updated to MC 1.9 and Forge 1.9-12.16.0.1865.

## [2.2.3] – 2016-02-21

### Fixed
- Crash on the client side.
- Various other fixes.
- Updated to the official RF API for MC 1.8.

## [2.2.2] – 2016-02-21

### Changed
- Added JEI support for the Crafting Machine, Atom Extractor, Coal Refiner, Advanced Coal Refiner and Chemistry
  Machine.
- Added JEI descriptions for multiblocks such as the Chemistry Machine and Advanced Ore Refiner.
- Nerfed the experience output of Coal Refiner recipes.

### Fixed
- Server side crash.
- Advanced Armor not absorbing damage properly.
- Advanced Ore Refiner not working with certain ores.

## [2.2.1] – 2016-02-19

### Changed
- Huge refactoring of package names and the GUI handler.
- Added JEI support for the Crafting Machine recipes, with more machines to come.

## [2.2] – Minecraft 1.8.9

### Changed
- Updated to MC 1.8.9 and Forge 11.15.1.1722.

### Fixed
- Small fixes for Redstone Tools not mining properly.

## [2.1.1]

### Fixed
- Coal ores not dropping the proper item.
- Other small bug fixes.

## [2.1]

### Changed
- Updated to the recommended Forge 11.14.3.1450.
- Added the Geothermal Generator:
  - needs to be placed in lava pools and generates 4 RF/t per lava block;
  - works better at lower altitudes;
  - scans for lava blocks recursively up to 25 meters from the generator, starting from its sides only;
  - outputs power to the bottom and top only;
  - has a 40% chance to turn lava into obsidian in a 3x3x3 cube centered on it, so it will eventually stop working.
- Added the Thermic Melter:
  - turns obsidian into lava in a 5x5x5 cube centered on it;
  - uses 10 RF/t passively and 50k RF per obsidian block.

### Fixed
- Implemented proper interfaces for machines that use RF.
- Fixed the Advanced Redstone BattleMiner GUI and added detailed power information to the power bar.

## [2.0] – Minecraft 1.8

### Changed
- Updated to MC 1.8 and Forge 11.14.1.1334.
- Increased the Advanced Redstone BattleMiner energy capacity to 5 million RF and its transfer rate to 20k RF/t.
- The Chemistry Machine does **not** give back coal or the element containers.
- The Tool Charger can now charge RF tools with up to 20k RF/t and can hold 10 million RF.

### Fixed
- Lots of bug fixes.
- Performance improvements.

## [1.6] – Minecraft 1.7.10

### Changed
- Updated to MC 1.7.10 and Forge 10.13.0.1199.
- Added the Advanced Redstone BattleMiner, an RF powered tool, more expensive than the Redstone BattleMiner:
  - can be upgraded with up to 12 perks;
  - supports up to 4 Wider Mining Radius perks, which increase the radius to 3x3x1, 5x5x1, 7x7x2 and 9x9x3.
- Added the Thermal Hardened Brick, used together with the Thermic Generator to produce RF: place the bricks next to
  the generator and it makes 20 RF/t per brick. Power can only be extracted through the bottom.
- Added the Tool Charger, which accepts up to 10000 RF/t and can only charge RF powered Redstone Tools.

## [1.5]

### Changed
- Updated to Forge 10.12.1.1060.
- Added more useful recipes to the Chemistry Machine.

### Fixed
- Added back compatibility with NEI.

## [1.4]

### Changed
- Updated to Forge 10.12.0.1047.
- Advanced Tools can now be enchanted. Enchantments do not stack with perks of the same type or with other perks
  (like Fortune with Auto-Smelt), unlike in the Redstone Tools.
- Decreased the enchantability of Advanced Tools and nerfed their fuel values, since they are pretty OP when enchanted.
- Buffed fuel values in Redstone Tools.
- Added config options for ore generation.
- Added the Drop Teleportation Perk: bind it to an inventory, put it in a tool, and drops go straight into that
  inventory instead of on the ground (unless the inventory is full).
- Added the Bypass Armor Perk, which makes 1/3 of the tool damage bypass armor.
- Added Advanced Armor, which can be customized with perks and works like the Advanced Tools.
- Added armor perks: General Protection, Blast Protection, Fire Protection, Magic Protection, Projectile Protection,
  No Fall Damage and Respiration.

### Fixed
- Tweaked tool damage.

## [1.3] – Minecraft 1.7.2

### Changed
- Updated to MC 1.7.2 and Forge 10.12.0.1034.
- Nerfed the Coal Refiner so that the Advanced Coal Refiner is useful.

### Fixed
- Small bug fixes and improvements.

## [1.2]

### Changed
- Changed some recipes for better balance.
- Added the block of coal as a fuel source and changed fuel burn times in the Crafting Machine.

### Fixed
- More tool bug fixes.

## [1.1]

### Fixed
- Tools work properly now.
- Lots of bug fixes.

## [1.0]

### Changed
- Removed Crafting Machine I and moved its recipes to the vanilla workbench.
- Removed Magma Ore and nerfed the generation of the coal ores.
- The new coal ores can no longer be used as fuel.
- Removed the coal exchange recipes from the Crafting Machine.
- Coal can be crafted from 4 graphite in a square.
- Diamonds have a new recipe.
- The Coal Refiner has some new recipes; coal ores give double output in it.
- New Advanced Ore Refiner, a complicated multiblock that acts like a furnace: it doubles your output with a chance to
  triple it, or triples it with a chance to quadruple it if you use a certain special item. Works only on ores that
  drop themselves.
- New Auto Stone Builder:
  - auto smelts and/or 2x2 crafts one block into a specific block, then places that block in the world (useful for
    turning cobblestone straight into stone or stone bricks);
  - has an internal capacity of 64 blocks and uses coal to recharge.
- New Redstone Miner (pickaxe + shovel) and Redstone BattleAxe (sword + axe + hoe), both with up to 4 perks and 4 fuel
  slots.
- New Advanced Redstone BattleAxe that acts like a pickaxe, shovel, axe, hoe and sword in one tool (up to 8 perks).
  **Important:** do not apply perks for these tools in the Crafting Machine. They will not work and will waste your
  perk.
- Reinforced Tools can no longer mine obsidian.
- Advanced Tools are no longer destroyed completely. Instead a tooltip shows when they are destroyed, and a destroyed
  tool can't be used to mine or attack.
- Buffed fuel values in the Advanced Tools.
- Tool damage slightly nerfed.
- Added the Damage, Experience, Fortune, Silk-Touch, Auto-Smelt, Wider Radius, Capitator and No-Drops perks.
- Perks now have a tooltip saying which items they work on (you can put a perk on other items, but it won't do
  anything).
- Added a higher tier of Precious Alloy Ingots.
- Changed some recipes for better balance with other mods.
- Added Chemistry:
  - new Atom Extractor machine; its speed can be upgraded with up to 4 Advanced Reinforced Bricks placed next to it;
  - new Element Containers, representing elements of the periodic table (currently 19);
  - new Chemistry Machine, used to combine elements and put them into other items. It is a multiblock: the machine
    in the middle, 4 Advanced Reinforced Bricks in the corners and item frames on the bricks. Put elements in the item
    frames and 1 or 2 items in the machine's GUI, then place a coal block on top to craft. You get the output and some
    coal back, and the element containers used become empty.

### Fixed
- Balanced the mod to work better with other mods installed.
- Added an NEI plugin: with NEI installed you can look up recipes for the mod's machines.
- Lots of bug fixes.
- The Advanced Coal Refiner now has a different texture when it is formed.
- Machines can now be partially automated.

## [0.7.1]

### Changed
- Changed the Advanced Coal Refiner recipe.
- The Reinforced Miner can now be crafted with damaged tools.

## [0.7]

### Changed
- Added a creative tab.
- Added the Advanced Coal Refiner multiblock.
- Added Machine Perks.
- Added chest loot.
- Added the Small Precious Alloy Ingot.
- Changed the recipes of the Advanced Tools.
- New textures.

### Fixed
- Strange Coal Refiner rotation bug.
- Tools can now be upgraded with perks after they are first crafted.

## [0.6.3]

### Fixed
- Crash on multiplayer.

## [0.6]

### Changed
- Added the Graphite Base, the base block for all advanced machines.
- Added Crafting Machine II.
- Added a new tier of tools that run on coal and can be upgraded with perks.
- Added a perk system (currently speed and efficiency perks).

## [0.5] – Minecraft 1.6.2

### Changed
- Updated the config file.
- Added a new tool, the Reinforced Miner: a pickaxe and a shovel in one that uses the new types of coal to recharge.
- Updated to MC 1.6.2 and Forge 9.10.0.789.
- Bug fixes.

## [0.4] – Minecraft 1.5

### Changed
- Changed Hardened Stone to Hardened Brick.
- Updated textures.
- Updated to MC 1.5 and Forge 7.7.0.591.
- Bug fixes.
