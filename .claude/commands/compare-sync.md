Compare what's listed in `plugins/*.md` against what's actually installed in packwiz (`modpack/mods/` and `modpack/config/paxi/datapacks/`). Produce a sync status report:

1. **Parse plugin files** — Read all `plugins/*.md` files and extract every mod entry. Note each mod's name, CurseForge ID, Modrinth Slug, and Mod Loader (especially "Datapack" entries which go to `modpack/config/paxi/datapacks/`).

2. **Parse packwiz files** — Read all `.pw.toml` files from both `modpack/mods/` and `modpack/config/paxi/datapacks/`. Extract the mod name, CurseForge project-id (from `[update.curseforge]`), and Modrinth mod-id (from `[update.modrinth]`).

3. **Match mods** — Match plugin entries to packwiz files using CurseForge project-id or Modrinth slug/mod-id. A mod is "synced" if it has a corresponding `.pw.toml` file.

4. **Report** — Output three sections as markdown tables:

   **Synced** — Mods present in both plugins and packwiz (with plugin category and packwiz filename).

   **Missing from packwiz** — Mods listed in `plugins/*.md` but with no matching `.pw.toml` file. These need to be installed via `sync-packwiz.sh`.

   **Extra in packwiz** — `.pw.toml` files in `modpack/mods/` or `modpack/config/paxi/datapacks/` that don't match any mod in the plugin files. These may be unlisted dependencies or orphaned entries.

5. **Summary** — Total counts: synced, missing, extra. Flag if everything is in sync.