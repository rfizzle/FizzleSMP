Read every file in `plugins/*.md` and produce a comprehensive audit:

1. **Summary Table** — For each mod found, list: Name, Category (filename), CurseForge ID, Conflicts field. Format as a markdown table.

2. **Statistics** — Count total mods by category.

3. **Issues** — Flag:
   - Mods missing any required field (CurseForge ID, Slug, Summary, Why, Conflicts).
   - Mods that appear in more than one category file (duplicates).
   - Mods with a non-empty Conflicts field — these need attention.

4. **Mod Loader Check** — If mod loader info is present, flag any mods that don't match the pack's target loader (Fabric for 1.21.1, unless changed in CLAUDE.md).

Print the full report. If there are zero mods yet, say so and remind the user to start adding mods.