The user wants to add one or more mods to the FizzleSMP modpack. The mod name(s) will be provided as arguments: $ARGUMENTS

For each mod provided, perform these steps in order:

## 1. Identify the Mod
- Search CurseForge (or the web) for the exact mod by name.
- Confirm the CurseForge project ID, slug, and correct display name.

## 2. Version Compatibility Check
- Verify the mod has a release for **Minecraft 1.21.1** on **Fabric**.
- If it does: mark it as **compatible** and proceed to step 3.
- If it does NOT:
  - State clearly that the mod is not available for 1.21.1 Fabric.
  - Note the latest Minecraft version it does support.
  - Search for **alternative mods** that serve the same purpose and ARE available for 1.21.1 Fabric. Suggest up to 3 alternatives with their names, CurseForge slugs, and a one-line description.
  - Ask the user if they'd like to add any of the alternatives instead, then stop processing this mod.

## 3. Dependency Check
- Look up the mod's required dependencies for 1.21.1 Fabric.
- For each dependency:
  - Check if it is already listed as `included` in any `plugins/*.md` file.
  - If missing, note it as a **new dependency that must also be added**.
- List all dependencies (already included and newly required).

## 4. CurseForge Page Compatibility Scrape
- Fetch the mod's CurseForge page (and any linked wiki/docs) using WebFetch.
- Look for any mentioned incompatibilities, known conflicts, or "works well with" notes from the mod author.
- Look for any compatibility notes in the mod description, changelog, or FAQ sections.
- Record all findings — both positive (verified compatible) and negative (conflicts).

## 5. Conflict Pre-Check & Matrix Update
- Read `docs/compatibility-matrix.md` for any known conflicts with this mod.
- Read all `plugins/*.md` files and check if any `included` mod is known to conflict.
- Flag any hard or soft conflicts found.
- **If a hard conflict exists with any `included` mod, STOP.** Do not add the mod. Instead:
  - Explain the conflict clearly.
  - Suggest alternatives that serve the same purpose without the conflict.
  - Ask the user if they want to replace the conflicting mod or skip the new one.
  - Only proceed once the conflict is resolved (conflicting mod removed/replaced or user explicitly overrides).
- **Update `docs/compatibility-matrix.md`** with any new information gathered:
  - Add entries from the CurseForge page scrape (step 4) to the appropriate section (Hard Conflicts, Soft Conflicts, or Verified Compatible).
  - For each `included` mod in the pack, if the CurseForge page mentions compatibility with it, add a Verified Compatible entry.
  - If the CurseForge page mentions conflicts with any mod (whether in our pack or not), add it to the appropriate conflict section for future reference.

## 6. Categorize
- Determine which `plugins/<category>.md` file the mod belongs in based on its primary function.
- If unclear, ask the user which category to place it in.

## 7. Add to Plugin File
- Add a properly formatted entry to the appropriate `plugins/<category>.md` file:
  ```
  ## Mod Name
  - **CurseForge ID:** <project-id>
  - **Slug:** <curseforge-slug>
  - **Mod Loader:** Fabric
  - **Status:** included
  - **Summary:** One-line description.
  - **Why:** <ask the user or infer from context>
  - **Dependencies:** List or "None"
  - **Conflicts:** Any found issues or "None known"
  ```
- Also add entries for any new dependencies using the same format, with **Why** set to "Required dependency of <parent mod>".

## 8. Summary Report
After processing all mods, print a summary:
- **Added:** mods successfully added (with categories).
- **Dependencies added:** any new dependencies pulled in.
- **Incompatible:** mods that couldn't be added, with suggested alternatives.
- **Conflicts found:** any issues that need attention.
- Remind the user to run `/check-conflicts` for a full cross-reference and to regenerate `curseforge/minecraftinstance.json` when ready.