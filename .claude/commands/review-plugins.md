Read every file in `plugins/*.md` and produce a comprehensive audit:

1. **Summary Table** — For each mod found, list: Name, Category (filename), CurseForge ID, Status, Conflicts field. Format as a markdown table.

2. **Statistics** — Count total mods by status (`included`, `considering`, `rejected`) and by category.

3. **Issues** — Flag:
   - Mods missing any required field (CurseForge ID, Slug, Status, Summary, Why, Conflicts).
   - Mods that appear in more than one category file (duplicates).
   - Mods with `Status: included` that have a non-empty Conflicts field — these need attention.
   - Mods with `Status: considering` that have been sitting without resolution (note them for review).

4. **Mod Loader Check** — If mod loader info is present, flag any mods that don't match the pack's target loader (NeoForge for 1.21.1, unless changed in CLAUDE.md).

Print the full report. If there are zero mods yet, say so and remind the user to start adding mods.