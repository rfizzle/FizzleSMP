# FizzleSMP — Minecraft 1.21.1 Fabric Modpack

## Project Overview

This repository defines **FizzleSMP**, a curated Minecraft 1.21.1 modpack managed with [packwiz](https://packwiz.infra.link/) and distributable to CurseForge and Modrinth. The project contains:

- Plugin/mod lists organized by category (`plugins/`) — the human-readable source of truth
- Packwiz metadata (`modpack/pack.toml`, `modpack/index.toml`, `modpack/mods/*.pw.toml`) — the machine-readable modpack definition
- A feature brainstorming document that maps ideas to mods (`docs/features-brainstorm.md`)
- A compatibility matrix tracking known conflicts (`docs/compatibility-matrix.md`)
- A server deployment guide (`docs/server-deployment.md`) for the live SMP server
- A changelog (`CHANGELOG.md`) following Keep a Changelog
- Release scripts (`scripts/release.sh`, `scripts/server-install.sh`) and a GitHub Actions workflow (`.github/workflows/release.yml`) for cutting versioned releases and updating the live server in place

## Target Platform

- **Minecraft version:** 1.21.1
- **Mod loader:** Fabric
- **Distribution:** CurseForge modpack

## Directory Structure

```
FizzleSMP/
├── CLAUDE.md                       # This file — project rules and context
├── modpack/                        # Packwiz modpack directory
│   ├── pack.toml                   # Packwiz pack definition (MC version, loader)
│   ├── index.toml                  # Packwiz file manifest (auto-generated)
│   ├── mods/                       # Packwiz mod metadata (one .pw.toml per mod)
│   │   ├── sodium.pw.toml
│   │   ├── lithium.pw.toml
│   │   └── ...
│   └── config/paxi/datapacks/      # Packwiz datapack metadata (.pw.toml)
│       └── ...
├── plugins/                        # Human-readable mod lists (source of truth)
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
├── scripts/
│   ├── sync-packwiz.sh             # Sync plugins/*.md → modpack/mods/
│   └── build-pack.sh               # Build client/server ZIP packs
└── .claude/
    └── commands/
        ├── review-plugins.md       # /review-plugins — audit the full mod list
        ├── check-conflicts.md      # /check-conflicts — find potential conflicts
        ├── compare-sync.md         # /compare-sync — compare plugins/ vs packwiz
        ├── add-mods.md             # /add-mods — add mods with version/dependency checks
        └── analyze-log.md          # /analyze-log — diagnose Minecraft logs & map to mods
```

## Plugin File Format

Each file in `plugins/` uses this format:

```markdown
# Category Name

## Mod Name
- **CurseForge ID:** <project-id>
- **Slug:** <curseforge-slug>
- **Modrinth Slug:** <modrinth-slug>
- **Pin CurseForge File ID:** <file-id> *(optional — only when version-pinning is needed)*
- **Mod Loader:** Fabric *(or `Manual` for custom builds not managed by packwiz)*
- **Side:** client | server | both
- **Summary:** One-line description of what it does.
- **Why:** Why we want it in the pack.
- **Dependencies:** Required dependencies (or "None").
- **Conflicts:** Any known incompatibilities (or "None known").
```

Side values:
- `client` — Only needed on the client (rendering, HUD, UI, animations, sounds, visual effects, recipe viewers).
- `server` — Only needed on the server (server-side logic, world gen in multiplayer, logging).
- `both` — Required on both sides (new items/blocks, combat systems, gameplay mechanics that need client+server code).

Only mods that belong in the pack should be listed. If a mod is rejected or removed, delete its entry entirely. When adding or editing mods, always populate all fields. Use `N/A` for **CurseForge ID** / **Slug** if the mod is Modrinth-only, or for **Modrinth Slug** if it is CurseForge-only. Only add the **Pin CurseForge File ID** field when a mod must be locked to a specific version (e.g., to avoid a dependency that conflicts with another mod). Document the reason in the **Conflicts** field and in `docs/compatibility-matrix.md`.

## Compatibility Matrix Rules

`docs/compatibility-matrix.md` tracks:
- **Hard conflicts** — mods that crash or break when loaded together.
- **Soft conflicts** — mods that overlap in functionality or have config clashes.
- **Verified compatible** — mod pairs explicitly tested together.

When adding a new mod, check it against every existing mod for conflicts. Only add matrix entries when there is a meaningful finding (see signal rule below).

**Scope rule:** Only track conflicts and compatibility entries between mods listed in the pack. Do not add entries for mods that are not in the pack — the matrix should reflect the actual mod list, not hypothetical external conflicts.

**Signal rule:** The matrix should only contain entries with real informational value. Do **not** add:
- **Dependency relationships** — these are already tracked in the plugin files' `Dependencies` field. A mod is obviously compatible with its own dependencies.
- **Trivial "no overlap" pairings** — if two mods operate in completely unrelated domains (e.g., a villager mechanic vs. a rendering optimizer, a storage mod vs. a game logic optimizer), there is no reason to track them. The absence of an entry already implies no known conflict.
- **Library/API pairings** — entries like "Mod X | Fabric API" add no information.

**Do** add entries when:
- Mods touch the **same system** and could plausibly conflict (worldgen + worldgen, rendering + rendering, accessory slots + accessory slots, inventory management + inventory management).
- Mods have an **explicit integration** or designed interaction (e.g., OPAC displays on Xaero's maps, Reforged applies modifiers to Simply Swords weapons).
- A mod is **universally compatible** with a whole category and it's worth noting once (e.g., "Lootr | All structure mods", "Noisium | All worldgen mods").

**Hard conflict rule:** A mod must NOT be added if it has a hard conflict with any existing mod. If a hard conflict is found, either skip the new mod, remove/replace the conflicting mod first, or resolve the conflict before proceeding.

**Add-mod conflict check:** When adding a new mod via `/add-mods`, always check for conflicts against **all** existing mods regardless of category. The matrix entries are limited to same-domain interactions, but the conflict *check* must be comprehensive. Use `/check-conflicts` for full cross-pack audits.

## Packwiz

[Packwiz](https://packwiz.infra.link/) manages the machine-readable modpack definition. Key files:

- **`modpack/pack.toml`** — Pack name, Minecraft version, Fabric loader version
- **`modpack/index.toml`** — Auto-generated manifest of all files with hashes
- **`modpack/mods/*.pw.toml`** — One metadata file per mod (download URL, hash, update tracking)

### Sync Script

`scripts/sync-packwiz.sh` syncs the `plugins/*.md` mod lists into packwiz:

```bash
./scripts/sync-packwiz.sh           # Install mods missing from packwiz
./scripts/sync-packwiz.sh --prune   # Also remove mods not in plugins/
./scripts/sync-packwiz.sh --dry-run # Preview changes without modifying anything
```

The script parses all `plugins/*.md` files and runs `packwiz curseforge install` (or `packwiz modrinth install` for Modrinth-only mods) for each mod not already in `modpack/mods/`. It matches by CurseForge project ID to avoid duplicates even when filenames differ.

### Common Packwiz Commands

Run these from the `modpack/` directory:

```bash
packwiz curseforge install <slug>   # Add a mod from CurseForge
packwiz modrinth install <slug>     # Add a mod from Modrinth
packwiz update --all                # Update all mods to latest versions
packwiz remove <mod>                # Remove a mod
packwiz list                        # List installed mods
packwiz refresh                     # Rebuild index.toml after manual edits
packwiz curseforge export           # Export CurseForge-format zip
packwiz modrinth export             # Export .mrpack for Modrinth
```

### Build Script

`scripts/build-pack.sh` downloads mods and packages them as ZIP files for distribution:

```bash
./scripts/build-pack.sh server              # Build FizzleSMP.server.zip
./scripts/build-pack.sh client              # Build FizzleSMP.client.zip
./scripts/build-pack.sh server --clean      # Wipe build dir, rebuild, and zip
./scripts/build-pack.sh client --dry-run    # Preview without downloading
```

The script reads `.pw.toml` metadata, filters by the `side` field (server pack excludes `client`-only mods, client pack excludes `server`-only mods), downloads via CurseForge edge CDN or Modrinth CDN, verifies hashes, and packages the result. Downloads are cached in `build/server/` or `build/client/` between runs.

### Workflow Integration

When adding a mod via `/add-mods`, the plugin file is the source of truth. **Do not** automatically run `sync-packwiz.sh` after adding mods — the user will trigger the sync manually when ready. The packwiz files (`modpack/pack.toml`, `modpack/index.toml`, `modpack/mods/`) should be committed alongside plugin changes when the sync is performed.

## Release Workflow

FizzleSMP is cut into versioned releases that ship to the live SMP server and to players.

### Versioning (pragmatic SemVer)

- **MAJOR** — world-breaking changes (worldgen or dimension mods added/removed). Existing worlds may need reset or migration.
- **MINOR** — additive changes (new mods, new content). Safe to apply to an existing world.
- **PATCH** — config tweaks, mod version bumps, bug fixes, compatibility fixes.

The version lives in `modpack/pack.toml` as the `version = "X.Y.Z"` field and is embedded in release artifact filenames (`FizzleSMP-client-X.Y.Z.zip`, `FizzleSMP-server-X.Y.Z.zip`).

### Cutting a release

```bash
./scripts/release.sh patch          # 1.2.3 → 1.2.4
./scripts/release.sh minor          # 1.2.3 → 1.3.0
./scripts/release.sh major          # 1.2.3 → 2.0.0
./scripts/release.sh 1.4.0          # explicit version
./scripts/release.sh patch --no-push # tag locally, do not push
```

The script checks that the working tree is clean and that the current branch is master, bumps `pack.toml`, refreshes the packwiz index, rolls `CHANGELOG.md`'s `[Unreleased]` section into a new `[X.Y.Z] - DATE` heading, commits as `chore(release): vX.Y.Z`, tags `vX.Y.Z`, and pushes both master and the tag to `origin`. Pushing the tag triggers the GitHub Actions release workflow.

### GitHub Actions release pipeline

`.github/workflows/release.yml` runs on any pushed tag matching `v*.*.*`. It:

1. Installs the `packwiz` CLI via `go install`.
2. Verifies `modpack/pack.toml`'s version matches the tag (fails if not — forces the use of `release.sh`).
3. Runs `./scripts/build-pack.sh client` → `FizzleSMP-client-X.Y.Z.zip` (CurseForge-compatible ZIP via `packwiz curseforge export --side client`).
4. Runs `./scripts/build-pack.sh server` → `FizzleSMP-server-X.Y.Z.zip` (drop-in Fabric server directory ZIP built by the custom download loop in `build-pack.sh`).
5. Extracts the changelog section for the new version from `CHANGELOG.md`.
6. Publishes a GitHub Release with both ZIPs and the changelog as the release body.

### Updating the live server

The server uses [packwiz-installer-bootstrap](https://github.com/packwiz/packwiz-installer-bootstrap) to diff against the published `pack.toml` and update in place — no wipe-and-recreate. Worlds, player data, `server.properties`, and the Fabric launcher are never touched.

```bash
./server-install.sh              # install/update to the latest release (via GitHub API)
./server-install.sh v1.3.0       # install/update to a specific tag (rollback path)
./server-install.sh master       # install/update to the current master branch (unreleased, for testing)
```

The installer URL pattern is `https://raw.githubusercontent.com/rfizzle/FizzleSMP/<ref>/modpack/pack.toml`, so every tag is a self-contained, pinnable release. Full operator docs are in `docs/server-deployment.md`.

### Build script split

`scripts/build-pack.sh` has two distinct code paths:

- **Client:** delegates entirely to `packwiz curseforge export --side client`. Produces the CurseForge-compatible ZIP that players import into the CurseForge launcher.
- **Server:** reads `.pw.toml` metadata directly, downloads all `server`/`both` mods into `build/server/`, copies configs, and zips the result as a drop-in Fabric server directory. Does **not** include the Fabric server jar, `eula.txt`, or `server.properties` — those are managed separately on the host.

Both paths read the version from `modpack/pack.toml` and embed it in the output filename.

## Custom Commands

| Command | Purpose |
|---------|---------|
| `/review-plugins` | Reads all `plugins/*.md` files, prints a summary table of every mod (name, category, conflicts), and flags any issues (missing fields, duplicates across categories). |
| `/check-conflicts` | Reads the plugin lists and compatibility matrix, cross-references all mods, and reports potential conflicts — both known (from the matrix) and suspected (overlapping functionality). |
| `/compare-sync` | Compares mods listed in `plugins/*.md` against packwiz files in `modpack/mods/` and `modpack/config/paxi/datapacks/`. Reports synced, missing, and extra mods. |
| `/add-mods <mod names>` | Verifies 1.21.1 Fabric compatibility, resolves dependencies, checks for conflicts, and adds the mod to the correct category file. Suggests alternatives if incompatible. |
| `/analyze-log <path or pasted log>` | Parses a Minecraft log file (or pasted log content), maps errors/warnings to mods in the pack, diagnoses root causes, and recommends fixes. Updates testing checklist and compatibility matrix as needed. |

## Workflow

1. **Brainstorm** — Add feature ideas to `docs/features-brainstorm.md`.
2. **Research** — Find mods that fulfill those features; verify compatibility.
3. **Evaluate** — Run `/check-conflicts` to surface issues. Update `docs/compatibility-matrix.md`.
4. **Add** — Add the mod to the appropriate `plugins/*.md` file via `/add-mods` or manually.
5. **Build** — Run `./scripts/sync-packwiz.sh` to sync packwiz, then `./scripts/build-pack.sh server` and `./scripts/build-pack.sh client` to build distributable packs.

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
  - `packwiz` — Changes to `modpack/pack.toml`, `modpack/index.toml`, or `modpack/mods/*.pw.toml`
- **short summary** — Imperative, lowercase, no period at the end

### Examples

```
feat(plugins): add Lithium to performance mods
fix(compat): correct hard conflict between Create and Sodium
docs(brainstorm): add custom enchantments feature idea
chore(packwiz): sync mods via sync-packwiz.sh
feat(combat): add Better Combat
refactor(plugins): split utility mods into utility and admin
```

### Rules

- Keep the summary line under 72 characters.
- Use the imperative mood ("add", not "added" or "adds").
- Stage only the files relevant to the change — avoid catch-all `git add .`.
- One logical change per commit. If adding a mod touches `plugins/`, `docs/compatibility-matrix.md`, and `modpack/mods/*.pw.toml`, commit them together as one `feat` commit.

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
- The `Side` field in plugin files is the source of truth for pack distribution. When syncing to packwiz, the side value propagates to the pw.toml `side` field.