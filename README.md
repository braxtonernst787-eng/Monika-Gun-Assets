# Monika Guns

An unofficial addon for **Minecraft Forge 1.20.1** that lets **Monika** from *Monicraft* use **TACZ** (Timeless and Classics Zero) guns.

Not affiliated with the authors of Monicraft or TACZ.

## Requirements
- Minecraft 1.20.1 with Forge 47.x
- [TACZ](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero) for 1.20.1
- Monicraft 1.0.0

## Install
Put the `monika_guns` jar in your `mods` folder alongside TACZ and Monicraft.

## How to use
1. Spawn Monika.
2. Put a **full** TACZ gun in her **main hand** (for example, a gun taken from the TACZ creative tab).
3. When she targets a mob, she aims and shoots it with the gun.

## How it works
Monika extends `PathfinderMob` and already chooses her own targets. This mod adds one extra AI goal, with top priority, that fires the TACZ gun in her main hand at her current target. It aims from her eyes to the middle of the target's body.

## Known limitations
- She never shoots players, but will shoot anything else she targets.
- She ignores her companion mode (stay, follow, etc.) while fighting.
- Ammo is simulated, not real: she learns the gun's magazine size from the ammo it has when she first uses it, pauses about 2 seconds when empty, then refills. Give her a full gun.
- She may swap her gun for a bow if she has bows or arrows in her inventory.
- Built against one TACZ version; future TACZ updates may change the API and break this addon.

## Building from source
1. Install JDK 17.
2. Put the TACZ and Monicraft jars in `libs/`, renamed `tacz-1.0.0.jar` and `monicraft-1.0.0.jar` (see `libs/PUT_JARS_HERE.txt`).
3. Run `gradlew build`. The jar is in `build/libs/`.
4. `gradlew runClient` starts a dev game. If TACZ's mixins fail there, test with the built jar in a normal Forge 1.20.1 install instead.

## License
MIT. See `LICENSE`.
