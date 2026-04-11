# FizzleSMP Server Deployment

How to install, update, and roll back the FizzleSMP server on a Linux host.

## Overview

Server updates use [packwiz-installer-bootstrap](https://github.com/packwiz/packwiz-installer-bootstrap),
which diffs the server's current mods/configs against the published `pack.toml`
and downloads only what changed. Mods dropped from the pack are removed
automatically. World files, player data, `server.properties`, `ops.json`,
`whitelist.json`, and the Fabric server launcher are never touched.

The server is pinned to a git tag (e.g. `v1.2.0`), so updates are deterministic
and rollback is just "install the previous tag".

## Prerequisites

- Linux host (tested on Ubuntu/Debian)
- Java 21+ installed (`apt install openjdk-21-jre-headless`)
- A Fabric server jar installed separately (the pack ships mods and configs,
  not the server launcher itself)
- Outbound HTTPS access to `raw.githubusercontent.com`, `api.github.com`,
  `mediafilez.forgecdn.net`, `cdn.modrinth.com`

## Fresh install

Assuming you already have a working Fabric server directory (with
`server.properties`, `eula.txt`, and a Fabric launcher jar):

```bash
cd /path/to/your/fabric-server

# Download the install script
curl -fsSLO https://raw.githubusercontent.com/rfizzle/FizzleSMP/master/scripts/server-install.sh
chmod +x server-install.sh

# Install the latest release
./server-install.sh
```

The installer downloads `packwiz-installer-bootstrap.jar` on first run (cached
next to the script) and populates `mods/`, `config/`, and `shaderpacks/` under
the current directory.

## Updating an existing server

Same command as install:

```bash
# Stop the server first
systemctl stop fizzlesmp  # or however you run it

# Update to the latest release
./server-install.sh

# ...or pin to a specific version
./server-install.sh v1.3.0

# Start the server
systemctl start fizzlesmp
```

The installer reads `.packwiz-installer.json` in the current directory to track
which files it manages, so it only downloads changed mods and removes mods no
longer in the pack.

### Testing an unreleased version

You can point at the `master` branch to install whatever is currently merged:

```bash
./server-install.sh master
```

This is useful for smoke-testing a change before cutting a release. Don't use
`master` on the real server — it pulls from a moving target.

## Rolling back

Every release is a git tag. To roll back, just install the previous tag:

```bash
./server-install.sh v1.2.0  # roll back from v1.3.0 → v1.2.0
```

Note: rollback only affects mods and configs. If v1.3.0 included mods that
altered the world (new worldgen, new dimensions, added blocks/items that
players interacted with), rolling back can leave the world in an inconsistent
state. Treat worldgen-adding minor/major releases as one-way migrations.

## What the installer manages

**Managed (will be created/updated/removed):**

- `mods/*.jar` — everything listed in `modpack/mods/*.pw.toml`
- `config/*` — everything committed under `modpack/config/` in the repo
- `shaderpacks/*` — shaderpacks listed in packwiz (typically client-only, so
  usually empty on server)
- Paxi datapacks at `config/paxi/datapacks/`

**Not managed (preserved across updates):**

- `world/` and any world backups
- `server.properties`, `eula.txt`, `ops.json`, `whitelist.json`, `banned-*.json`
- The Fabric server launcher jar and any `run.sh` / systemd units
- `logs/`
- `.packwiz-installer.json` (tracking file used by the installer itself)
- Anything else you drop in manually

**Warning:** if you have hand-tweaked any config under `config/` that is also
tracked in `modpack/config/`, the installer will overwrite your tweak on the
next update. If you want to keep a tweak, commit it upstream in the repo so it
becomes part of the pack.

## Manual install (without the script)

If you'd rather run packwiz-installer directly:

```bash
curl -fsSLO https://github.com/packwiz/packwiz-installer-bootstrap/releases/latest/download/packwiz-installer-bootstrap.jar

java -jar packwiz-installer-bootstrap.jar -g -s server \
    https://raw.githubusercontent.com/rfizzle/FizzleSMP/v1.2.0/modpack/pack.toml
```

Flags:

- `-g` — headless mode, no GUI (required on servers)
- `-s server` — only install mods marked `side = "server"` or `side = "both"`

## Release artifacts (fallback path)

Every tag also publishes a pre-built server ZIP via GitHub Releases:

- `FizzleSMP-server-X.Y.Z.zip` — mods/, config/, shaderpacks/ for the server
- `FizzleSMP-client-X.Y.Z.zip` — CurseForge-compatible modpack ZIP for clients

The ZIPs are a fallback; `server-install.sh` is the recommended path because it
does in-place diffing. The server ZIP is useful for:

- First-time bulk install on a fresh machine
- Auditing what's in a release without running anything
- Air-gapped deployments

To use a server ZIP, unzip it into your Fabric server directory.

## Troubleshooting

**`Error: Failed to download file`** — transient CDN issue. Re-run. The
installer resumes from where it left off.

**`Warning: Your pack is older than the installed one`** — you're installing
an older tag over a newer one. This is intentional for rollback; confirm yes.

**The installer replaced my hand-tweaked config** — expected. Commit the tweak
upstream in the repo so it ships with the pack.

**A mod file is stuck in `mods/`** — delete `.packwiz-installer.json` and
re-run the installer to force a full reinstall. Back up `mods/` first if you
have any non-packwiz mods side-loaded.
