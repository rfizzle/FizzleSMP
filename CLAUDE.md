# FizzleSMP — Minecraft 1.21.1 CurseForge Modpack

## Project Overview

This repository defines **FizzleSMP**, a curated Minecraft 1.21.1 modpack distributed via CurseForge. The project contains:

- Plugin/mod lists organized by category (`plugins/`)
- A feature brainstorming document that maps ideas to mods (`docs/features-brainstorm.md`)
- A compatibility matrix tracking known conflicts (`docs/compatibility-matrix.md`)
- The CurseForge instance definition file (`curseforge/minecraftinstance.json`)

## Target Platform

- **Minecraft version:** 1.21.1
- **Mod loader:** Fabric
- **Distribution:** CurseForge modpack

## Directory Structure

```
FizzleSMP/
├── CLAUDE.md                       # This file — project rules and context
├── plugins/
│   ├── performance.md              # Performance & optimization mods
│   ├── worldgen.md                 # World generation & terrain mods
│   ├── gameplay.md                 # Core gameplay mechanics & content
│   ├── combat.md                   # Combat overhauls & balancing
│   ├── social.md                   # Social, chat, & community features
│   ├── economy.md                  # Economy, trading, & shops
│   ├── protection.md               # Land claims, anti-grief, & security
│   ├── admin.md                    # Server admin & management tools
│   └── utility.md                  # QoL, HUD, minimap, & misc utilities
├── docs/
│   ├── features-brainstorm.md      # Feature wishlist → mod mapping
│   └── compatibility-matrix.md     # Known conflicts & compatibility notes
├── curseforge/
│   └── minecraftinstance.json      # CurseForge modpack instance config
└── .claude/
    └── commands/
        ├── review-plugins.md       # /review-plugins — audit the full mod list
        ├── check-conflicts.md      # /check-conflicts — find potential conflicts
        └── add-mods.md             # /add-mods — add mods with version/dependency checks
```

## Plugin File Format

Each file in `plugins/` uses this format:

```markdown
# Category Name

## Mod Name
- **CurseForge ID:** <project-id>
- **Slug:** <curseforge-slug>
- **Mod Loader:** Fabric
- **Status:** included | considering | rejected
- **Summary:** One-line description of what it does.
- **Why:** Why we want it in the pack.
- **Dependencies:** Required dependencies (or "None").
- **Conflicts:** Any known incompatibilities (or "None known").
```

When adding or editing mods, always populate all fields. Use `Status: considering` for mods not yet confirmed.

## Compatibility Matrix Rules

`docs/compatibility-matrix.md` tracks:
- **Hard conflicts** — mods that crash or break when loaded together.
- **Soft conflicts** — mods that overlap in functionality or have config clashes.
- **Verified compatible** — mod pairs explicitly tested together.

When adding a new mod, check it against every existing `included` mod and update the matrix.

**Scope rule:** Only track conflicts and compatibility entries between mods that are `included` or `considering` in the pack. Do not add entries for mods that are not in the pack — the matrix should reflect the actual mod list, not hypothetical external conflicts.

**Hard conflict rule:** A mod must NOT be added with `Status: included` if it has a hard conflict with any existing `included` mod. If a hard conflict is found, either reject the new mod, remove/replace the conflicting mod first, or resolve the conflict before proceeding. This rule applies to all workflows — manual edits, `/add-mods`, and status changes.

## CurseForge Instance Config

`curseforge/minecraftinstance.json` is the machine-readable modpack definition. It follows the CurseForge manifest schema. When the plugin lists change, this file must be regenerated to stay in sync.

## Custom Commands

| Command | Purpose |
|---------|---------|
| `/review-plugins` | Reads all `plugins/*.md` files, prints a summary table of every mod (name, status, category, conflicts), and flags any issues (missing fields, duplicates across categories). |
| `/check-conflicts` | Reads the plugin lists and compatibility matrix, cross-references all `included` mods, and reports potential conflicts — both known (from the matrix) and suspected (overlapping functionality). |
| `/add-mods <mod names>` | Verifies 1.21.1 Fabric compatibility, resolves dependencies, checks for conflicts, and adds the mod to the correct category file. Suggests alternatives if incompatible. |

## Workflow

1. **Brainstorm** — Add feature ideas to `docs/features-brainstorm.md`.
2. **Research** — Find mods that fulfill those features; add to the appropriate `plugins/*.md` file with `Status: considering`.
3. **Evaluate** — Run `/check-conflicts` to surface issues. Update `docs/compatibility-matrix.md`.
4. **Decide** — Change status to `included` or `rejected`.
5. **Build** — Regenerate `curseforge/minecraftinstance.json` from all `included` mods.

## Git Commit Workflow

This project uses **Conventional Commits** for all commit messages.

### Commit Message Format

```
<type>(<scope>): <short summary>
```

- **type** — One of the following:
  - `feat` — New mod added, new feature idea, or new documentation section
  - `fix` — Correcting a conflict entry, fixing a broken field, or resolving an error
  - `docs` — Changes to `CLAUDE.md`, `features-brainstorm.md`, or other documentation
  - `chore` — Maintenance tasks (regenerating `minecraftinstance.json`, reorganizing files)
  - `refactor` — Restructuring plugin files or docs without changing content
  - `revert` — Reverting a previous commit
- **scope** (optional) — The area of the project affected. Common scopes:
  - `plugins` — Changes to any `plugins/*.md` file
  - `worldgen`, `combat`, `economy`, etc. — Specific plugin category
  - `compat` — Changes to `compatibility-matrix.md`
  - `brainstorm` — Changes to `features-brainstorm.md`
  - `curseforge` — Changes to `minecraftinstance.json`
- **short summary** — Imperative, lowercase, no period at the end

### Examples

```
feat(plugins): add Lithium to performance mods
fix(compat): correct hard conflict between Create and Sodium
docs(brainstorm): add custom enchantments feature idea
chore(curseforge): regenerate minecraftinstance.json
feat(combat): add Better Combat with status included
refactor(plugins): split utility mods into utility and admin
```

### Rules

- Keep the summary line under 72 characters.
- Use the imperative mood ("add", not "added" or "adds").
- Stage only the files relevant to the change — avoid catch-all `git add .`.
- One logical change per commit. If adding a mod touches `plugins/`, `docs/compatibility-matrix.md`, and `curseforge/minecraftinstance.json`, commit them together as one `feat` commit.

## Modrinth API Access

The project uses the [Modrinth API v2](https://docs.modrinth.com/) for mod lookups. No authentication required. Do **not** use Python — use `curl` and `jq` exclusively.

### Base URL

```
https://api.modrinth.com/v2
```

### Useful Endpoints

#### Get Project (by slug or ID)

```bash
curl -s "https://api.modrinth.com/v2/project/<slug>"
```

Key response fields: `.id`, `.slug`, `.title`, `.description`, `.game_versions`, `.loaders`, `.source_url`.

#### Search Mods

```bash
curl -s "https://api.modrinth.com/v2/search?query=<name>&facets=[[%22categories:fabric%22],[%22versions:1.21.1%22],[%22project_type:mod%22]]"
```

Key response fields: `.hits[].project_id`, `.hits[].slug`, `.hits[].title`, `.hits[].description`.

#### Get Project Versions (filtered by version + loader)

```bash
curl -s "https://api.modrinth.com/v2/project/<slug>/version?game_versions=%5B%221.21.1%22%5D&loaders=%5B%22fabric%22%5D"
```

Returns an array of version objects. Each includes a `dependencies` array with `project_id` and `dependency_type` (`required`, `optional`, `incompatible`, `embedded`).

#### Get Multiple Projects at Once

```bash
curl -s "https://api.modrinth.com/v2/projects?ids=%5B%22id1%22,%22id2%22%5D"
```

### Typical jq Patterns

```bash
# Extract key fields from a project
jq '{id, slug, title, description}' /tmp/project.json

# Check if a mod supports 1.21.1
jq '.game_versions | map(select(. == "1.21.1"))' /tmp/project.json

# Check if a mod supports Fabric
jq '.loaders | map(select(. == "fabric"))' /tmp/project.json

# Get dependencies from a version
jq '.[0].dependencies[] | {project_id, dependency_type}' /tmp/versions.json

# Search results extraction
jq '.hits[] | {project_id, slug, title, description}' /tmp/search.json
```

### CurseForge ID Cross-Reference

Plugin files still track CurseForge project IDs and slugs (for the CurseForge modpack manifest). The CurseForge website blocks direct `curl`/`WebFetch` requests (403), so use this fallback chain:

1. **CFWidget API** (preferred for bulk lookups) — Returns full project metadata including the numeric project ID. Works with both CurseForge slugs and numeric IDs:

   ```bash
   # Look up by CurseForge slug
   curl -s "https://api.cfwidget.com/minecraft/mc-mods/<slug>" | jq '{id, title}'

   # Verify a numeric ID
   curl -s "https://api.cfwidget.com/<numeric-id>" | jq '{id, title}'
   ```

   > **Note:** CFWidget has rate limits. When looking up many mods, add a small delay (`sleep 1`) between requests. Responses may be empty if rate-limited — retry after a brief pause.

2. **CFLookup** — [cflookup.com](https://cflookup.com/) resolves CurseForge slugs to project pages. Useful as a manual fallback but redirects to curseforge.com (so not directly fetchable via `curl`/`WebFetch`):

   ```
   https://cflookup.com/minecraft/mc-mods/<slug>
   ```

3. **WebSearch** — Search `"<mod name> Minecraft CurseForge project ID"` with `allowed_domains: ["curseforge.com"]`. The search results typically include the project ID in snippets or metadata.

4. **Mod's GitHub README** — Many mods link to their CurseForge page. Fetch the raw README and extract the URL; the project ID is sometimes in badge URLs or metadata.

5. **Modrinth project page** — Check the `.source_url` or description from the Modrinth API response for CurseForge links.

Do **not** attempt to `WebFetch` curseforge.com directly — it will return 403.

## Conventions

- Always use CurseForge slugs and project IDs — never rely on display names alone. Use Modrinth API for lookups, then cross-reference CurseForge IDs.
- Keep one mod per `## Heading` block in the plugin files.
- When web-searching for mod info, verify compatibility with **Minecraft 1.21.1** specifically.
- Prefer mods with active maintenance (updated within the last 6 months).
- The target mod loader is **Fabric** — flag any mods that don't support it.