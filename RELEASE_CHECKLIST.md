# Release checklist

## Before you post
- [ ] Copy `gradlew`, `gradlew.bat`, `settings.gradle` and the `gradle/` folder from your working project into this folder (the Gradle wrapper makes the project buildable by others).
- [ ] Ask Monicraft's authors (nownetted / CGT) if they are fine with an addon. Monicraft is "All Rights Reserved".
- [ ] Check TACZ's license/rules page for addon requirements.
- [ ] Build: Gradle panel > Tasks > build > clean, then build.
- [ ] Test in a clean Forge 1.20.1 profile with only: Monika Guns, TACZ, Monicraft.
- [ ] Confirm Monika fires, stays inside her magazine size, and does not shoot you.

## Files to upload
- `build/libs/monika_guns-1.0.0.jar`  -> the mod file (CurseForge / Modrinth)
- `build/libs/monika_guns-1.0.0-sources.jar` -> optional sources jar

## CurseForge / Modrinth form
- Name: Monika Guns
- Game version: 1.20.1
- Loader: Forge
- Dependencies: Monicraft (required), TACZ (required)
- License: MIT
- Description: copy the first sections of README.md

## GitHub
- Create a repository, upload this folder (the .gitignore keeps jars and build files out).
- Do NOT commit the TACZ or Monicraft jars.
