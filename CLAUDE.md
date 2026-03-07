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

## Conventions

- Always use CurseForge slugs and project IDs — never rely on display names alone.
- Keep one mod per `## Heading` block in the plugin files.
- When web-searching for mod info, verify compatibility with **Minecraft 1.21.1** specifically.
- Prefer mods with active maintenance (updated within the last 6 months).
- The target mod loader is **Fabric** — flag any mods that don't support it.