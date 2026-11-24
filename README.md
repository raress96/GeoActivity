# GeoActivity - customizable tools/armor, ore quadrupling and more

![Image](http://i.imgur.com/0KtR3do.png)

## Downloads for the latest versions can be found on [CurseForge](http://minecraft.curseforge.com/projects/geoactivity).

Version 3.0.0 is for **Minecraft 26.3** with **NeoForge**. [JEI](https://www.curseforge.com/minecraft/mc-mods/jei) is
optional but recommended: it shows the recipes of every machine. Worlds from 2.4 and older (Minecraft 1.10.2) can't be
loaded. See the [changelog](CHANGELOG.md) for what changed.

## Features
- 3 new coal types
- new armor/tools sets that can be customized
- new blocks
- new machines
- Chemistry
- FE (RF-compatible) generators and powered tools

## General Information
This mod adds 3 new different coal ores to the game:

Anthracite Ore - spawns on layers 18-38 in veins of maximum 3 blocks, more difficult to find

Bituminous Ore - spawns on layers 34-48 in veins of maximum 3 blocks, not very hard to find

Lignite Ore - spawns on layers 46-64 in veins of maximum 3 blocks, somewhat common

The mod adds a variety of modular tools that can be upgraded with Perks. There are also tools that act like a pickaxe
and shovel in one and a tool which is a pickaxe, shovel, sword, axe and hoe in one. Because they are modular, you can
put any Perk you want on them.

Perks range from Wider Radius, Fortune to No Drops and Auto-Smelt. There are currently over 10 Perks available.

We also have implemented a chemistry system, with atoms and molecules. Currently there are 19 elements that this mod
adds. These elements are used in a Chemistry Machine to craft various items. Elements are extracted from items using
the Atom Extractor.

There is also a multiblock furnace that can quadruple your resources if used correctly. Be sure to check it out!

The mod is compatible with JEI, so be sure to have that installed if you want to browse the recipes available for each
machine. To install, place the .jar file in the `mods` folder of a NeoForge 26.3 instance.

## Mod Showcase (v1.4)
https://www.youtube.com/watch?v=hbpYyVw6cqk

## Development
Needs a JDK 21+ to run Gradle; Java 25 is downloaded automatically for the build.

```bash
./gradlew build                # jar in build/libs/
./gradlew runGameTestServer    # in-game tests
./gradlew runClient            # dev client with JEI
```

[`docs/GAMEPLAY_SPEC.md`](docs/GAMEPLAY_SPEC.md) documents every mechanic and number. [`CLAUDE.md`](CLAUDE.md) has the
code layout and conventions.

##### This mod is licensed under the [Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International license](http://creativecommons.org/licenses/by-nc-sa/4.0/).
